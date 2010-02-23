package com.ecommerce.clients;

import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.Domain;
import com.ecommerce.dasient.model.WebServer;
import com.ecommerce.utils.DasientIntegrationConstants;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.jms.Queue;
import org.apache.log4j.Logger;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.jboss.ejb3.annotation.LocalBinding;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;
import org.postgresql.xa.PGXADataSource;

@Stateless
@LocalBinding(jndiBinding="DasientIntegration/HsphereSyncBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class HsphereSyncBean implements HsphereSyncLocal {

    private static final Logger logger = Logger.getLogger(HsphereSyncBean.class);

    @Resource
    private SessionContext sessionContext;

    @Resource(mappedName = "java:/TransactionManager")
    private TransactionManager txManager;

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = DasientIntegrationConstants.INITDOMAIN_QUEUE)
    private Queue initDomainQueue;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncControlPanel(int controlPanelId) {
        logger.debug(String.format("Sync Job for H-Sphere CP%d started", controlPanelId));

        try {
            ControlPanel controlPanel = dasientEM.find(ControlPanel.class, controlPanelId);

            if (controlPanel == null) {
                throw new NoResultException(String.format(
                        "No ControlPanel entity with ID %d found",
                        controlPanelId));
            }

            // Need to use this reference for TransactionAttribute to take effect
            HsphereSyncLocal self = sessionContext.getBusinessObject(HsphereSyncLocal.class);

            self.syncWebServers(controlPanelId);

            // To keep the transactions smaller, this is split into a loop,
            // one transaction is opened for each iteration. There is a set
            // number of domains that are imported per invocation.
            boolean hasMoreDomains;
            do {
                hasMoreDomains = self.syncDomains(controlPanelId);
            } while (hasMoreDomains);

        } catch (RuntimeException exc) {
            // Should log it as ERROR here, because Quartz will log
            // a RuntimeException as INFO only.
            logger.error(exc);
            throw exc;
        }

        logger.debug(String.format("Sync Job for H-Sphere CP%d finished", controlPanelId));
    }

    private java.sql.Connection getHsphereSqlConnection(ControlPanel controlPanel, boolean transacted) throws SQLException {
        String jdbcHost = controlPanel.getDbHost();
        String jdbcUser = System.getProperty("dasient.hs_db_user", "dasient");
        String jdbcPass = System.getProperty("dasient.hs_db_pass", "");

        BaseDataSource dataSource;

        if (transacted) {
            dataSource = new PGXADataSource();
        } else {
            dataSource = new PGSimpleDataSource();
        }

        dataSource.setServerName(jdbcHost);
        dataSource.setDatabaseName("hsphere");
        dataSource.setUser(jdbcUser);
        dataSource.setPassword(jdbcPass);

        // Timeouts are in seconds
        dataSource.setSocketTimeout(20);
        dataSource.setLoginTimeout(20);
        dataSource.setTcpKeepAlive(true);

        java.sql.Connection conn;

        if (transacted) {
            javax.sql.XAConnection xaConn = ((PGXADataSource) dataSource).getXAConnection();

            try {
                txManager.getTransaction().enlistResource(xaConn.getXAResource());
            } catch (RollbackException exc) {
                throw new EJBException(exc);
            } catch (SystemException exc) {
                throw new EJBException(exc);
            }

            conn = xaConn.getConnection();
        } else {
            conn = dataSource.getConnection();
        }

        conn.setAutoCommit(false);

        return conn;
    }

    private static final int WEB_SERVER_TYPE_ID = 1;
    private static final int WIN_SERVER_TYPE_ID = 5;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void syncWebServers(int controlPanelId) {
        ControlPanel controlPanel = dasientEM.find(ControlPanel.class, controlPanelId);

        if (controlPanel == null) {
            throw new NoResultException(String.format(
                    "No ControlPanel entity with ID %d found",
                    controlPanelId));
        }

        java.sql.Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getHsphereSqlConnection(controlPanel, false);

            stmt = conn.prepareStatement(
                    "SELECT ls.id, ls.name, ps.ip1" +
                    " FROM l_server ls" +
                    " INNER JOIN l_server_groups lsg ON lsg.id = ls.group_id" +
                    " INNER JOIN p_server ps ON ps.id = ls.p_server_id" +
                    " WHERE lsg.type_id IN (?, ?)" +
                    " ORDER BY ls.id");

            stmt.setInt(1, WEB_SERVER_TYPE_ID);
            stmt.setInt(2, WIN_SERVER_TYPE_ID);

            stmt.execute();

            rset = stmt.getResultSet();
            while (rset.next()) {
                try {
                    dasientEM.createNamedQuery("getWebServerByLogicalServerId").setParameter("controlPanel", controlPanel).setParameter("logicalServerId", rset.getInt("id")).getSingleResult();
                } catch (NoResultException exc) {
                    WebServer webServer = new WebServer();

                    webServer.setControlPanel(controlPanel);
                    webServer.setName(rset.getString("name"));
                    webServer.setLogicalServerId(rset.getInt("id"));
                    webServer.setIpAddress(rset.getString("ip1"));

                    dasientEM.persist(webServer);
                }
            }
        } catch (SQLException exc) {
            throw new EJBException(exc);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC ResultSet", exc);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC PreparedStatement", exc);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC Connection", exc);
                }
            }
        }
    }

    private static final int DOMAINS_PER_TRANSACTION = 100;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean syncDomains(int controlPanelId) {
        ControlPanel controlPanel = dasientEM.find(ControlPanel.class, controlPanelId);

        if (controlPanel == null) {
            throw new NoResultException(String.format(
                    "No ControlPanel entity with ID %d found",
                    controlPanelId));
        }

        javax.jms.Connection jmsConnection = null;
        Session jmsSession = null;
        MessageProducer initDomainProducer = null;

        java.sql.Connection sqlConn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            sqlConn = getHsphereSqlConnection(controlPanel, false);

            int lastSyncedDomainId = controlPanel.getLastSyncedDomainId();

            stmt = sqlConn.prepareStatement(
                    "SELECT d.id, d.name" +
                    " FROM domains d" +
                    " WHERE id > ?" +
                    " ORDER BY d.id" +
                    " LIMIT ?");

            stmt.setInt(1, lastSyncedDomainId);
            stmt.setInt(2, DOMAINS_PER_TRANSACTION);
            stmt.execute();

            rset = stmt.getResultSet();

            if (!rset.next()) {
                // There are no domains to sync, tell the caller to not call the method any more
                return false;
            }

            int domainCounter = 0;
			int hspherSyncJobNextScanDayIncrement = new Integer(System.getProperty("hsphere_sync_job_next_scan_day_increment")).intValue();

            do {
                Domain domain = new Domain();

                domain.setControlPanel(controlPanel);
                domain.setHsphereId(rset.getInt("id"));
                domain.setName(rset.getString("name"));
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE,hspherSyncJobNextScanDayIncrement);
				Date now = cal.getTime();
				domain.setNextScan(now);

                dasientEM.persist(domain);

                // The JMS session is opened late, because we can avoid creating one
                // if there are no domains to sync (this code would be skipped).
                if (jmsConnection == null) {
                    jmsConnection = connectionFactory.createConnection();
                    jmsSession = jmsConnection.createSession(true, Session.SESSION_TRANSACTED);
                    initDomainProducer = jmsSession.createProducer(initDomainQueue);
                }

                MapMessage msg = jmsSession.createMapMessage();
                msg.setString(DasientIntegrationConstants.MESSAGE_TYPE, DasientIntegrationConstants.INITDOMAIN_QUEUE);
                msg.setString(DasientIntegrationConstants.CONTROL_PANEL, controlPanel.getName());
                msg.setString(DasientIntegrationConstants.DOMAIN_NAME, domain.getName());

                // It's just too slow and unbearingly bogging the system down, commenting it out for now
                //initDomainProducer.send(msg);

                lastSyncedDomainId = domain.getHsphereId();

                domainCounter++;
            } while (rset.next());

            if (lastSyncedDomainId != controlPanel.getLastSyncedDomainId()) {
                controlPanel.setLastSyncedDomainId(lastSyncedDomainId);
                dasientEM.persist(controlPanel);
            }

            logger.info(String.format("%d new domains imported from H-Sphere CP%d", domainCounter, controlPanel.getId()));

            // There may be more domains to sync, tell the caller to call the method again
            return true;
        } catch (JMSException exc) {
            throw new EJBException(exc);
        } catch (SQLException exc) {
            throw new EJBException(exc);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC ResultSet", exc);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC PreparedStatement", exc);
                }
            }
            if (sqlConn != null) {
                try {
                    sqlConn.close();
                } catch (SQLException exc) {
                    logger.debug("Failed to close JDBC Connection", exc);
                }
            }
            if (initDomainProducer != null) {
                try {
                    initDomainProducer.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS MessageProducer", exc);
                }
            }
            if (jmsSession != null) {
                try {
                    jmsSession.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS Session", exc);
                }
            }
            if (jmsConnection != null) {
                try {
                    jmsConnection.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS Connection", exc);
                }
            }
        }
    }
}

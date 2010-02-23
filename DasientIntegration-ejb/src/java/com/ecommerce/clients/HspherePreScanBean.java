package com.ecommerce.clients;

import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.Domain;
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
@LocalBinding(jndiBinding = "DasientIntegration/HspherePreScanBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class HspherePreScanBean implements HspherePreScanLocal {

	private static final Logger logger = Logger.getLogger(HspherePreScanBean.class);
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

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public HspherePreScanContainer getPreScanAugmentedData(String domainName, String cpname) throws NoResultException {

		ControlPanel controlPanel = null;
		HspherePreScanContainer container = null;

		try {

			controlPanel = (ControlPanel) dasientEM.createNamedQuery("getControlPanelByName").setParameter("name", cpname).getSingleResult();

			if (controlPanel == null) {
				logger.error(String.format("No ControlPanel entity with name %s found... returning null object", cpname));
				return null;
			}
		} catch (Exception e) {

			logger.error(String.format("No ControlPanel entity with name %s found... returning null object", cpname));
			return null;
		}

		logger.debug(String.format("domain name : %s, control panel : %s, found! - dbhost = %s", domainName, controlPanel.getName(), controlPanel.getDbHost()));

		java.sql.Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;

		try {
			conn = getHsphereSqlConnection(controlPanel, false);

			stmt = conn.prepareStatement(
					"SELECT a.suspended AS suspended, ls.name AS webserver, uu.login AS username, CASE WHEN lsi.ip IS NOT NULL then TRUE ELSE FALSE END as dedicated_ip, COALESCE(lsi.ip, lsi_shared.ip) AS ipaddress, a.id AS account_id"
					+ " FROM domains d"
					+ " INNER JOIN parent_child pc ON pc.child_id = d.id"
					+ " INNER JOIN unix_user uu ON uu.id = pc.parent_id"
					+ " INNER JOIN l_server ls ON ls.id = uu.hostid"
					+ " INNER JOIN parent_child pc_ip ON pc_ip.parent_id = pc.child_id AND pc_ip.child_type = 8"
					+ " LEFT JOIN l_server_ips lsi ON lsi.r_id = pc_ip.child_id AND lsi.flag <> -1"
					+ " INNER JOIN accounts a ON a.id = pc.account_id"
					+ " INNER JOIN plan_value pv ON pv.plan_id = a.plan_id AND pv.name = 'SHARED_IP'"
					+ " LEFT JOIN l_server_ips lsi_shared ON lsi_shared.l_server_id = uu.hostid AND lsi_shared.flag = pv.value"
					+ " WHERE d.name = ?");

			stmt.setString(1, domainName);

			stmt.execute();

			rset = stmt.getResultSet();
			while (rset.next()) {
				try {
					container = new HspherePreScanContainer();
					container.setHostIp(rset.getString("ipaddress"));
					container.setWebserver(rset.getString("webserver"));
					container.setWebUsername(rset.getString("username"));
					container.setAccountId(rset.getInt("account_id"));
					container.setDedicatedIp(rset.getBoolean("dedicated_ip"));

					if (rset.getTimestamp("suspended") != null) {
						container.setSuspended(true);
					} else {
						container.setSuspended(false);
					}

				} catch (NoResultException exc) {
					// create the HspherePreScanContainer
				}
			}
		} catch (SQLException exc) {
			logger.debug("SQLException", exc);
			throw new NoResultException(exc.toString());
		} catch (Exception exc) {
			logger.debug("SQLException", exc);
			throw new NoResultException(exc.toString());
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

		return container;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void markDomainDeleted(long domainId, String domainName) throws NoResultException {

		try {

			// retrieve named domain from DB
			Domain domain = (Domain) dasientEM.createNamedQuery("getDomainById").setParameter("id", domainId).getSingleResult();

			// update the deleted value
			domain.setDeleted(true);
			domain.setStatus(null);

			// persist to the DB
			dasientEM.persist(domain);

		} catch (NoResultException exc) {

			if (domainName != null) {
				throw new NoResultException(String.format("No Domain entity with name: %d found", domainName));
			}

		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void markDomainNextScan(long domainId, String domainName, int numberOfDays) throws NoResultException {

		try {

			// retrieve named domain from DB
			Domain domain = (Domain) dasientEM.createNamedQuery("getDomainById").setParameter("id", domainId).getSingleResult();

			// update the nextScan value
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, new Integer(numberOfDays).intValue());
			Date updatedScanDate = cal.getTime();
			domain.setNextScan(updatedScanDate);
			domain.setStatus(null);

			// persist to the DB
			dasientEM.persist(domain);

		} catch (NoResultException exc) {

			if (domainName != null) {
				throw new NoResultException(String.format("No Domain entity with name: %d found", domainName));
			}

		}
	}
}

package com.ecommerce.sbs;

import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.WebServer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;
import org.postgresql.xa.PGXADataSource;

/**
 * Augments CleanDomain messages with information from H-Sphere (eg. webserver/username).
 *
 * After the message is augmented it is put into a database table that tracks
 * the pending cleaning requests.
 */

@Stateless
@LocalBinding(jndiBinding = "DasientIntegration/HsphereInterfaceBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class HsphereInterfaceBean implements HsphereInterfaceLocal {

    /**
     * Augments a CleanDomain message and puts it back into the queue.
     */

    private static final Logger logger = Logger.getLogger(HsphereInterfaceBean.class);

    //@Resource(mappedName = "java:/TransactionManager")
    //private TransactionManager txManager;

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    /**
     * Connects to H-Sphere, finds web server and username for the given domain,
     * matches it against the info in our own database and returns all info.
     *
     * @param controlPanel the control panel to search the info in
     * @param domainName the domain name to use to find the info
     */

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AugmentedInfo queryAugmentInfo(ControlPanel controlPanel, String domainName) {
        AugmentedInfo augment = new AugmentedInfo();

        java.sql.Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getHsphereSqlConnection(controlPanel, false);

            stmt = conn.prepareStatement(
                    "SELECT ls.name AS webserver, uu.login AS username, a.id AS account_id " +
                    " FROM domains d" +
                    " INNER JOIN parent_child pc ON pc.child_id = d.id" +
                    " INNER JOIN unix_user uu ON uu.id = pc.parent_id" +
                    " INNER JOIN l_server ls ON ls.id = uu.hostid" +
                    " INNER JOIN accounts a ON a.id = pc.account_id" +
                    " WHERE d.name = ?");

            stmt.setString(1, domainName);

            stmt.execute();

            rset = stmt.getResultSet();

            if (rset.next()) {
                WebServer webServer = (WebServer) dasientEM.createNamedQuery("getWebServerByName")
                        .setParameter("name", rset.getString("webserver"))
                        .getSingleResult();

                augment.controlPanel = controlPanel;
                augment.webServer = webServer;
                augment.webUsername = rset.getString("username");
                augment.accountId = rset.getInt("account_id");
            }
            else {
                throw new EJBException(String.format(
                        "Domain '%s' not found in control panel %d",
                        domainName, controlPanel.getId()));
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

        return augment;
    }

    /**
     * Establishes a JDBC connection to an H-Sphere database.
     *
     * Username and password to use for the connection are read from system
     * properties, which can be set in the application configuration file.
     *
     * @param controlPanel the H-Sphere control panel to connect to.
     * @param transacted if true the connection will be attached to the JTA transaction
     */

    private java.sql.Connection getHsphereSqlConnection(ControlPanel controlPanel, boolean transacted)
            throws SQLException {

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

            /*
            try {
                txManager.getTransaction().enlistResource(xaConn.getXAResource());
            } catch (RollbackException exc) {
                throw new EJBException(exc);
            } catch (SystemException exc) {
                throw new EJBException(exc);
            }
            */

            conn = xaConn.getConnection();
        } else {
            conn = dataSource.getConnection();
        }

        conn.setAutoCommit(false);

        return conn;
    }
}

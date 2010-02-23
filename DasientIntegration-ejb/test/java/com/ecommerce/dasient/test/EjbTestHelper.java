package com.ecommerce.dasient.test;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

public abstract class EjbTestHelper {

    public static String getJdbcDriver() {
        return System.getProperty("test.jdbc.driver", "org.hsqldb.jdbcDriver");
    }

    public static String getJdbcUrl() {
        return System.getProperty("test.jdbc.url", "jdbc:hsqldb:mem:.;shutdown=true");
    }

    public static String getJdbcUsername() {
        return System.getProperty("test.jdbc.user", "sa");
    }

    public static String getJdbcPassword() {
        return System.getProperty("test.jdbc.pass", "");
    }

    public static String getHibernateDialect() {
        return System.getProperty("test.hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
    }

    public static String getDbUnitEscapePattern() {
        return System.getProperty("test.dbunit.escapePattern", "\"?\"");
    }

    public static String getDbUnitDataTypeFactory() {
        return System.getProperty("test.dbunit.dataTypeFactory", "org.dbunit.ext.hsqldb.HsqldbDataTypeFactory");
    }

    public static String getDbUnitMetaDataHandler() {
        return System.getProperty("test.dbunit.metaDataHandler", "org.dbunit.database.DefaultMetadataHandler");
    }

    public static InitialContext createContainer(Class testClass)
            throws NamingException, DataSetException, MalformedURLException,
            SQLException, DatabaseUnitException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        Properties p = new Properties();

        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

        p.put("openejb.nobanner", "true");
        p.put("openejb.home", new File("test/openejb").getAbsolutePath());
        p.put("openejb.embedded.initialcontext.close", "destroy");
        p.put("openejb.jndiname.format", "{ejbName}/{interfaceType.annotationName}");
        p.put("openejb.vendor.config", "jboss");
        p.put("openejb.altdd.prefix", testClass.getSimpleName());

        p.put("dataSource", "new://Resource?type=DataSource");
        p.put("dataSource.JdbcDriver", getJdbcDriver());
        p.put("dataSource.JdbcUrl", getJdbcUrl());
        p.put("dataSource.UserName", getJdbcUsername());
        p.put("dataSource.Password", getJdbcPassword());
        p.put("dataSource.JtaManaged", "true");

        p.put("dasientPU.hibernate.dialect", getHibernateDialect());
        p.put("dasientPU.hibernate.hbm2ddl.auto", "create");

        InitialContext ctx = new InitialContext(p);

        applyFixtures(ctx, "common", testClass.getSimpleName());

        return ctx;
    }

    public static void applyFixtures(InitialContext ctx, String... names)
            throws SQLException, DatabaseUnitException, NamingException,
            ClassNotFoundException, InstantiationException, IllegalAccessException,
            MalformedURLException {

        IDatabaseConnection conn = getConnection(ctx);

        try {
            for (String name : names) {
                File fixture = new File("test/fixtures/" + name + ".xml");

                if (fixture.exists()) {
                    DatabaseOperation.INSERT.execute(conn, loadFixture(fixture));
                }
            }
        }
        finally {
            conn.close();
        }
    }

    private static IDatabaseConnection getConnection(InitialContext ctx)
            throws NamingException, SQLException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        IDatabaseConnection conn = new DatabaseDataSourceConnection(
                ctx, "openejb:Resource/dataSource");

        conn.getConfig().setProperty(
                "http://www.dbunit.org/properties/escapePattern",
                getDbUnitEscapePattern());

        conn.getConfig().setProperty(
                "http://www.dbunit.org/properties/datatypeFactory",
                Class.forName(getDbUnitDataTypeFactory()).newInstance());

        conn.getConfig().setProperty(
                "http://www.dbunit.org/properties/metadataHandler",
                Class.forName(getDbUnitMetaDataHandler()).newInstance());

        return conn;
    }

    private static IDataSet loadFixture(File fixture) throws MalformedURLException, DataSetException {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

        builder.setDtdMetadata(true);

        return builder.build(fixture);
    }

    public static void destroyContainer(InitialContext ctx) throws NamingException {
        if (ctx != null) {
            ctx.close();
        }
    }

    public static interface Work {
        void doWork() throws Exception;
    }

    public static void withinTransaction(InitialContext ctx, Work callee) throws Exception {
        UserTransaction ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");

        ut.begin();

        try {
            callee.doWork();

            if (ut.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                ut.rollback();
            }
            else {
                ut.commit();
            }
        }
        catch (Exception exc) {
            ut.rollback();
            throw exc;
        }
    }

    private static ConnectionFactory lookupConnectionFactory(InitialContext ctx) throws NamingException {
        return (ConnectionFactory) ctx.lookup("openejb:Resource/Default JMS Connection Factory");
    }

    private static Queue lookupQueue(InitialContext ctx, String queueName) throws NamingException {
        return (Queue) ctx.lookup("openejb:Resource/" + queueName);
    }

    public static <T extends Message> List<T> browseMessages(InitialContext ctx, String queueName, String messageSelector, Class<T> messageClass)
            throws NamingException, JMSException {

        ConnectionFactory connectionFactory = lookupConnectionFactory(ctx);
        Queue queue = lookupQueue(ctx, queueName);

        Connection connection = null;
        Session session = null;
        QueueBrowser browser = null;

        try {
            connection = connectionFactory.createConnection();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            browser = session.createBrowser(queue, messageSelector);

            connection.start();

            Enumeration messageEnum = browser.getEnumeration();

            List<T> messages = new ArrayList<T>();

            while (messageEnum.hasMoreElements()) {
                Object msg = messageEnum.nextElement();

                if (messageClass.isInstance(msg)) {
                    messages.add(messageClass.cast(msg));
                }
            }

            return messages;
        } finally {
            if (browser != null) {
                browser.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static <T extends Message> List<T> consumeMessages(InitialContext ctx, String queueName, String messageSelector, Class<T> messageClass)
            throws NamingException, JMSException {

        ConnectionFactory connectionFactory = lookupConnectionFactory(ctx);
        Queue queue = lookupQueue(ctx, queueName);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            connection = connectionFactory.createConnection();

            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            consumer = session.createConsumer(queue, messageSelector);

            connection.start();

            List<T> messages = new ArrayList<T>();

            for (Message msg = consumer.receiveNoWait(); msg != null;) {
                if (messageClass.isInstance(msg)) {
                    messages.add(messageClass.cast(msg));
                    msg.acknowledge();
                }
            }

            return messages;
        } finally {
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static interface MessageCreator<T extends Message> {
        T createMessage(Session session);
    }

    public static <T extends Message> void produceMessage(InitialContext ctx, String queueName, MessageCreator<T> messageCreator)
            throws NamingException, JMSException {

        ConnectionFactory connectionFactory = lookupConnectionFactory(ctx);
        Queue queue = lookupQueue(ctx, queueName);

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = connectionFactory.createConnection();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            producer = session.createProducer(queue);

            Message message = messageCreator.createMessage(session);

            producer.send(message);
        } finally {
            if (producer != null) {
                producer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}

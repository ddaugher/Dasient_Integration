package com.ecommerce.mdbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author djdaugherty
 */
public abstract class ConsumerMDBBean implements MessageListener {

	static Logger logger = Logger.getLogger(ConsumerMDBBean.class);

	@PersistenceContext(unitName = "DasientPU")
    public EntityManager dasientEM;

	@Resource
	public MessageDrivenContext mdc;

	public String beanName;

	public abstract void onChildMessage(MapMessage message) throws Exception;

	public ConsumerMDBBean() {
	}

	public void onMessage(Message message) {

		logger.info("-------------------------------------------------------------------------");
		logger.info("   " + getBeanName() + " onMessage() initiated... ");
		logger.info("-------------------------------------------------------------------------");

		// check to make sure the incoming message is an MapMessage
		if (checkMessage(message)) {

			try {

				MapMessage mapMessage = (MapMessage) message;
				mapMessage.acknowledge();

				// call the extended method to handle the message
				onChildMessage(mapMessage);

			} catch (JMSException e) {
				e.printStackTrace();
				//mdc.setRollbackOnly();  I do not think we want to keep an invalid message... may need to move to another queue
			} catch (Throwable te) {
				te.printStackTrace();
			}
		} else {
			try {
				logger.error("retrieving non-MapMessage : " + message.getJMSCorrelationID());
			} catch (Exception e) {
				logger.error("Exception encountered", e);
			}
		}

	}

	protected boolean checkMessage(Message m) {
		if (m instanceof MapMessage) {
			logger.info("retrieving MapMessage");
			return true;
		} else if (m instanceof ObjectMessage) {
			logger.info("retrieving non-MapMessage (ObjectMessage)");
			return false;
		} else if (m instanceof TextMessage) {
			logger.info("retrieving non-MapMessage (TextMessage)");
			return false;
		} else {
			logger.error("retrieving non-MapMessage - ERROR!");
			return false;
		}
	}

	protected void sendMapMessage(HashMap<String, Object> map, String dest, String messageID, long timeToLive) throws Exception {
		InitialContext ic = null;
		Connection connection = null;
		Session session = null;

		try {
			ic = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			Queue queue = (Queue) ic.lookup(dest);
			logger.debug("Queue " + dest + " exists");

			connection = cf.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(queue);

			// if incoming timeToLive value greater than 0... set to incoming value, otherwise
			// set timetoLive to Message.DEFAULT_TIME_TO_LIVE (never expire)
			if ( timeToLive > 0 ) {
				sender.setTimeToLive(timeToLive);
			} else {
				sender.setTimeToLive(Message.DEFAULT_TIME_TO_LIVE);
			}

			MapMessage message = createMapMessageFromMap(map, session);
			message.setJMSCorrelationID(messageID);
			sender.send(message);
			sender.close();
			logger.debug("The message was successfully sent to the " + queue.getQueueName() + " queue");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The Message Driven Bean failed!");
		} finally {
			if (ic != null) {
				try {
					ic.close();
				} catch (Exception e) {
					throw e;
				}
			}

			closeConnection(connection);
		}
	}

		/**
	 * Create a JMS MapMessage for the given Map.
	 * @param map the Map to convert
	 * @param session current JMS session
	 * @return the resulting message
	 * @throws JMSException if thrown by JMS methods
	 * @see javax.jms.Session#createMapMessage
	 */
	protected MapMessage createMapMessageFromMap(HashMap<String, Object> map, Session session) throws JMSException {
		MapMessage message = session.createMapMessage();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (!(entry.getKey() instanceof String)) {
				logger.debug("Cannot convert non-String key of type [" + entry.getKey() + "] to JMS MapMessage entry");
			}

			try {
				message.setObject((String) entry.getKey(), entry.getValue());
			} catch (IllegalArgumentException i) {
				// this exception will occur when - 'if the name is null or if the name is an empty string.'
				// ignoring the exception because we do not care

				//logger.debug(entry.getKey());
				//logger.debug(entry.getValue());
			} catch (MessageFormatException mf) {
				// a MessageFormatExecption indicates the object trying to be set is either null or invalid...
				// ignoring the exception because we do not care

				//logger.debug(entry.getKey());
				//logger.debug(entry.getValue());
			}
		}
		return message;
	}

	protected void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (JMSException jmse) {
			logger.debug("Could not close connection " + con + " exception was " + jmse);
		}
	}

	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}
}

package com.ecommerce.sbs;

import com.ecommerce.dasient.exceptions.UnableToProcessDasientResultException;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import com.ecommerce.dasient.vo.DasientAck;
import com.ecommerce.dasient.vo.DasientResult;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;

@Stateless
@LocalBinding(jndiBinding="DasientIntegration/DasientResultBrokerBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DasientResultBrokerBean implements DasientResultBrokerLocal {

    private final static Logger logger = Logger.getLogger(DasientResultBrokerBean.class);

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = DasientIntegrationConstants.DASIENTACK_QUEUE)
    private Queue dasientAckQueue;

    @Resource(mappedName = DasientIntegrationConstants.DASIENTRESULT_QUEUE)
    private Queue dasientResultQueue;

    /**
     * @param result
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void submitResult(String requestId, String jsonString, boolean isInfected) throws UnableToProcessDasientResultException {

		// the ack and result are married together by the use of the message selector
        String msgSelector = String.format("JMSCorrelationID = '%s'", requestId);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        MessageProducer producer = null;

		DasientAck ackMsg = null;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            consumer = session.createConsumer(dasientAckQueue, msgSelector);

            connection.start();

			// wait to retrieve a message matching the message selector for 10 seconds
            MapMessage message = (MapMessage) consumer.receive(10000);
            if (message == null) {
                throw new RuntimeException(String.format("No message with ID '%s' found ", msgSelector));
            }

			ackMsg = (DasientAck) EntityMapper.mapEntity(false,(HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), DasientAck.class);

			DasientResult r = new DasientResult();
			r.setDomainName(ackMsg.getDomainName());
			r.setDomainId(ackMsg.getDomainId());
			r.setControlPanel(ackMsg.getControlPanel());
			r.setHostIP(ackMsg.getHostIP());
			r.setMessageType(DasientIntegrationConstants.DASIENT_RESULT_MESSAGETYPE);
			r.setRequestId(requestId);
			r.setJsonPayload(jsonString);
			r.setRequestType(ackMsg.getRequestType());
			r.setStartTime(ackMsg.getStartTime());
			r.setWebserver(ackMsg.getWebserver());
            r.setWebUsername(ackMsg.getWebUsername());
            r.setAccountId(ackMsg.getAccountId());
			r.setDedicatedIp(ackMsg.isDedicatedIp());
			r.setIsInfected(isInfected);

            producer = session.createProducer(dasientResultQueue);
            producer.send(createMapMessageFromMap(EntityMapper.mapEntityToHashMap(false, r), session));

        } catch (JMSException exc) {
            throw new RuntimeException(String.format( "Failed to 'get' DasientAckMessage message for id : '%s'", requestId), exc);
        } catch (Exception e) {
			logger.error(e.toString());
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (Exception exc) {
                    logger.debug("Failed to close JMS MessageProducer", exc);
                }
            }
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (Exception exc) {
                    logger.debug("Failed to close JMS MessageConsumer", exc);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (Exception exc) {
                    logger.debug("Failed to close JMS Session", exc);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception exc) {
                    logger.debug("Failed to close JMS Connection", exc);
                }
            }
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

			if ( null == entry.getKey() || null == entry.getValue() ) {
				logger.info("HashMap does not allow for null key or value... skipping!");
				continue;
			}

			if (!(entry.getKey() instanceof String)) {
				logger.debug("Cannot convert non-String key of type [" + entry.getKey() + "] to JMS MapMessage entry");
			} else {
				message.setObject((String) entry.getKey(), entry.getValue());
			}
		}
		return message;
	}
}
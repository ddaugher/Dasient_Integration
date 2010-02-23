package com.ecommerce.clients;

import com.ecommerce.dasient.model.Domain;
import com.ecommerce.utils.DasientIntegrationConstants;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import javax.ejb.MessageDriven;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.MapMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 * The Processor is a Quartz scheduled Job responsible for the management of the Dasient integration process.
 * The Processor is triggered by Quartz, looks into the DB to determine the total number of requests submitted
 * today, the total number allowed and then submits new requests to the queue based on a max batch size.
 *
 * @author djdaugherty
 */
@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "cronTrigger", propertyValue = DasientIntegrationConstants.PROCESSOR_CRON_SCHEDULE)})
@ResourceAdapter("quartz-ra.rar")
public class Processor implements Job {

	@PersistenceContext(unitName = "DasientPU")
	private EntityManager dasientEM;
	static Logger logger = Logger.getLogger(Processor.class);
	static boolean running = false;

	private synchronized boolean isRunning() {
		return Processor.running;
	}

	/**
	 * execute method extended from the <code>Job</code> class
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		if (!isRunning()) {
			logger.debug("Processor NOT running... starting new instance... calling businessLogic()");
			businessLogic();
		}

	}

	private void businessLogic() {

		// flip the flag to indicate the processor is running
		running = true;

		logger.debug("-------------------------------------------------------------------------");
		logger.info("   Processor job initiated... ");
		logger.debug("-------------------------------------------------------------------------");
		InitialContext ic = null;
		ConnectionFactory cf = null;
		Connection connection = null;
		String destinationName = DasientIntegrationConstants.SCANDOMAIN_QUEUE;

		try {
			ic = new InitialContext();

			cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			Queue queue = (Queue) ic.lookup(destinationName);
			logger.info("Queue " + destinationName + " exists");

			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(queue);

			logger.info("Determining if max requests have been reached... ");
			int domainCount = getScannedHistoryCount();
			logger.info("current count of domains scanned today : " + domainCount);

			int maxDailyDasientRequests = Integer.getInteger("max_daily_dasient_requests", 1000).intValue();
			logger.info("current maxDailyDasientRequests setting : " + maxDailyDasientRequests);

			int maxRequestsPerBatch = Integer.getInteger("max_requests_per_batch", 25).intValue();
			logger.info("current maxRequestsPerBatch setting : " + maxRequestsPerBatch);
			int batchSize = 0;

			int scansRemaining = maxDailyDasientRequests - domainCount;
			logger.info("current number of scans remaining : " + scansRemaining);

			if (scansRemaining <= 0) {
				batchSize = 0;
			} else if (scansRemaining >= maxRequestsPerBatch) {
				batchSize = maxRequestsPerBatch;
			} else {
				batchSize = scansRemaining;
			}
			logger.info("setting batch size to : " + batchSize);

			if (batchSize > 0) {

				logger.info("Checking 'Domains' to process...");
				Collection<Domain> domains = getDomainsForScanning(batchSize);
				logger.info(String.format("%s Domains returned from DB", domains.size()));

				for (Domain domain : domains) {

					domain.setStatus("SCANNING");
					dasientEM.persist(domain);

					logger.debug("creating SCAN_DOMAIN message...");
					MapMessage msg = session.createMapMessage();
					msg.setString(DasientIntegrationConstants.MESSAGE_TYPE, DasientIntegrationConstants.SCAN_DOMAIN_MESSAGETYPE);
					msg.setString(DasientIntegrationConstants.DOMAIN_NAME, domain.getName());
					msg.setString(DasientIntegrationConstants.CONTROL_PANEL, domain.getControlPanel().getName());
					msg.setLong(DasientIntegrationConstants.DOMAIN_ID, domain.getId());

					logger.info("Adding domain - " + msg.getString(DasientIntegrationConstants.DOMAIN_NAME) + " to work queue");

					// send message
					sender.send(msg);
					logger.info("The message was successfully sent to the " + queue.getQueueName() + " queue");
				}

				displayProviderInfo(connection.getMetaData());

			} else {
				logger.info("Max Daily Dasient Requests reached... ");
			}


		} catch (JMSException ex) {
			logger.warn(Processor.class.getName(), ex);
		} catch (NamingException ex) {
			logger.warn(Processor.class.getName(), ex);
		} catch (Exception ex) {
			logger.warn(Processor.class.getName(), ex);
		} finally {

			// flip the flag to indicate the processor is not running
			running = false;

			if (ic != null) {
				try {
					ic.close();
				} catch (Exception e) {
					//throw e;
				}
			}

			closeConnection(connection);
		}
	}

	private void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (JMSException jmse) {
			logger.debug("Could not close connection " + con + " exception was " + jmse);
		}
	}

	/**
	 *
	 * @param metaData
	 * @throws Exception
	 */
	protected void displayProviderInfo(ConnectionMetaData metaData) throws Exception {
		String info =
				"The processor connected to " + metaData.getJMSProviderName()
				+ " version " + metaData.getProviderVersion() + " ("
				+ metaData.getProviderMajorVersion() + "." + metaData.getProviderMinorVersion()
				+ ")";

		logger.info(info);
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private Collection<Domain> getDomainsForScanning(int maxResults) throws NoResultException {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// add 1 day to the current date (today)
		cal.add(Calendar.DATE, 1);

		Date today = cal.getTime();

		Collection<Domain> domains;

		try {

			logger.debug(String.format("date value : %s", today.toString()));
			Query q = dasientEM.createNamedQuery("getDomainsForScanning").setParameter("date", today, TemporalType.DATE);
			q.setMaxResults(maxResults);
			domains = q.getResultList();

		} catch (NoResultException exc) {
			throw new NoResultException("No Domains found!");
		}

		return domains;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryCount() throws NoResultException {

		Date fromDate = null;
		Date toDate = null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		fromDate = cal.getTime();

		cal.add(Calendar.DATE, 1);
		toDate = cal.getTime();

		logger.debug(String.format("from date value : %s", fromDate.toString()));
		logger.debug(String.format("to date value : %s", toDate.toString()));

		int domainCount = 0;

		try {

			Query q = dasientEM.createNamedQuery("getScannedHistoryCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");

		}
		return domainCount;
	}
}
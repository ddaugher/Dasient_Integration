package com.ecommerce.clients;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.MailUtils;
import java.util.Calendar;
import java.util.Date;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import javax.ejb.MessageDriven;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 * The StatusReport is a Quartz scheduled Job responsible for the heartbeat status report of the Dasient integration process.
 * The StatusReport is triggered by Quartz, looks into the DB to determine the total number of requests submitted
 * today, the total number allowed and then sends email notification to mailing list.
 *
 * @author djdaugherty
 */
@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "cronTrigger", propertyValue = DasientIntegrationConstants.STATUS_REPORT_CRON_SCHEDULE)})
@ResourceAdapter("quartz-ra.rar")
public class StatusReport implements Job {

	@PersistenceContext(unitName = "DasientPU")
	private EntityManager dasientEM;
	static Logger logger = Logger.getLogger(StatusReport.class);
	static boolean running = false;

	private synchronized boolean isRunning() {
		return StatusReport.running;
	}

	/**
	 * execute method extended from the <code>Job</code> class
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		if (!isRunning()) {
			logger.debug("StatusReport NOT running... starting new instance... calling businessLogic()");
			businessLogic();
		}

	}

	private void businessLogic() {

		// flip the flag to indicate the StatusReport is running
		running = true;

		logger.debug("-------------------------------------------------------------------------");
		logger.info("   StatusReport job initiated... ");
		logger.debug("-------------------------------------------------------------------------");

		try {

			StringBuffer messageBody = new StringBuffer();
			messageBody.append("Dasient Heartbeat Status - " + Calendar.getInstance().getTime().toString() + "\n\n");

			int domainCount = getScannedHistoryCount();
			messageBody.append("Current count of domains scanned today - " + domainCount + "\n");

			int maxDailyDasientRequests = Integer.getInteger("max_daily_dasient_requests", 1000).intValue();
			messageBody.append("Current maxDailyDasientRequests setting - " + maxDailyDasientRequests + "\n");

			int maxRequestsPerBatch = Integer.getInteger("max_requests_per_batch", 25).intValue();
			messageBody.append("Current maxRequestsPerBatch setting - " + maxRequestsPerBatch + "\n");
			int batchSize = 0;

			int scansRemaining = maxDailyDasientRequests - domainCount;
			messageBody.append("Current number of scans remaining - " + scansRemaining + "\n\n");

			int maliciousCount = getScannedHistoryMaliciousCount();
			messageBody.append("Current number of malicious scans - " + maliciousCount + "\n");

			int suspiciousCount = getScannedHistorySuspiciousCount();
			messageBody.append("Current number of suspicious scans - " + suspiciousCount + "\n");

			int zeroLengthCount = getScannedHistoryZeroLengthCount();
			messageBody.append("Current number of zero length scans - " + zeroLengthCount + "\n");

			int ignoredCount = getScannedHistoryIgnoredCount();
			messageBody.append("Current number of ignored scans - " + ignoredCount + "\n");

			int expiredCount = getScannedHistoryExpiredCount();
			messageBody.append("Current number of expired scans - " + expiredCount + "\n");

			int requestAckCount = getScannedHistoryRequestAckCount();
			messageBody.append("Current number of request ack scans - " + requestAckCount + "\n");

			int completedCount = getScannedHistoryCompletedCount();
			messageBody.append("Current number of completed scans - " + completedCount + "\n\n");

			int failedCount = getScannedHistoryFailedCount();
			messageBody.append("Current number of failed scans - " + failedCount + "\n\n");

			if (scansRemaining <= 0) {
				batchSize = 0;
			} else if (scansRemaining >= maxRequestsPerBatch) {
				batchSize = maxRequestsPerBatch;
			} else {
				batchSize = maxRequestsPerBatch - scansRemaining;
			}
			messageBody.append("Batch size set to value - " + batchSize + "\n");

			MailUtils.sendEmail("Dasient Integration - Heartbeat Status Report", messageBody.toString());

		} catch (NamingException ex) {
			logger.warn(StatusReport.class.getName(), ex);
		} catch (Exception ex) {
			logger.warn(StatusReport.class.getName(), ex);
		} finally {

			// flip the flag to indicate the processor is not running
			running = false;
		}
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
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryMaliciousCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryMaliciousCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistorySuspiciousCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistorySuspiciousCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryZeroLengthCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryZeroLengthCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryRequestAckCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryRequestAckCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryIgnoredCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryIgnoredCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryCompletedCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryCompletedCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryExpiredCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryExpiredCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int getScannedHistoryFailedCount() throws NoResultException {

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

			Query q = dasientEM.createNamedQuery("getScannedHistoryFailedCount");
			q.setParameter("fromdate", fromDate, TemporalType.DATE);
			q.setParameter("todate", toDate, TemporalType.DATE);
			domainCount = ((Long) q.getSingleResult()).intValue();

		} catch (NoResultException exc) {

			logger.info("No ScanHistory records found!");
			domainCount = 0;

		}
		return domainCount;
	}
}
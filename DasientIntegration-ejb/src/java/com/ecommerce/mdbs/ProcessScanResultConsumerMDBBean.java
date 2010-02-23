package com.ecommerce.mdbs;

import com.ecommerce.clients.ProcessScanResultLocal;
import com.ecommerce.dasient.vo.ProcessScanResult;
import com.ecommerce.sbs.CleanBrokerLocal;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import com.ecommerce.utils.MailUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "ProcessScanResultConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.PROCESSSCANRESULT_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "ProcessScanResultConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "ProcessScanResultConsumerMDBBean")
}, messageListenerInterface = MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ProcessScanResultConsumerMDBBean extends ConsumerMDBBean {

	@EJB(mappedName = "DasientIntegration/ProcessScanResultBean/local")
	private ProcessScanResultLocal processScanResultBean;
	@EJB(mappedName = "DasientIntegration/CleanBrokerBean/local")
	private CleanBrokerLocal cleanBroker;

	public ProcessScanResultConsumerMDBBean() {
		setBeanName(ProcessScanResultConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		ProcessScanResult r = (ProcessScanResult) EntityMapper.mapEntity(false, (HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), ProcessScanResult.class);

		logger.debug("A Message received in ProcessScanResultConsumerMDBBean: (request_id)" + r.getRequestId());

		String infectionWithinLastN1Scans = System.getProperty("infection_within_last_n1_scans", "5");
		String infectionWithinLastN3Scans = System.getProperty("infection_within_last_n3_scans", "30");
		String lessThanN0Scans = System.getProperty("less_than_n0_scans", "5");

		// perform post scan operations

		// is scan rejected?
		if (null == r.getRequestId() || r.getRequestId().isEmpty()) {

			// update the next scan date
			processRejected(r);

			// if domain infected... schedule next scan tomorrow
		} else if (r.isInfected()) {
			processInfected(r);

			// if the previous scan was rejected... continue post-scan opertations
			logger.info("checking if previous scan for " + r.getDomainName() + " was rejected (IGNORED)");
			if (processScanResultBean.hasBeenRejectedWithinLastScans(r.getDomainName(), 2)) {

				logger.info("previous scan for " + r.getDomainName() + " was rejected (IGNORED)");

				// if domain has been rejected all of the three previous scans
				logger.info("checking if 3 previous scans for " + r.getDomainName() + " were rejected (IGNORED)");
				if (processScanResultBean.hasRejectedScansInARow(r.getDomainName(), 4) >= 3) {

					logger.info("3 previous scans for " + r.getDomainName() + " were rejected (IGNORED)");

					// if the domain uses a dedicated IP
					logger.info("checking to determine if domain : " + r.getDomainName() + " : is using dedicated IP... ");
					if (r.isDedicatedIp()) {

						logger.info(r.getDomainName() + " : is using dedicated IP... ");

						// send email
						logger.info("sending email " + r.getDomainName());

						MailUtils.sendEmail("Infection Alert", "test message");
					}
				}

				// else create ticket
			} else {
				// create CR ticket right here
				logger.info("[MOCK] creating CR ticket for " + r.getDomainName());
			}

			// check to determine if infected scan within last N1 scans (default=5)
		} else if (processScanResultBean.getScanHistoryInfectedCountForDomain(r.getDomainName(), new Integer(infectionWithinLastN1Scans).intValue())) {

			String scanInterval = System.getProperty("schedule_next_n2_day", "3");
			processNextScanDate(r, scanInterval);

			// check to determine if infected scan within last N3 scans (default=30)
		} else if (processScanResultBean.getScanHistoryInfectedCountForDomain(r.getDomainName(), new Integer(infectionWithinLastN3Scans).intValue())) {

			String scanInterval = System.getProperty("schedule_next_n4_day", "7");
			processNextScanDate(r, scanInterval);

			// check to determine if less than N0 scans have occurred (default=5)
		} else if (processScanResultBean.getScanHistoryCountForDomain(r.getDomainName()) <= (new Integer(lessThanN0Scans).intValue())) {

			String scanInterval = System.getProperty("rejected_less_than_n0_interval", "7");
			logger.info("less than " + lessThanN0Scans + "scans have occurred... setting next scan date : + " + scanInterval);
			processNextScanDate(r, scanInterval);

			// fall all the way through... schedule next scan = 'domain_not_infected_next_scan_interval' value
		} else {

			processAllClean(r);

        }
	}

	private void processAllClean(ProcessScanResult r) throws NumberFormatException {
		Calendar cal = Calendar.getInstance();
		String scanInterval = System.getProperty("domain_not_infected_next_scan_interval", "25");
		logger.info("domain " + r.getDomainName() + " is NOT infected... setting next scan date : + " + scanInterval);
		cal.add(Calendar.DATE, new Integer(scanInterval).intValue());
		Date updatedScanDate = cal.getTime();
		processScanResultBean.persistDomainNextScanUpdate(r.getDomainId(), r.getDomainName(), updatedScanDate);
        logger.debug("Next scan date was set");
	}

	private void processNextScanDate(ProcessScanResult r, String scanInterval) throws NumberFormatException {
		Calendar cal = Calendar.getInstance();
		logger.info("setting next scan date : + " + scanInterval);
		cal.add(Calendar.DATE, new Integer(scanInterval).intValue());
		Date updatedScanDate = cal.getTime();
		processScanResultBean.persistDomainNextScanUpdate(r.getDomainId(), r.getDomainName(), updatedScanDate);
        logger.debug("Next scan date was set");
    }

	private void processInfected(ProcessScanResult r) throws NumberFormatException, Exception {
		Calendar cal = Calendar.getInstance();
		String domainInfectedNextScanInterval = System.getProperty("domain_infected_scan_interval", "1");
		logger.info("domain " + r.getDomainName() + " is infected... setting next scan date : + " + domainInfectedNextScanInterval);
		cal.add(Calendar.DATE, new Integer(domainInfectedNextScanInterval).intValue());
		Date updatedScanDate = cal.getTime();
		processScanResultBean.persistDomainNextScanUpdate(r.getDomainId(), r.getDomainName(), updatedScanDate);
		// initiate clean of domain
		createCleanDomainMessage(r);
	}

	private void processRejected(ProcessScanResult r) throws NumberFormatException {
		logger.info("Request ID is empty... setting next scan date... ");
		String rejectedNextScanInterval = System.getProperty("rejected_next_scan_interval", "14");
		logger.info("an empty request id indicates the request was 'IGNORED' ... setting next scan date... + " + rejectedNextScanInterval);
		// an empty request id indicated an ignored request to dasient... schedule next scan
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, new Integer(rejectedNextScanInterval).intValue());
		Date updatedScanDate = cal.getTime();
		processScanResultBean.persistDomainNextScanUpdate(r.getDomainId(), r.getDomainName(), updatedScanDate);
	}

	private void createCleanDomainMessage(ProcessScanResult r) throws Exception {
		logger.debug("Creating new 'PendingClean' message");

		cleanBroker.scheduleCleanTriggeredByInfection(
				r.getControlPanel(), r.getWebserver(),
				r.getWebUsername(), r.getAccountId(),
				r.getInfectionId());
	}
}

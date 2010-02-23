package com.ecommerce.clients;

import com.ecommerce.dasient.model.IgnoredRequest;
import com.ecommerce.dasient.model.ScanHistory;
import org.apache.log4j.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.ejb3.annotation.LocalBinding;

/**
 * Statelss Session Bean responsible for managing the persistence of ScanHistory
 * entities
 * @author djdaugherty
 */
@Stateless
@LocalBinding(jndiBinding = "DasientIntegration/ScanDomainAugmentedBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ScanDomainAugmentedBean implements ScanDomainAugmentedLocal {

	private static final Logger logger = Logger.getLogger(ScanDomainAugmentedBean.class);
	@PersistenceContext(unitName = "DasientPU")
	private EntityManager dasientEM;

	/**
	 * This method will persist a ScanHistory entity for a given domain... storing the
	 * requested ip address and actual ip address
	 * @param requestedIp the requested ip address submitted to Dasient
	 * @param actualIp the actual ip address found during the dasient malware scan
	 * @param domainName the domain name submitted to the dasient api
	 * @return id id value returned from persistence call to DB
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long persistScanHistoryRequestIgnored(String requestedIp, String actualIp, String domainName, String jsonRequest) {

		ScanHistory sh = new ScanHistory();
		logger.debug("calling persistScanHistoryRequestIgnored : domainname = " + domainName);
		sh.setHostname(domainName);
		sh.setRequestId("IGNORED");
		sh.setStatus("IGNORED");
		sh.setRawRequest(jsonRequest);

		IgnoredRequest ignoredRequest = new IgnoredRequest();
		ignoredRequest.setRequestedIp(requestedIp);
		ignoredRequest.setActualIp(actualIp);
		ignoredRequest.setScanHistory(sh);

		sh.setIgnoredRequest(ignoredRequest);
		dasientEM.persist(sh);

		return sh.getId();
	}

	/**
	 *
	 * @param domainName
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long persistScanHistoryZeroLengthResponse(String domainName, String jsonRequest) {

		ScanHistory sh = new ScanHistory();
		logger.debug("calling persistScanHistoryZeroLengthResponse : domainname = " + domainName);
		sh.setHostname(domainName);
		sh.setRequestId("ZERO_LENGTH");
		sh.setStatus("ZERO_LENGTH");
		sh.setRawRequest(jsonRequest);

		dasientEM.persist(sh);

		return sh.getId();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long persistScanHistoryRequestAck(String domainName, String requestId, String statusUrl, String rawRequest) {

		ScanHistory sh = new ScanHistory();
		logger.debug("calling persistScanHistoryRequestAck : domainname = " + domainName);
		sh.setHostname(domainName);
		sh.setRequestId(requestId);
		sh.setStatus("REQUEST_ACK");
		sh.setStatusUrl(statusUrl);
		sh.setRawRequest(rawRequest);

		dasientEM.persist(sh);

		return sh.getId();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long updateScanHistoryStatusByRequestId(String requestId, String status) {

		Query q = dasientEM.createNamedQuery("getScanHistoryByRequestId").setParameter("requestId", requestId);
		ScanHistory sh = (ScanHistory) q.getSingleResult();
		logger.debug("calling updateScanHistoryStatusByRequestId : requestId = " + requestId);
		logger.debug("setting status = " + status + " : requestId = " + requestId);
		sh.setStatus(status);
		dasientEM.persist(sh);

		return sh.getId();
	}
}

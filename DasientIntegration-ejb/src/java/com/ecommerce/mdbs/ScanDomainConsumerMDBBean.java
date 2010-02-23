package com.ecommerce.mdbs;

import com.ecommerce.clients.HspherePreScanContainer;
import com.ecommerce.clients.HspherePreScanLocal;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.dasient.vo.ScanDomainAugmented;
import com.ecommerce.dasient.vo.ScanDomain;
import com.ecommerce.utils.EntityMapper;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;
import javax.persistence.NoResultException;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "ScanDomainConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.SCANDOMAIN_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "ScanDomainConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "ScanDomainConsumerMDBBean")
}, messageListenerInterface = MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ScanDomainConsumerMDBBean extends ConsumerMDBBean {

	@EJB(mappedName = "DasientIntegration/HspherePreScanBean/local")
	HspherePreScanLocal preScanBean;

	public ScanDomainConsumerMDBBean() {
		setBeanName(ScanDomainConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		ScanDomain scanDomain = (ScanDomain) EntityMapper.mapEntity(false, (HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), ScanDomain.class);
		logger.debug("A message received in ScanDomainConsumerMDBBean: " + scanDomain.getDomainName());

		// this is where we will contact H-Sphere in order to grad additional information
		logger.debug("Contacting H-Sphere to retrieve augmentation information");


		HspherePreScanContainer container = null;

		try {
			// make a request to retrieve the required augmentation data from Hsphere based on the
			// domain name and control panel values.

			logger.debug("requesting augmentation information from Hsphere");
			container = preScanBean.getPreScanAugmentedData(scanDomain.getDomainName(), scanDomain.getControlPanel());

			if (container != null) {

				if (!container.isSuspended()) {

					ScanDomainAugmented aug = new ScanDomainAugmented();
					aug.setMessageType(DasientIntegrationConstants.SCAN_DOMAIN_AUGMENTED_MESSAGETYPE);
					aug.setDomainName(scanDomain.getDomainName());
					aug.setDomainId(scanDomain.getDomainId());
					aug.setControlPanel(scanDomain.getControlPanel());
					aug.setHostIP(container.getHostIp());
					aug.setWebserver(container.getWebserver());
					aug.setWebUsername(container.getWebUsername());
					aug.setAccountId(container.getAccountId());
					aug.setDedicatedIp(container.isDedicatedIp());

					logger.debug("Sending new 'Augmented' msg to queue");
					sendMapMessage(EntityMapper.mapEntityToHashMap(false, aug), DasientIntegrationConstants.SCANDOMAINAUGMENTED_QUEUE, "", 0);
				} else {
					logger.debug("Domain belongs to a suspended account... skipping domain for infection scanning!... setting next scan to tomorrow!");
					preScanBean.markDomainNextScan(scanDomain.getDomainId(), scanDomain.getDomainName(), 1);
				}
			} else {
				logger.debug("call to HSphere returned empty container !");
				logger.debug("Marking domain : " + scanDomain.getDomainName() + " as deleted!");
				preScanBean.markDomainDeleted(scanDomain.getDomainId(), scanDomain.getDomainName());
			}

		} catch (NoResultException e) {

			// the attempt to retrieve augmentation information was unsuccessful... marking the requested domain as 'deleted'
			logger.debug("Marking domain : " + scanDomain.getDomainName() + " as deleted!");
			preScanBean.markDomainDeleted(scanDomain.getDomainId(), scanDomain.getDomainName());

		}
	}
}
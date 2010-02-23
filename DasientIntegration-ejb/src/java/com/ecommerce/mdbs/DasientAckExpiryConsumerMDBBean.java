package com.ecommerce.mdbs;

import com.ecommerce.clients.ScanDomainAugmentedLocal;
import com.ecommerce.dasient.vo.DasientAck;
import com.ecommerce.dasient.vo.ScanDomain;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import com.ecommerce.utils.MailUtils;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "DasientAckExpiryConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.DASIENTACKEXPIRY_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "DasientAckExpiryConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "DasientAckExpiryConsumerMDBBean")
}, messageListenerInterface = MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DasientAckExpiryConsumerMDBBean extends ConsumerMDBBean {

	@EJB(mappedName = "DasientIntegration/ScanDomainAugmentedBean/local")
	ScanDomainAugmentedLocal scanDomainAugmentedBean;

	public DasientAckExpiryConsumerMDBBean() {
		setBeanName(DasientAckExpiryConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		DasientAck ackMsg = (DasientAck) EntityMapper.mapEntity(false, (HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), DasientAck.class);
		logger.debug("A message received in DasientAckExpiryConsumerMDBBean: " + ackMsg.getDomainName());

		// for now... we are just going to send an email indicating an error has occurred.
		// potenitally may want to create new message / or post to servlet as response.

		// send email to distribution group
		StringBuffer messageBody = new StringBuffer();
		messageBody.append("DasientAck Message Expiry Notice \n\n");
		messageBody.append("Domain Name : " + ackMsg.getDomainName() + " ( id : " + ackMsg.getDomainId() + " )\n");
		messageBody.append("Control Panel : " + ackMsg.getControlPanel() + "\n");
		messageBody.append("Host IP: " + ackMsg.getHostIP() + "\n");
		messageBody.append("Message Type: " + ackMsg.getMessageType() + "\n");
		messageBody.append("Request ID: " + ackMsg.getRequestId() + "\n");
		messageBody.append("Request Type: " + ackMsg.getRequestType() + "\n");
		messageBody.append("Status URL: " + ackMsg.getStatusUrl() + "\n");
		messageBody.append("Webserver Name: " + ackMsg.getWebUsername() + "\n");
		MailUtils.sendEmail("DasientAck Message Expiry Notice - Dasient Integration", messageBody.toString());

		// update scanhistory in order to track expired messages
		scanDomainAugmentedBean.updateScanHistoryStatusByRequestId(ackMsg.getRequestId(), "EXPIRED");

		// requeue message back to ScanDomain for retry
		ScanDomain scanDomain = new ScanDomain();
		scanDomain.setControlPanel(ackMsg.getControlPanel());
		scanDomain.setDomainName(ackMsg.getDomainName());
		scanDomain.setDomainId(ackMsg.getDomainId());
		scanDomain.setMessageType(DasientIntegrationConstants.SCAN_DOMAIN_MESSAGETYPE);

		logger.debug("Sending requeue'd ScanDomain msg to queue (expired DasientAck msg)");
		sendMapMessage(EntityMapper.mapEntityToHashMap(false, scanDomain), DasientIntegrationConstants.SCANDOMAIN_QUEUE, "", 0);
	}
}

package com.ecommerce.mdbs;

import com.ecommerce.dasient.vo.DomainCleaned;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "DomainCleanedConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.DOMAINCLEANED_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "DomainCleanedConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "DomainCleanedConsumerMDBBean")
}, messageListenerInterface=MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DomainCleanedConsumerMDBBean extends ConsumerMDBBean {

	public DomainCleanedConsumerMDBBean() {
		setBeanName(DomainCleanedConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		DomainCleaned r = (DomainCleaned) EntityMapper.mapEntity(false,(HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), DomainCleaned.class);
		logger.debug("A Message received in DomainCleanedConsumerMDBBean: (account id)" + r.getAccountId());

		// perform post clean operations
	}
}
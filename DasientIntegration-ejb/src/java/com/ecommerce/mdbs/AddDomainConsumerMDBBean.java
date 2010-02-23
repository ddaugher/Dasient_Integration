package com.ecommerce.mdbs;

import com.ecommerce.dasient.vo.AddDomain;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "AddDomainConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.INITDOMAIN_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "AddDomainConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "AddDomainConsumerMDBBean")
	//@ActivationConfigProperty(propertyName = "dLQMaxResent", propertyValue = "10")
}, messageListenerInterface=MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AddDomainConsumerMDBBean extends ConsumerMDBBean {

	public AddDomainConsumerMDBBean() {
		setBeanName(AddDomainConsumerMDBBean.class.getSimpleName());
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void onChildMessage(MapMessage message) throws Exception {

		//AddDomain addDomain = (AddDomain) EntityMapper.mapEntity(false,(HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), AddDomain.class);
		//logger.debug("A Message received in AddDomainConsumerMDBBean: " + addDomain.getDomainName());

    }
}

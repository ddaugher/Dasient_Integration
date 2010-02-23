package com.ecommerce.mdbs;

import com.ecommerce.clients.DasientResultHandlerLocal;
import com.ecommerce.dasient.model.ScanHistory;
import com.ecommerce.dasient.vo.DasientResult;
import com.ecommerce.dasient.vo.ProcessScanResult;
import com.ecommerce.dasient.vo.result.DasientJSONResult;
import com.ecommerce.dasient.vo.result.MaliciousResultUrl;
import com.ecommerce.dasient.vo.result.SuspiciousResultUrl;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.EntityMapper;
import com.ecommerce.utils.JSONUtils;
import java.util.HashMap;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.MapMessage;
import javax.jms.MessageListener;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.JavaIdentifierTransformer;

/**
 * @author djdaugherty
 */
@MessageDriven(mappedName = "DasientResultConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.DASIENTRESULT_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "DasientResultConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "DasientResultConsumerMDBBean")
}, messageListenerInterface = MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DasientResultConsumerMDBBean extends ConsumerMDBBean {

	@EJB(mappedName = "DasientIntegration/DasientResultHandlerBean/local")
	DasientResultHandlerLocal resultHandlerBean;

	public DasientResultConsumerMDBBean() {
		setBeanName(DasientResultConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		DasientResult r = (DasientResult) EntityMapper.mapEntity(false, (HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), DasientResult.class);
		logger.debug("A Message received in DasientResultConsumerMDBBean: (request_id)" + r.getRequestId());

		// parse json payload into DasientJSONResult Bean
		DasientJSONResult result = getDasientJSONResult(r);

		// store the result information to ScanHistory entity/table
		ScanHistory history = resultHandlerBean.persistScanHistory(result, r.getJsonPayload());

		// ScanHistory persisted successfully... create ProcessScanResult message and send
		createSendProcessScanResultMessage(r, history.getId());
	}

	private void createSendProcessScanResultMessage(DasientResult r, long scanHistoryId) throws Exception {
		logger.debug("Creating new 'ProcessScan' msg");
		ProcessScanResult pro = new ProcessScanResult();
		pro.setMessageType(DasientIntegrationConstants.PROCESS_SCAN_REQUEST_MESSAGETYPE);
		pro.setStartTime(r.getStartTime());
		pro.setDomainName(r.getDomainName());
		pro.setDomainId(r.getDomainId());
		pro.setControlPanel(r.getControlPanel());
		pro.setRequestId(r.getRequestId());
		pro.setRequestType(r.getRequestType());
		pro.setWebserver(r.getWebserver());
        pro.setWebUsername(r.getWebUsername());
        pro.setAccountId(r.getAccountId());
		pro.setDedicatedIp(r.isDedicatedIp());
        pro.setInfectionId(scanHistoryId);
		pro.setIsInfected(r.isInfected());

		logger.debug("Sending new 'ProcessScan' msg to queue");
		sendMapMessage(EntityMapper.mapEntityToHashMap(false, pro), DasientIntegrationConstants.PROCESSSCANRESULT_QUEUE, r.getRequestId(), 0);
	}

	private DasientJSONResult getDasientJSONResult(DasientResult r) throws Exception {
		String jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(DasientJSONResult.class, r.getJsonPayload());
		jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(MaliciousResultUrl.class, jsonTemp);
		jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(SuspiciousResultUrl.class, jsonTemp);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJavaIdentifierTransformer(JavaIdentifierTransformer.CAMEL_CASE);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonTemp, jsonConfig);
		logger.debug("jsonObject - " + jsonObject);
		DasientJSONResult result = (DasientJSONResult) JSONObject.toBean(jsonObject, DasientJSONResult.class);
		return result;
	}
}
package com.ecommerce.mdbs;

import com.ecommerce.clients.ScanDomainAugmentedLocal;
import com.ecommerce.dasient.exceptions.UnableToDetermineDasientUrlException;
import com.ecommerce.dasient.vo.AckDasientResponse;
import com.ecommerce.dasient.vo.request.DasientJSONRequest;
import com.ecommerce.dasient.vo.request.Request;
import com.ecommerce.dasient.vo.DasientAck;
import com.ecommerce.dasient.vo.IgnoredDasientResponse;
import com.ecommerce.dasient.vo.ProcessScanResult;
import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.DasientRequest;
import com.ecommerce.dasient.vo.ScanDomainAugmented;
import com.ecommerce.dasient.vo.response.DasientJSONResponse;
import com.ecommerce.dasient.vo.result.MaliciousResultUrl;
import com.ecommerce.dasient.vo.result.SuspiciousResultUrl;
import com.ecommerce.utils.DasientUrlUtils;
import com.ecommerce.utils.EntityMapper;
import com.ecommerce.utils.JSONUtils;
import com.ecommerce.utils.MailUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author djdaugherty
 */
@MessageDriven(mappedName = "ScanDomainAugmentedConsumerMDBBean", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = DasientIntegrationConstants.SCANDOMAINAUGMENTED_QUEUE),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
	@ActivationConfigProperty(propertyName = "clientId", propertyValue = "ScanDomainAugmentedConsumerMDBBean"),
	@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "ScanDomainAugmentedConsumerMDBBean")
}, messageListenerInterface = MessageListener.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ScanDomainAugmentedConsumerMDBBean extends ConsumerMDBBean {

	@EJB(mappedName = "DasientIntegration/ScanDomainAugmentedBean/local")
	ScanDomainAugmentedLocal scanDomainAugmentedBean;

	public ScanDomainAugmentedConsumerMDBBean() {
		setBeanName(ScanDomainAugmentedConsumerMDBBean.class.getSimpleName());
	}

	@Override
	public void onChildMessage(MapMessage message) throws Exception {

		ScanDomainAugmented msg = (ScanDomainAugmented) EntityMapper.mapEntity(false, (HashMap<String, Object>) EntityMapper.extractMapFromMessage(message), ScanDomainAugmented.class);
		logger.debug("A Message received in ScanDomainAugmentedConsumerMDBBean: (domain name)" + msg.getDomainName());

		try {

			// make the call to Dasient to request the scan
			DasientContainer container = callDasient(msg);

			// need to parse the results into object, including JSON structure
			if (container.getJsonResponse().isEmpty()) {

				// TODO : do nothing at this point, but in the future, we may want to
				// suspend the MDB or send a notification to the SA group for handling.
				logger.debug("zero length result returned from Dasient... potential thrashing situation or OVER_DAILY_LIMIT!");
				handleZeroLength(msg.getDomainName(), container.getJsonRequest());

			} else {

				// parse json payload into DasientResult Bean, the response back will indicate one of the following...
				// dasient accepts the request and will return a request id... or dasient has found an IP mismatch and is ignoring the request.
				if (container.getJsonResponse().contains("request-ack")) {

					// successful acknowledgement of request to Dasient
					handleRequestAck(container, msg);
				} else {

					// if the ack = null... assume request-ignored from Dasient... the
					// returned info will be packaged within a ProcessScanResult message and the ProcessScanResultConsumer will handle the situation.
					handleRequestIgnored(container, msg);
				}
			}

		} catch (Exception e) {

			// TODO : will need to send notification in order to identify connection issues
			e.printStackTrace();
		}
	}

	private void handleZeroLength(String domainName, String jsonRequest) throws Exception {

		// send email to distribution group
		StringBuffer messageBody = new StringBuffer();
		messageBody.append("Zero Length Response \n\n");
		messageBody.append("Domain Name : " + domainName + "\n");
		messageBody.append("jsonRequest : " + jsonRequest + "\n");
		MailUtils.sendEmail("Zero Length Response - Dasient Integration", messageBody.toString());

		// persist the scanHistory entity
		long id = scanDomainAugmentedBean.persistScanHistoryZeroLengthResponse(domainName, jsonRequest);

	}

	private void handleRequestIgnored(DasientContainer container, ScanDomainAugmented msg) throws Exception {
		String jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(DasientJSONResponse.class, container.getJsonResponse());
		jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(MaliciousResultUrl.class, jsonResponse);
		jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(SuspiciousResultUrl.class, jsonResponse);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJavaIdentifierTransformer(JavaIdentifierTransformer.CAMEL_CASE);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonResponse, jsonConfig);
		logger.debug("converted jsonObject - " + jsonObject);

		// create the response
		DasientJSONResponse response = (DasientJSONResponse) JSONObject.toBean(jsonObject, DasientJSONResponse.class);
		IgnoredDasientResponse responseIgnored = new IgnoredDasientResponse();
		responseIgnored.setRequestedIp(response.getRequestIgnored().getRequestedIp());
		responseIgnored.setActualIp(response.getRequestIgnored().getActualIp());

		// persist the scanHistory entity
		String requestedIp = response.getRequestIgnored().getRequestedIp();
		String actualIp = response.getRequestIgnored().getActualIp();
		String domainName = msg.getDomainName();
		long id = scanDomainAugmentedBean.persistScanHistoryRequestIgnored(requestedIp, actualIp, domainName, container.getJsonRequest());

		// send response data to the process scan result process
		ProcessScanResult pro = new ProcessScanResult();
		pro.setMessageType(DasientIntegrationConstants.PROCESS_SCAN_RESULT_MESSAGETYPE);
		pro.setStartTime(msg.getStartTime());
		pro.setDomainName(msg.getDomainName());
		pro.setDomainId(msg.getDomainId());
		pro.setControlPanel(msg.getControlPanel());
		pro.setRequestType(msg.getRequestType());
		pro.setWebserver(msg.getWebserver());
		pro.setIgnoredResponse(responseIgnored);
		pro.setWebUsername(msg.getWebUsername());
		pro.setAccountId(msg.getAccountId());
		pro.setDedicatedIp(msg.isDedicatedIp());
		pro.setIsInfected(false);

		sendMapMessage(EntityMapper.mapEntityToHashMap(false, pro), DasientIntegrationConstants.PROCESSSCANRESULT_QUEUE, "", 0);
	}

	private void handleRequestAck(DasientContainer container, ScanDomainAugmented msg) throws Exception {
		String jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(DasientJSONResponse.class, container.getJsonResponse());
		jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(MaliciousResultUrl.class, jsonResponse);
		jsonResponse = JSONUtils.mapJsonAttrsToJavaAttrs(SuspiciousResultUrl.class, jsonResponse);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJavaIdentifierTransformer(JavaIdentifierTransformer.CAMEL_CASE);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonResponse, jsonConfig);
		logger.debug("converted jsonObject - " + jsonObject.toString());

		// need to parse json attributes back to java
		DasientJSONResponse response = (DasientJSONResponse) JSONObject.toBean(jsonObject, DasientJSONResponse.class);

		String domainName = msg.getDomainName();
		String requestId = response.getRequestAck().getRequestId();
		String statusUrl = response.getRequestAck().getStatusUrl();
		long id = scanDomainAugmentedBean.persistScanHistoryRequestAck(domainName, requestId, statusUrl, container.getJsonRequest());

		// create DasientAck in order to put ack back on the queue... actual json string to bean will need to be added when actual call works
		DasientAck ack = new DasientAck();
		ack.setMessageType(DasientIntegrationConstants.DASIENT_ACK_MESSAGETYPE);
		ack.setStartTime(msg.getStartTime());
		ack.setDomainName(msg.getDomainName());
		ack.setDomainId(msg.getDomainId());
		ack.setControlPanel(msg.getControlPanel());
		ack.setHostIP(msg.getHostIP());
		ack.setRequestType(msg.getRequestType());
		ack.setWebserver(msg.getWebserver());
		ack.setRequestId(response.getRequestAck().getRequestId());
		ack.setStatusUrl(response.getRequestAck().getStatusUrl());
		ack.setWebUsername(msg.getWebUsername());
		ack.setDedicatedIp(msg.isDedicatedIp());
		ack.setAccountId(msg.getAccountId());

		// the ack type will actually be determined by the value returned from Dasient
		ack.setResponseAckType(DasientRequest.REQUESTACK); // could also be DasientRequest.REQUESTIGNOREDj
		AckDasientResponse responseAck = new AckDasientResponse();
		responseAck.setRequestId(response.getRequestAck().getRequestId());
		responseAck.setStatusUrl(response.getRequestAck().getStatusUrl());
		ack.setResponseAck(responseAck);

		// the value 'request-id' will need to be changed when the actual Dasient call is in place.
		HashMap<String, Object> m = EntityMapper.mapEntityToHashMap(false, ack);
		long timeToLive = Long.getLong("dasientAck_timetolive", 300000);
		sendMapMessage(m, DasientIntegrationConstants.DASIENTACK_QUEUE, response.getRequestAck().getRequestId(), timeToLive);
	}

	private DasientContainer callDasient(ScanDomainAugmented msg) throws UnableToDetermineDasientUrlException, ParseException, Exception, IOException {
		logger.debug("Contacting Dasient with domain scan request.");
		HttpClient httpclient = new DefaultHttpClient();
		String dasientUrl = DasientUrlUtils.generateDasientRequestUrl();
		HttpPost httppost = new HttpPost(dasientUrl);
		DasientContainer container = new DasientContainer();
		String jsonResponse = "";

		// create the DasientJSONRequest object in order to create the json payload
		DasientJSONRequest dReq = new DasientJSONRequest();
		Request req = dReq.getRequest();
		req.setHostname(msg.getDomainName());
		req.setRequestType(msg.getRequestType());
		req.setHostIp(msg.getHostIP());
		req.setServerhost(msg.getWebserver());

		String responseUrl = System.getProperty("response_url");
		req.setResponseUrl(responseUrl);
		dReq.setRequest(req);

		String jsonString = JSONUtils.mapObjectToJsonString(dReq);
		container.setJsonRequest(jsonString);
		//logger.debug(jsonString);

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		InputStreamEntity reqEntity = new InputStreamEntity(is, -1);
		reqEntity.setContentType("binary/octet-stream");
		reqEntity.setChunked(true);

		httppost.setEntity(reqEntity);
		logger.debug("executing request " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		logger.debug(response.getStatusLine());

		if (resEntity != null) {
			logger.debug("Response content length: " + resEntity.getContentLength());
			jsonResponse = EntityUtils.toString(resEntity);
			container.setJsonResponse(jsonResponse);
			logger.debug("content= " + jsonResponse);
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		// persist Dasient request at this

		// shut down the connection manager to ensure immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();
		return container;
	}

	// private inner class to maintain the jsonRequest and jsonResponse
	private class DasientContainer {

		private String jsonRequest = "";
		private String jsonResponse = "";

		public void setJsonRequest(String request) {
			this.jsonRequest = request;
		}

		public String getJsonRequest() {
			return this.jsonRequest;
		}

		public void setJsonResponse(String response) {
			this.jsonResponse = response;
		}

		public String getJsonResponse() {
			return this.jsonResponse;
		}
	}
}

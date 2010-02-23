package com.ecommerce.dasient.vo;

import com.ecommerce.utils.*;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class DasientAck {
	private String messageType;
	private String startTime;
	private String domainName;
	private long domainId;
	private String controlPanel;
	private String requestType;
	private String hostIP;
	private String webserver;
    private String webUsername;
    private int accountId;
	private boolean dedicatedIp;
	private String statusUrl;
	private String requestId;
	private DasientResponse responseAck;
	private DasientRequest responseAckType;

	public DasientAck() {

		this.messageType = DasientIntegrationConstants.DASIENT_ACK_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;

	}

	/**
	 * @return the messageType
	 */
	@EntityMapping(keyValue = "messageType")
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	@EntityMapping(keyValue = "messageType")
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the domainName
	 */
	@EntityMapping(keyValue = "domainName")
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	@EntityMapping(keyValue = "domainName")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the controlPanel
	 */
	@EntityMapping(keyValue = "controlPanel")
	public String getControlPanel() {
		return controlPanel;
	}

	/**
	 * @param controlPanel the controlPanel to set
	 */
	@EntityMapping(keyValue = "controlPanel")
	public void setControlPanel(String controlPanel) {
		this.controlPanel = controlPanel;
	}

	/**
	 * @return the startTime
	 */
	@EntityMapping(keyValue = "startTime")
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	@EntityMapping(keyValue = "startTime")
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the hostIP
	 */
	@EntityMapping(keyValue = "hostIp")
	public String getHostIP() {
		return hostIP;
	}

	/**
	 * @param hostIP the hostIP to set
	 */
	@EntityMapping(keyValue = "hostIp")
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	/**
	 * @return the responseAck
	 */
	@EntityMapping(keyValue = "responseAck")
	public DasientResponse getResponseAck() {
		return responseAck;
	}

	/**
	 * @param responseAck the responseAck to set
	 */
	@EntityMapping(keyValue = "responseAck")
	public void setResponseAck(DasientResponse responseAck) {
		this.responseAck = responseAck;
	}

	/**
	 * @return the responseAckType
	 */
	@EntityMapping(keyValue = "responseAckType")
	public DasientRequest getResponseAckType() {
		return responseAckType;
	}

	/**
	 * @param responseAckType the responseAckType to set
	 */
	@EntityMapping(keyValue = "responseAckType")
	public void setResponseAckType(DasientRequest responseAckType) {
		this.responseAckType = responseAckType;
	}

	/**
	 * @return the requestType
	 */
	@EntityMapping(keyValue = "requestType")
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	@EntityMapping(keyValue = "requestType")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the webserver
	 */
	@EntityMapping(keyValue = "webserver")
	public String getWebserver() {
		return webserver;
	}

	/**
	 * @param webserver the webserver to set
	 */
	@EntityMapping(keyValue = "webserver")
	public void setWebserver(String webserver) {
		this.webserver = webserver;
	}

	/**
	 * @return the statusUrl
	 */
	@EntityMapping(keyValue = "statusUrl")
	public String getStatusUrl() {
		return statusUrl;
	}

	/**
	 * @param statusUrl the statusUrl to set
	 */
	@EntityMapping(keyValue = "statusUrl")
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	/**
	 * @return the requestId
	 */
	@EntityMapping(keyValue = "requestId")
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	@EntityMapping(keyValue = "requestId")
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the domainId
	 */
	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_ID)
	public long getDomainId() {
		return domainId;
	}

	/**
	 * @param domainId the domainId to set
	 */
	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_ID)
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	@EntityMapping(keyValue="webUsername")
    public String getWebUsername() {
        return webUsername;
    }

	@EntityMapping(keyValue="webUsername")
    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

	@EntityMapping(keyValue="accountId")
    public int getAccountId() {
        return accountId;
    }

	@EntityMapping(keyValue="accountId")
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

	/**
	 * @return the dedicatedIp
	 */
	@EntityMapping(keyValue="dedicatedIp")
	public boolean isDedicatedIp() {
		return dedicatedIp;
	}

	/**
	 * @param dedicatedIp the dedicatedIp to set
	 */
	@EntityMapping(keyValue="dedicatedIp")
	public void setDedicatedIp(boolean dedicatedIp) {
		this.dedicatedIp = dedicatedIp;
	}
}

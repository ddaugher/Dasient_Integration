package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class DasientResult {
	private String messageType;
	private String startTime;
	private String domainName;
	private long domainId;
	private String controlPanel;
	private String requestType;
	private String hostIP;
	private String webserver;
    private String requestId;
    private String jsonPayload;
    private String webUsername;
    private int accountId;
	private boolean dedicatedIp;
	private boolean isInfected;

    public DasientResult() {
		this.messageType = DasientIntegrationConstants.DASIENT_RESULT_MESSAGETYPE;

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
     * @param jsonPayload
     */
	@EntityMapping(keyValue = "requestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

	@EntityMapping(keyValue = "requestId")
    public String getRequestId() {
		return requestId;
    }
    /**
     * @param jsonPayload
     */
	@EntityMapping(keyValue = "jsonPayload")
    public void setJsonPayload(String payload) {
		this.jsonPayload = payload;
    }

	@EntityMapping(keyValue = "jsonPayload")
    public String getJsonPayload() {
        return this.jsonPayload;
    }

	/**
	 * @return the requestType
	 */
	@EntityMapping(keyValue = "requesType")
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	@EntityMapping(keyValue = "requesType")
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

	/**
	 * @return the isInfected
	 */
	@EntityMapping(keyValue="isInfected")
	public boolean isInfected() {
		return isInfected;
	}

	/**
	 * @param isInfected the isInfected to set
	 */
	@EntityMapping(keyValue="isInfected")
	public void setIsInfected(boolean isInfected) {
		this.isInfected = isInfected;
	}
}
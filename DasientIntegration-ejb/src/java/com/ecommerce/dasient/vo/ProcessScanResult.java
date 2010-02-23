package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class ProcessScanResult {
    private String messageType;
    private String startTime;
    private String domainName;
	private long domainId;
    private int accountId;
    private String controlPanel;
	private String requestType;
	private String hostIP;
	private String requestId;
	private String webserver;
    private String webUsername;
    private Long infectionId;
	private boolean dedicatedIp;
	private boolean isInfected;
	private IgnoredDasientResponse ignoredResponse;

	public ProcessScanResult() {

		this.messageType = DasientIntegrationConstants.PROCESS_SCAN_REQUEST_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;
	}

	/**
     */
	@EntityMapping(keyValue = "requestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

	/**
     */
	@EntityMapping(keyValue = "requestId")
    public String getRequestId() {
        return this.requestId;
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
	 * @return the infectionId
	 */
	@EntityMapping(keyValue = "infectionId")
	public Long getInfectionId() {
		return infectionId;
	}

	/**
	 * @param infectionId the infectionId to set
	 */
	@EntityMapping(keyValue = "infectionId")
	public void setInfectionId(Long infectionId) {
		this.infectionId = infectionId;
	}

	/**
	 * @return the ignoredResponse
	 */
	@EntityMapping(keyValue = "ignoredResponse")
	public IgnoredDasientResponse getIgnoredResponse() {
		return ignoredResponse;
	}

	/**
	 * @param ignoredResponse the ignoredResponse to set
	 */
	@EntityMapping(keyValue = "ignoredResponse")
	public void setIgnoredResponse(IgnoredDasientResponse ignoredResponse) {
		this.ignoredResponse = ignoredResponse;
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

    @EntityMapping(keyValue = "accountid")
    public int getAccountId() {
        return accountId;
    }

    @EntityMapping(keyValue = "accountid")
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @EntityMapping(keyValue = "webUsername")
    public String getWebUsername() {
        return webUsername;
    }

    @EntityMapping(keyValue = "webUsername")
    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

	/**
	 * @return the dedicatedIp
	 */
    @EntityMapping(keyValue = "dedicatedIp")
	public boolean isDedicatedIp() {
		return dedicatedIp;
	}

	/**
	 * @param dedicatedIp the dedicatedIp to set
	 */
    @EntityMapping(keyValue = "dedicatedIp")
	public void setDedicatedIp(boolean dedicatedIp) {
		this.dedicatedIp = dedicatedIp;
	}

	/**
	 * @return the isInfected
	 */
    @EntityMapping(keyValue = "isInfected")
	public boolean isInfected() {
		return isInfected;
	}

	/**
	 * @param isInfected the isInfected to set
	 */
    @EntityMapping(keyValue = "isInfected")
	public void setIsInfected(boolean isInfected) {
		this.isInfected = isInfected;
	}
}
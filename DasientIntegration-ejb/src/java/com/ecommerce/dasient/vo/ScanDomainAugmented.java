package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class ScanDomainAugmented {

	private String messageType;
	private String startTime;
	private String domainName;
	private long domainId;
	private String controlPanel;
	private String requestType;
	private String webserver;
	private String hostIP;
    private String webUsername;
    private int accountId;
	private boolean dedicatedIp;

	public ScanDomainAugmented() {

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;

		setRequestType("top-level-scan");
		setMessageType(DasientIntegrationConstants.SCAN_DOMAIN_AUGMENTED_MESSAGETYPE);
	}

	/**
	 * @param domainName
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.MESSAGE_TYPE)
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.MESSAGE_TYPE)
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param domainName
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.DOMAIN_NAME)
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.DOMAIN_NAME)
	public String getDomainName() {
		return this.domainName;
	}

	/**
	 * @param controlPanel
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.CONTROL_PANEL)
	public void setControlPanel(String controlPanel) {
		this.controlPanel = controlPanel;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.CONTROL_PANEL)
	public String getControlPanel() {
		return this.controlPanel;
	}

	/**
	 * @param startTime String value used to track message 'live' time.
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.START_TIME)
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.START_TIME)
	public String getStartTime() {
		return this.startTime;
	}
	/**
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.REQUEST_TYPE)
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.REQUEST_TYPE)
	public String getRequestType() {
		return this.requestType;
	}

	/**
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.WEBSERVER)
	public void setWebserver(String webserver) {
		this.webserver = webserver;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.WEBSERVER)
	public String getWebserver() {
		return this.webserver;
	}

	/**
	 */
	@EntityMapping(keyValue = DasientIntegrationConstants.HOSTIP)
	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	@EntityMapping(keyValue = DasientIntegrationConstants.HOSTIP)
	public String getHostIP() {
		return this.hostIP;
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

	@EntityMapping(keyValue = "webUsername")
    public String getWebUsername() {
        return webUsername;
    }

	@EntityMapping(keyValue = "webUsername")
    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

	@EntityMapping(keyValue = "accountId")
    public int getAccountId() {
        return accountId;
    }

	@EntityMapping(keyValue = "accountId")
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

	/**
	 * @return the dedicatedIp
	 */
	public boolean isDedicatedIp() {
		return dedicatedIp;
	}

	/**
	 * @param dedicatedIp the dedicatedIp to set
	 */
	public void setDedicatedIp(boolean dedicatedIp) {
		this.dedicatedIp = dedicatedIp;
	}
}

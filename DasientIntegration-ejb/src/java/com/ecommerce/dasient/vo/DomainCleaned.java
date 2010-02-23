package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class DomainCleaned {

	private String messageType;
	private String controlPanel;
	private String startTime;
	private String webserver;
	private String username;
	private String infectionId;
	private int accountId;

	public DomainCleaned() {

		this.messageType = DasientIntegrationConstants.DOMAIN_CLEANED_MESSAGETYPE;

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
	 * @return the infectionId
	 */
	@EntityMapping(keyValue = "hostIp")
	public String getHostIP() {
		return getInfectionId();
	}

	/**
	 * @param infectionId the infectionId to set
	 */
	@EntityMapping(keyValue = "hostIp")
	public void setHostIP(String hostIP) {
		this.setInfectionId(hostIP);
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
	public String getInfectionId() {
		return infectionId;
	}

	/**
	 * @param infectionId the infectionId to set
	 */
	@EntityMapping(keyValue = "infectionId")
	public void setInfectionId(String infectionId) {
		this.infectionId = infectionId;
	}

	/**
	 * @return the username
	 */
	@EntityMapping(keyValue = "username")
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	@EntityMapping(keyValue = "username")
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the account id
	 */
	@EntityMapping(keyValue = "accountId")
    public int getAccountId() {
        return accountId;
    }

	/**
	 * @param accountId the account id to set
	 */
	@EntityMapping(keyValue = "accountId")
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}

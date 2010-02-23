package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class AddDomain {
    private String messageType;
    private String domainName;
    private String controlPanel;
    private String startTime;

    public AddDomain() {

		this.messageType = DasientIntegrationConstants.ADD_DOMAIN_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;
    }

	public AddDomain(String domainName, String controlPanel) {

		this.domainName = domainName;
		this.controlPanel = controlPanel;
		this.messageType = DasientIntegrationConstants.ADD_DOMAIN_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;
	}

    /**
     * @param messageType type of message associated with AddDomain
     */
	@EntityMapping(keyValue="messageType")
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

	@EntityMapping(keyValue="messageType")
    public String getMessageType() {
		return messageType;
    }
    /**
     * @param domainName
     */
	@EntityMapping(keyValue="domainName")
    public void setDomainName(String domainName) {
		this.domainName = domainName;
    }

	@EntityMapping(keyValue="domainName")
    public String getDomainName() {
        return this.domainName;
    }

    /**
     * @param controlPanel
     */
	@EntityMapping(keyValue="controlPanel")
    public void setControlPanel(String controlPanel) {
		this.controlPanel = controlPanel;
    }

	@EntityMapping(keyValue="controlPanel")
    public String getControlPanel() {
		return this.controlPanel;
    }

    /**
     * @param startTime String value used to track message 'live' time.
     */
	@EntityMapping(keyValue="startTime")
    public void setStartTime(String startTime) {
		this.startTime = startTime;
    }

	@EntityMapping(keyValue="startTime")
    public String getStartTime() {
		return this.startTime;
    }
}
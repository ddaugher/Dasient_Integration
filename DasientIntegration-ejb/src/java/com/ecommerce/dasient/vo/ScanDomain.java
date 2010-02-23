package com.ecommerce.dasient.vo;

import com.ecommerce.utils.DasientIntegrationConstants;
import com.ecommerce.utils.JulianDay;
import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class ScanDomain {

	private String messageType;
    private String startTime;
    private String domainName;
    private Long domainId;
    private String controlPanel;

	public ScanDomain() {

		this.messageType = DasientIntegrationConstants.SCAN_DOMAIN_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;
	}

	public ScanDomain(String domainName, String controlPanel) {

		this.domainName = domainName;
		this.controlPanel = controlPanel;
		this.messageType = DasientIntegrationConstants.SCAN_DOMAIN_MESSAGETYPE;

		// just as a sanity check... set the startTime to 'now', just so we can track when the message was created.
		String jdn = new JulianDay().getDateTimeStr();
		this.startTime = jdn;
	}

    /**
     * @param domainName
     */
	@EntityMapping(keyValue=DasientIntegrationConstants.MESSAGE_TYPE)
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

	@EntityMapping(keyValue=DasientIntegrationConstants.MESSAGE_TYPE)
    public String getMessageType() {
		return messageType;
    }
    /**
     * @param domainName
     */
	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_NAME)
    public void setDomainName(String domainName) {
		this.domainName = domainName;
    }

	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_NAME)
    public String getDomainName() {
        return this.domainName;
    }

    /**
     * @param controlPanel
     */
	@EntityMapping(keyValue=DasientIntegrationConstants.CONTROL_PANEL)
    public void setControlPanel(String controlPanel) {
		this.controlPanel = controlPanel;
    }

	@EntityMapping(keyValue=DasientIntegrationConstants.CONTROL_PANEL)
    public String getControlPanel() {
		return this.controlPanel;
    }

    /**
     * @param startTime String value used to track message 'live' time.
     */
	@EntityMapping(keyValue=DasientIntegrationConstants.START_TIME)
    public void setStartTime(String startTime) {
		this.startTime = startTime;
    }

	@EntityMapping(keyValue=DasientIntegrationConstants.START_TIME)
    public String getStartTime() {
		return this.startTime;
    }

	/**
	 * @return the domainId
	 */
	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_ID)
	public Long getDomainId() {
		return domainId;
	}

	/**
	 * @param domainId the domainId to set
	 */
	@EntityMapping(keyValue=DasientIntegrationConstants.DOMAIN_ID)
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
}
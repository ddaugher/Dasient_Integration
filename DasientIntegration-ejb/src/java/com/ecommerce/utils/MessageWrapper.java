package com.ecommerce.utils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author djdaugherty
 */
public class MessageWrapper implements Serializable {
    private String messageType;
    private String domainName;
    private String controlPanel;
	private String requestId;
	private String webServerName;
    private long startTime;
    private HashMap<String, Object> _properties = new HashMap<String, Object>(5);

	public MessageWrapper() {

		// just as a sanity check... can be overridden
		setStartTime();
    }

    /**
     */
    public MessageWrapper(String messageType, String domainName, String controlPanel) {
		this.messageType = messageType;
		this.domainName = domainName;
		this.controlPanel = controlPanel;

		// just as a sanity check... can be overridden
		setStartTime();
    }

    /**
     * @return Returns the _properties.
     */
    public HashMap<String, Object> getProperties() {
        return _properties;
    }

    /**
     * @param _properties The _properties to set.
     */
    public void setProperties(HashMap<String, Object> _properties) {
        this._properties = _properties;
    }

    public void add(String key, Object value) {
        _properties.put(key, value);
    }

    public Object get(String key) {
        return _properties.get(key);
    }

    public void delete(String key) {
        _properties.remove(key);
    }

    /**
     * @param startTime long value used to track message 'live' time.
     */
    public void setStartTime(long startTime) {
		this.startTime = startTime;
    }

	// sets the startTime to the number of milliseconds (now)
	public void setStartTime() {
		this.startTime = new JulianDay().getMilliSeconds();
	}

    public long getStartTime() {
        return startTime;
    }

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @return the controlPanel
	 */
	public String getControlPanel() {
		return controlPanel;
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the webServerName
	 */
	public String getWebServerName() {
		return webServerName;
	}

	/**
	 * @param webServerName the webServerName to set
	 */
	public void setWebServerName(String webServerName) {
		this.webServerName = webServerName;
	}
}
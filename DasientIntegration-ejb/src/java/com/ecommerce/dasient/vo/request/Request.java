package com.ecommerce.dasient.vo.request;

import com.ecommerce.utils.annotations.JsonAttribute;

/**
 * @author djdaugherty
 */
public class Request {
	public String hostname;
	private String requestType;
	private String hostIp;
	private String serverhost;
	private String responseUrl;

	public Request() {
	}

	/**
	 */
	@JsonAttribute(javaString="hostname", jsonString="hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonAttribute(javaString="hostname", jsonString="hostname")
	public String getHostname() {
		return this.hostname;
	}

	/**
	 */
	@JsonAttribute(javaString="requestType", jsonString="request-type")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@JsonAttribute(javaString="requestType", jsonString="request-type")
	public String getRequestType() {
		return this.requestType;
	}

	/**
	 */
	@JsonAttribute(javaString="hostIp", jsonString="hostip")
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	@JsonAttribute(javaString="hostIp", jsonString="hostip")
	public String getHostIp() {
		return this.hostIp;
	}

	/**
	 */
	@JsonAttribute(javaString="serverhost", jsonString="serverhost")
	public void setServerhost(String serverhost) {
		this.serverhost = serverhost;
	}

	@JsonAttribute(javaString="serverhost", jsonString="serverhost")
	public String getServerhost() {
		return this.serverhost;
	}

	/**
	 */
	@JsonAttribute(javaString="responseUrl", jsonString="response-url")
	public void setResponseUrl(String url) {
		this.responseUrl = url;
	}

	@JsonAttribute(javaString="responseUrl", jsonString="response-url")
	public String getResponseUrl() {
		return this.responseUrl;
	}
}
package com.ecommerce.dasient.vo.response;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class ResponseAck {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String statusUrl;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String requestId;

	public ResponseAck() {
	}

	/**
	 */
	@JsonAttribute(javaString="statusUrl", jsonString="status-url")
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	@JsonAttribute(javaString="statusUrl", jsonString="status-url")
	public String getStatusUrl() {
		return this.statusUrl;
	}

	/**
	 */
	@JsonAttribute(javaString="requestId", jsonString="request-id")
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@JsonAttribute(javaString="requestId", jsonString="request-id")
	public String getRequestId() {
		return this.requestId;
	}
}
package com.ecommerce.dasient.vo.response;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class DasientJSONResponse {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	private ResponseAck requestAck;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	private ResponseIgnored requestIgnored;

	public DasientJSONResponse() {
	}

	@JsonAttribute(javaString="requestAck", jsonString="request-ack")
	public void setRequestAck(ResponseAck requestAck) {
		this.requestAck = requestAck;
	}

	@JsonAttribute(javaString="requestAck", jsonString="request-ack")
	public ResponseAck getRequestAck() {
		return this.requestAck;
	}

	@JsonAttribute(javaString="requestIgnored", jsonString="request-ignored")
	public void setRequestIgnored(ResponseIgnored requestIgnored) {
		this.requestIgnored = requestIgnored;
	}

	@JsonAttribute(javaString="requestIgnored", jsonString="request-ignored")
	public ResponseIgnored getRequestIgnored() {
		return this.requestIgnored;
	}
}
package com.ecommerce.dasient.vo.response;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class ResponseIgnored {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String requestedId;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String actualIp;

	public ResponseIgnored() {
	}

	/**
	 */
	@JsonAttribute(javaString="requestedIp", jsonString="requested-ip")
	public void setRequestedIp(String requestedId) {
		this.requestedId = requestedId;
	}

	@JsonAttribute(javaString="requestedIp", jsonString="requested-ip")
	public String getRequestedIp() {
		return this.requestedId;
	}

	/**
	 */
	@JsonAttribute(javaString="actualIp", jsonString="actual-ip")
	public void setActualIp(String actualIp) {
		this.actualIp = actualIp;
	}

	@JsonAttribute(javaString="actualIp", jsonString="actual-ip")
	public String getActualIp() {
		return this.actualIp;
	}
}
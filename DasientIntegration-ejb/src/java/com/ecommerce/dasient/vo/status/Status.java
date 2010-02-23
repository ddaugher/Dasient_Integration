package com.ecommerce.dasient.vo.status;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class Status {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String requestId;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String hostname;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String statusState;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	private Info info;

	public Status() {
	}

	/**
	 */
	@JsonAttribute(javaString="requestId", jsonString="request-id")
	public void setRequestId(String id) {
		this.requestId = id;
	}

	@JsonAttribute(javaString="requestId", jsonString="request-id")
	public String getRequestId() {
		return this.requestId;
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
	@JsonAttribute(javaString="statusState", jsonString="status-state")
	public void setStatusState(String statusState) {
		this.statusState = statusState;
	}

	@JsonAttribute(javaString="statusState", jsonString="status-state")
	public String getStatusState() {
		return this.statusState;
	}

	@JsonAttribute(javaString="info", jsonString="info")
	public void setInfo(Info info) {
		this.info = info;
	}

	@JsonAttribute(javaString="info", jsonString="info")
	public Info getInfo() {
		if (this.info==null) {
			this.info = new Info();
		}
		return this.info;
	}
}
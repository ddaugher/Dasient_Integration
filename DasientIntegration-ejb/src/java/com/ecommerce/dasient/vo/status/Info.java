package com.ecommerce.dasient.vo.status;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class Info {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String requestUrl;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String completedAt;

	public Info() {
	}

	/**
	 */
	@JsonAttribute(javaString="requestUrl", jsonString="request-url")
	public void setRequestUrl(String url) {
		this.requestUrl = url;
	}

	@JsonAttribute(javaString="requestUrl", jsonString="request-url")
	public String getRequestUrl() {
		return this.requestUrl;
	}

	/**
	 */
	@JsonAttribute(javaString="completedAt", jsonString="completed-at")
	public void setCompletedAt(String completedAt) {
		this.completedAt = completedAt;
	}

	@JsonAttribute(javaString="completedAt", jsonString="completed-at")
	public String getCompletedAt() {
		return this.completedAt;
	}
}
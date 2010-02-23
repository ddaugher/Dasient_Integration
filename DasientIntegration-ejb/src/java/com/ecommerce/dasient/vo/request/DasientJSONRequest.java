package com.ecommerce.dasient.vo.request;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class DasientJSONRequest {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String apiVersion = "0.9";

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	public Request request;

	public DasientJSONRequest() {
	}

	@JsonAttribute(javaString="request", jsonString="request")
	public void setRequest(Request req) {
		request = req;
	}

	@JsonAttribute(javaString="request", jsonString="request")
	public Request getRequest() {
		if (request==null) {
			request = new Request();
		}
		return request;
	}

	/**
	 * @return the apiVersion
	 */
	@JsonAttribute(javaString="apiVersion", jsonString="api-version")
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion the apiVersion to set
	 */
	@JsonAttribute(javaString="apiVersion", jsonString="api-version")
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
}

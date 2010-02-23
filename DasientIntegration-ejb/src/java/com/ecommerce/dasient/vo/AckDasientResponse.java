package com.ecommerce.dasient.vo;

import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 *
 */
public class AckDasientResponse extends DasientResponse {
    private String statusUrl;
	private String requestId;

    public AckDasientResponse() {
    }

	@EntityMapping(keyValue = "statusUrl")
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
    }

	@EntityMapping(keyValue = "statusUrl")
    public String getStatusUrl() {
        return this.statusUrl;
    }

	@EntityMapping(keyValue = "requestId")
	public void setRequestId(String requestId) {
		this.requestId = requestId;
    }

	@EntityMapping(keyValue = "requestId")
    public String getRequestId() {
        return this.requestId;
    }
}
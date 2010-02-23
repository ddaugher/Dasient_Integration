package com.ecommerce.dasient.vo;

import com.ecommerce.utils.annotations.EntityMapping;

/**
 *
 */
public class IgnoredDasientResponse extends DasientResponse {
    private String requestedIp;
	private String actualIp;

    public IgnoredDasientResponse() {
    }

	@EntityMapping(keyValue = "requestedIp")
	public void setRequestedIp(String requestedIp) {
		this.requestedIp = requestedIp;
    }

	@EntityMapping(keyValue = "requestedIp")
    public String getRequestedIp() {
        return this.requestedIp;
    }

	@EntityMapping(keyValue = "actualIp")
	public void setActualIp(String actualIp) {
		this.actualIp = actualIp;
    }

	@EntityMapping(keyValue = "actualIp")
    public String getActualIp() {
        return this.actualIp;
    }
}
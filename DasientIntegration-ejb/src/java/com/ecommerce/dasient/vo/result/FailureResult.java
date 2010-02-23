package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * FailureResult is a class returned from the Dasient API as part of the payload
 * of a <code>DasientJSONResult</code>.  The failure is not required to contain a
 * data and will be considered optional during the entity mapping process.
 * @author djdaugherty
 */
public class FailureResult {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
    private String code;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
    private String reason;


	/**
	 * default constructor
	 */
	public FailureResult() {
	}

	/**
	 * set code value
	 * @param code failure code
	 */
	@JsonAttribute(javaString="code", jsonString="code")
	public void setCode(String code) {
		this.code = code;
    }

	/**
	 * get code value
	 * @return code string
	 */
	@JsonAttribute(javaString="code", jsonString="code")
    public String getCode() {
        return this.code;
    }

	/**
	 * set reason value
	 * @param reason failure reason
	 */
	@JsonAttribute(javaString="reason", jsonString="reason")
	public void setReason(String reason) {
		this.reason = reason;
    }

	/**
	 * get reason value
	 * @return reason string
	 */
	@JsonAttribute(javaString="reason", jsonString="reason")
    public String getReason() {
        return this.reason;
    }

}
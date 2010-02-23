package com.ecommerce.dasient.vo.status;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class DasientJSONStatus {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	private Status stat;

	public DasientJSONStatus() {
	}

	@JsonAttribute(javaString="status", jsonString="status")
	public void setStatus(Status status) {
		this.stat = status;
	}

	@JsonAttribute(javaString="status", jsonString="status")
	public Status getStatus() {
		if (this.stat==null) {
			this.stat = new Status();
		}
		return this.stat;
	}
}

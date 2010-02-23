package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class DasientJSONResult {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.YES)
	private Result result;

	public DasientJSONResult() {
	}

	@JsonAttribute(javaString="result", jsonString="result")
	public void setResult(Result res) {
		result = res;
	}

	@JsonAttribute(javaString="result", jsonString="result")
	public Result getResult() {
		return result;
	}
}
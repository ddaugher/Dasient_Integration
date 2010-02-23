package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;

/**
 * @author djdaugherty
 */
public class Snippet {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String snippet;

	/**
	 * create empty snippet
	 */
	public Snippet() {
	}

	/**
	 * code snippet associated with malicious/suspicious url returned after scan
	 * via Dasient API.  Not all results from Dasient API will contain snippet (optional)
	 * @param snippet set text value of snippet associated with malicious/suspicious url
	 */
	@JsonAttribute(javaString="snippet", jsonString="snippet")
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	/**
	 * code snippet associated with malicious/suspicious url returned after scan
	 * via Dasient API.  Not all results from Dasient API will contain snippet (optional)
	 * @return text value of snippet associated with malicious/suspicious url
	 */
	@JsonAttribute(javaString="snippet", jsonString="snippet")
	public String getSnippet() {
		return this.snippet;
	}
}
package com.ecommerce.dasient.vo;

import com.ecommerce.utils.annotations.EntityMapping;
import java.util.ArrayList;

/**
 *
 */
public class MaliciousURL {
    private String url;
	private ArrayList<String> codeSnippets;

    public MaliciousURL(String url) {
		this.url = url;
    }

	@EntityMapping(keyValue = "url")
	public void setUrl(String url) {
		this.url = url;
    }

	@EntityMapping(keyValue = "url")
    public String getUrl() {
        return this.url;
    }

	@EntityMapping(keyValue = "codeSnippets")
    public Object[] getCodeSnippets() {
        return this.codeSnippets.toArray();
    }

	public int codeSnippetsSize() {
		return this.getCodeSnippets().length;
	}

	/**
	 * @param codeSnippets the codeSnippets to set
	 */
	@EntityMapping(keyValue = "codeSnippets")
	public void setCodeSnippets(ArrayList<String> codeSnippets) {
		this.codeSnippets = codeSnippets;
	}
}
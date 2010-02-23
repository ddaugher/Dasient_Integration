package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;
import java.util.ArrayList;

/**
 * @author djdaugherty
 */
public class MaliciousResultUrl {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
    private String url;
	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<Snippet> maliciousSourceCodeSnippets = new ArrayList<Snippet>(0);

	/**
	 *
	 */
	public MaliciousResultUrl() {
	}

	/**
	 *
	 * @param url
	 */
	@JsonAttribute(javaString="url", jsonString="url")
	public void setUrl(String url) {
		this.url = url;
    }

	/**
	 *
	 * @return
	 */
	@JsonAttribute(javaString="url", jsonString="url")
    public String getUrl() {
        return this.url;
    }

	/**
	 *
	 * @param snippet
	 */
	public void addCodeSnippet(Snippet snippet) {
		this.maliciousSourceCodeSnippets.add(snippet);
    }

	/**
	 *
	 * @param codeSnippets
	 */
	@JsonAttribute(javaString="maliciousSourceCodeSnippet", jsonString="malicious-source-code-snippet")
	public void setMaliciousSourceCodeSnippets(ArrayList<Snippet> codeSnippets) {
		this.maliciousSourceCodeSnippets = codeSnippets;
	}

	/**
	 *
	 * @return
	 */
	@JsonAttribute(javaString="maliciousSourceCodeSnippet", jsonString="malicious-source-code-snippet")
    public ArrayList<Snippet> getMaliciousSourceCodeSnippets() {
        return this.maliciousSourceCodeSnippets;
    }

	/**
	 *
	 * @return
	 */
	public int MaliciousSourceCodeSnippetsSize() {
		return this.maliciousSourceCodeSnippets.size();
	}
}
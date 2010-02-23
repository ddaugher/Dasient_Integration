package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;
import java.util.ArrayList;

/**
 * Suspicious Result Url is a url returned from the Dasient API as part of the payload
 * of a <code>DasientJSONResult</code>.  The result is not required to contain a
 * SuspiciousResultUrl and will be considered optional during the entity mapping
 * process.
 * @author djdaugherty
 */
public class SuspiciousResultUrl {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
    private String url;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<Snippet> suspiciousSourceCodeSnippets = new ArrayList<Snippet>(0);

	/**
	 * default constructor
	 */
	public SuspiciousResultUrl() {
	}

	/**
	 * set suspicious url value
	 * @param url url string
	 */
	@JsonAttribute(javaString="url", jsonString="url")
	public void setUrl(String url) {
		this.url = url;
    }

	/**
	 * get suspicious url value
	 * @return url string
	 */
	@JsonAttribute(javaString="url", jsonString="url")
    public String getUrl() {
        return this.url;
    }

	/**
	 * add a code snippet to the SuspiciousResultUrl object
	 * @param snippet url string
	 */
	public void addCodeSnippet(Snippet snippet) {
		this.suspiciousSourceCodeSnippets.add(snippet);
    }

	/**
	 * set an ArrayList of Snippets to the SuspiciousResultUrl object
	 * @param codeSnippets
	 */
	@JsonAttribute(javaString="suspiciousSourceCodeSnippet", jsonString="suspicious-source-code-snippet")
	public void setSuspiciousSourceCodeSnippets(ArrayList<Snippet> codeSnippets) {
		this.suspiciousSourceCodeSnippets = codeSnippets;
	}

	/**
	 * get ArrayList of suspicious code snippets from object
	 * @return arraylist of snippets
	 */
	@JsonAttribute(javaString="suspiciousSourceCodeSnippet", jsonString="suspicious-source-code-snippet")
    public ArrayList<Snippet> getSuspiciousSourceCodeSnippets() {
        return this.suspiciousSourceCodeSnippets;
    }

	/**
	 * obtain the size of the current snippet list for the object
	 * @return int value of snippet count
	 */
	public int SuspiciousSourceCodeSnippetsSize() {
		return this.suspiciousSourceCodeSnippets.size();
	}
}
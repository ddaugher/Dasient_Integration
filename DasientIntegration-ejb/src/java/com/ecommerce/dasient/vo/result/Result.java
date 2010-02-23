package com.ecommerce.dasient.vo.result;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;
import java.util.ArrayList;

/**
 * @author djdaugherty
 */
public class Result {

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String requestId;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String completedAt;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String status;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private String hostname;

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<String> onBlacklist = new ArrayList<String>();

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<String> scannedUrls = new ArrayList<String>();

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<MaliciousResultUrl> maliciousUrls = new ArrayList<MaliciousResultUrl>();

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<SuspiciousResultUrl> suspiciousUrls = new ArrayList<SuspiciousResultUrl>();

	@JsonFieldRecursive(recursive=JsonFieldRecursive.Recursive.NO)
	private ArrayList<FailureResult> failure = new ArrayList<FailureResult>();

	public Result() {
	}

	@JsonAttribute(javaString="requestId", jsonString="request-id")
	public void setRequestId(String id) {
		this.requestId = id;
    }

	@JsonAttribute(javaString="requestId", jsonString="request-id")
    public String getRequestId() {
        return this.requestId;
    }

	@JsonAttribute(javaString="completedAt", jsonString="completed-at")
	public void setCompletedAt(String completedAt) {
		this.completedAt = completedAt;
    }

	@JsonAttribute(javaString="completedAt", jsonString="completed-at")
    public String getCompletedAt() {
        return this.completedAt;
    }

	@JsonAttribute(javaString="status", jsonString="status")
	public void setStatus(String status) {
		this.status = status;
    }

	@JsonAttribute(javaString="status", jsonString="status")
    public String getStatus() {
        return this.status;
    }

	@JsonAttribute(javaString="hostname", jsonString="hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
    }

	@JsonAttribute(javaString="hostname", jsonString="hostname")
    public String getHostname() {
        return this.hostname;
    }

	public void addOnBlacklist(String blacklist) {
		this.onBlacklist.add(blacklist);
	}

	@JsonAttribute(javaString="onBlacklist", jsonString="on-blacklist")
	public void setOnBlacklist(ArrayList<String> onBlacklist) {
		this.onBlacklist = onBlacklist;
    }

	@JsonAttribute(javaString="onBlacklist", jsonString="on-blacklist")
    public ArrayList<String> getOnBlacklist() {
        return this.onBlacklist;
    }

	public int onBlacklistSizeI() {
		return this.onBlacklist.size();
	}

	public void addScannedUrl(String url) {
		this.scannedUrls.add(url);
    }

	@JsonAttribute(javaString="scannedUrls", jsonString="scanned-urls")
	public void setScannedUrls(ArrayList<String> urls) {
		this.scannedUrls = urls;
	}

	@JsonAttribute(javaString="scannedUrls", jsonString="scanned-urls")
    public ArrayList<String> getScannedUrls() {
        return this.scannedUrls;
    }

	public int scannedUrlsSize() {
		return this.scannedUrls.size();
	}

	public void addMaliciousUrls(MaliciousResultUrl url) {
		this.maliciousUrls.add(url);
    }

	@JsonAttribute(javaString="maliciousUrls", jsonString="malicious-urls")
	public void setMaliciousUrls(ArrayList<MaliciousResultUrl> urls) {
		this.maliciousUrls = urls;
	}

	@JsonAttribute(javaString="maliciousUrls", jsonString="malicious-urls")
    public ArrayList<MaliciousResultUrl> getMaliciousUrls() {
        return this.maliciousUrls;
    }

	public int maliciousUrlsSize() {
		return this.maliciousUrls.size();
	}

	public void addSuspiciousUrls(SuspiciousResultUrl url) {
		this.suspiciousUrls.add(url);
    }

	@JsonAttribute(javaString="suspiciousUrls", jsonString="suspicious-urls")
	public void setSuspiciousUrls(ArrayList<SuspiciousResultUrl> urls) {
		this.suspiciousUrls = urls;
	}

	@JsonAttribute(javaString="suspiciousUrls", jsonString="suspicious-urls")
    public ArrayList<SuspiciousResultUrl> getSuspiciousUrls() {
        return this.suspiciousUrls;
    }

	public int suspiciousUrlsSize() {
		return this.suspiciousUrls.size();
	}

	public void addFailure(FailureResult failure) {
		this.failure.add(failure);
    }

	@JsonAttribute(javaString="failure", jsonString="failure")
	public void setFailure(ArrayList<FailureResult> failure) {
		this.failure = failure;
	}

	@JsonAttribute(javaString="failure", jsonString="failure")
    public ArrayList<FailureResult> getFailures() {
        return this.failure;
    }

	public int failureSize() {
		return this.failure.size();
	}
}
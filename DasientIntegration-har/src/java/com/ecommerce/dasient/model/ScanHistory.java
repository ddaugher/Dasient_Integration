package com.ecommerce.dasient.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author djdaugherty
 */
@Entity
@Table(name = "scan_history")
@NamedQueries({
	@NamedQuery(name = "getScannedHistoryCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'REQUEST_ACK', 'IGNORED', 'ZERO_LENGTH', 'EXPIRED' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryMaliciousCount",
	query = "select count(s) from ScanHistory s where s.rawResponse like '%malicious%' and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistorySuspiciousCount",
	query = "select count(s) from ScanHistory s where s.rawResponse like '%suspicious%' and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryZeroLengthCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'ZERO_LENGTH' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryExpiredCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'EXPIRED' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryFailedCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'FAILED' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryRequestAckCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'REQUEST_ACK' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryIgnoredCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'IGNORED' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScannedHistoryCompletedCount",
	query = "select count(s) from ScanHistory s where s.status in ( 'COMPLETED' ) and s.createTms between :fromdate and :todate"),

	@NamedQuery(name = "getScanHistoryByRequestId",
	query = "select s from ScanHistory s where s.requestId = :requestId"),

	@NamedQuery(name = "getScanHistoryEntities",
	query = "select s from ScanHistory s where s.hostname = :hostname and s.status in ( 'COMPLETED', 'IGNORED', 'ZERO_LENGTH' ) order by s.createTms desc"),

	@NamedQuery(name = "getScanHistoryCountForDomain",
	query = "select count(s) from ScanHistory s where s.hostname = :hostname")
})
public class ScanHistory implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(generator = "scan_history_seq")
    @SequenceGenerator(name = "scan_history_seq", sequenceName = "scan_history_seq")
	@Column(name = "scan_history_id")
	private Long id;

	/**
	 *
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 */
	@PrePersist
	@PreUpdate
	protected void onCreate() {
		createTms = new Date();
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_tms", nullable = false)
	private Date createTms;

	/**
	 *
	 * @return
	 */
	public Date getCreateTms() {
		return createTms;
	}

	/**
	 *
	 * @param tms
	 */
	public void setCreateTms(Date tms) {
		this.createTms = tms;
	}

	@Column(name = "request_id")
	private String requestId;

	/**
	 *
	 * @return
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 *
	 * @param requestId
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	@Column(name = "completed_at")
	private String completedAt;

	/**
	 *
	 * @return
	 */
	public String getCompletedAt() {
		return completedAt;
	}

	/**
	 *
	 * @param completedAt
	 */
	public void setCompletedAt(String completedAt) {
		this.completedAt = completedAt;
	}
	@Column(name = "status")
	private String status;

	/**
	 *
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 *
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	@Column(name = "hostname")
	private String hostname;

	/**
	 *
	 * @return
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 *
	 * @param hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	// on_blacklist
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private Set<OnBlacklist> onBlacklists;

	/**
	 *
	 * @return
	 */
	public Set<OnBlacklist> getOnBlacklists() {
		return onBlacklists;
	}

	/**
	 *
	 * @param onBlacklists
	 */
	public void setOnBlacklists(Set<OnBlacklist> onBlacklists) {
		this.onBlacklists = onBlacklists;
	}
	// scanned urls
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private Set<ScannedUrl> scannedUrls;

	/**
	 *
	 * @return
	 */
	public Set<ScannedUrl> getScannedUrls() {
		return scannedUrls;
	}

	/**
	 *
	 * @param scannedUrls
	 */
	public void setScannedUrls(Set<ScannedUrl> scannedUrls) {
		this.scannedUrls = scannedUrls;
	}

	// malicious urls
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private Set<MaliciousUrl> maliciousUrls;

	/**
	 *
	 * @return
	 */
	public Set<MaliciousUrl> getMaliciousUrls() {
		return maliciousUrls;
	}

	/**
	 *
	 * @param maliciousUrls
	 */
	public void setMaliciousUrls(Set<MaliciousUrl> maliciousUrls) {
		this.maliciousUrls = maliciousUrls;
	}

	// suspicious urls
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private Set<SuspiciousUrl> suspiciousUrls;

	/**
	 *
	 * @return
	 */
	public Set<SuspiciousUrl> getSuspiciousUrls() {
		return suspiciousUrls;
	}

	/**
	 *
	 * @param suspiciousUrls
	 */
	public void setSuspiciousUrls(Set<SuspiciousUrl> suspiciousUrls) {
		this.suspiciousUrls = suspiciousUrls;
	}

	// failure
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private Set<Failure> failures;

	/**
	 *
	 * @return
	 */
	public Set<Failure> getFailures() {
		return failures;
	}

	/**
	 *
	 * @param failures
	 */
	public void setFailures(Set<Failure> failures) {
		this.failures = failures;
	}

	// ignored requests
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "scanHistory", fetch = FetchType.LAZY)
	private IgnoredRequest ignoredRequest;

	/**
	 *
	 * @return
	 */
	public IgnoredRequest getIgnoredRequest() {
		return ignoredRequest;
	}

	/**
	 *
	 * @param ignoredRequest
	 */
	public void setIgnoredRequest(IgnoredRequest ignoredRequest) {
		this.ignoredRequest = ignoredRequest;
	}

	@Lob
	@Column(name = "raw_request")
	private String rawRequest;

	/**
	 *
	 * @return
	 */
	public String getRawRequest() {
		return rawRequest;
	}

	/**
	 *
	 * @param rawRequest
	 */
	public void setRawRequest(String rawRequest) {
		this.rawRequest = rawRequest;
	}

	@Lob
	@Column(name = "raw_response")
	private String rawResponse;

	/**
	 *
	 * @return
	 */
	public String getRawResponse() {
		return rawResponse;
	}

	/**
	 *
	 * @param rawResponse
	 */
	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	@Lob
	@Column(name = "status_url")
	private String statusUrl;

	/**
	 *
	 * @return
	 */
	public String getStatusUrl() {
		return statusUrl;
	}

	/**
	 *
	 * @param statusUrl
	 */
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ScanHistory)) {
			return false;
		}
		ScanHistory other = (ScanHistory) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ecommerce.dasient.model.ScanHistory[id=" + id + "]";
	}
}

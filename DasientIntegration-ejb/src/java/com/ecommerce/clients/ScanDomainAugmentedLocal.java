package com.ecommerce.clients;

import javax.ejb.Local;

@Local
public interface ScanDomainAugmentedLocal {

	public long persistScanHistoryRequestIgnored(String requestIp, String actualIp, String domainName, String jsonRequest);

	public long persistScanHistoryRequestAck(String domainName, String requestId, String statusUrl, String rawRequest);

	public long persistScanHistoryZeroLengthResponse(String domainName, String jsonRequest);

	public long updateScanHistoryStatusByRequestId(String requestId, String status);
}

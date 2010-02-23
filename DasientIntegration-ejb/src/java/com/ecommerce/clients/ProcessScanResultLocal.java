package com.ecommerce.clients;

import com.ecommerce.dasient.model.Domain;
import java.util.Date;
import javax.ejb.Local;

@Local
public interface ProcessScanResultLocal {

	public Domain getDomainById(Long domainId, String domainName);

	public void persistDomainNextScanUpdate(Long domainId, String domainName, Date nextScanDate);

	public boolean getScanHistoryInfectedCountForDomain(String domainName, int limitResultCount);

	public int hasRejectedScansInARow(String domainName, int limitResultCount);

	public int getScanHistoryCountForDomain(String domainName);

	public boolean hasBeenRejectedWithinLastScans(String domainName, int limitScanCount);

}

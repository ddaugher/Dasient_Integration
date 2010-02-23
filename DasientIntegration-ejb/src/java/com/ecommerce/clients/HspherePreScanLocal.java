package com.ecommerce.clients;

import javax.ejb.Local;

@Local
public interface HspherePreScanLocal {

    public HspherePreScanContainer getPreScanAugmentedData(String domainName, String controlPanel);

	public void markDomainDeleted(long domainId, String domainName);

	public void markDomainNextScan(long domainId, String domainName, int numberDays);

}

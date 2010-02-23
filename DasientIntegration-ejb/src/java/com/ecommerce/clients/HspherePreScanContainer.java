package com.ecommerce.clients;

/**
 * @author djdaugherty
 */
public class HspherePreScanContainer {
	public String hostip;
	private String webserver;
    private String webUsername;
    private int accountId;
	private boolean dedicatedIp = false;
	private boolean suspended = false;

	public HspherePreScanContainer() {
	}

	/**
	 */
	public void setHostIp(String hostip) {
		this.hostip = hostip;
	}

	public String getHostIp() {
		return this.hostip;
	}

	/**
	 */
	public void setWebserver(String webserver) {
		this.webserver = webserver;
	}

	public String getWebserver() {
		return this.webserver;
	}


    public String getWebUsername() {
        return webUsername;
    }

    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

	/**
	 * @return the dedicatedIp
	 */
	public boolean isDedicatedIp() {
		return dedicatedIp;
	}

	/**
	 * @param dedicatedIp the dedicatedIp to set
	 */
	public void setDedicatedIp(boolean dedicatedIp) {
		this.dedicatedIp = dedicatedIp;
	}

	/**
	 * @return the isSuspended
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * @param isSuspended the isSuspended to set
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
}
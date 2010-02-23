package com.ecommerce.clients;

import javax.ejb.Local;

@Local
public interface HsphereSyncLocal {

    public void syncControlPanel(int controlPanelId);

    public void syncWebServers(int controlPanelId);

    public boolean syncDomains(int controlPanelId);
}

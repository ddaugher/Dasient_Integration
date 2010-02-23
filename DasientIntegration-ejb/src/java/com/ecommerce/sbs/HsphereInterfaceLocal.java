package com.ecommerce.sbs;

import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.WebServer;
import java.io.Serializable;
import javax.ejb.Local;

@Local
public interface HsphereInterfaceLocal {

    /**
     * A summary of the augmented info acquired in the process.
     */

    public static class AugmentedInfo implements Serializable {
        public ControlPanel controlPanel;
        public WebServer webServer;
        public String webUsername;
        public int accountId;
    }

    AugmentedInfo queryAugmentInfo(ControlPanel controlPanel, String domainName);

}

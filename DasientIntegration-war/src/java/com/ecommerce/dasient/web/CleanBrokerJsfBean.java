package com.ecommerce.dasient.web;

import com.ecommerce.dasient.exceptions.ControlPanelNotFoundException;
import com.ecommerce.dasient.exceptions.WebServerNotFoundException;
import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.WebServer;
import com.ecommerce.dasient.utils.NaturalOrderComparator;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.sbs.CleanBrokerLocal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CleanBrokerJsfBean {

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    @EJB(mappedName="DasientIntegration/CleanBrokerBean/local")
    private CleanBrokerLocal cleanBrokerBean;

    private List<ControlPanel> controlPanels;

    @SuppressWarnings("unchecked")
    public List<ControlPanel> getControlPanels() {
        if (controlPanels == null) {
            controlPanels = dasientEM.createQuery("select cp from ControlPanel cp order by cp.id").getResultList();
        }
        return controlPanels;
    }

    private Map<Integer, List<WebServer>> webServersByControlPanel;

    @SuppressWarnings("unchecked")
    public Map<Integer, List<WebServer>> getWebServersByControlPanel() {
        if (webServersByControlPanel == null) {
            List<WebServer> allWebServers = dasientEM.createQuery("select ws from WebServer ws order by ws.name").getResultList();

            webServersByControlPanel = new HashMap<Integer, List<WebServer>>();

            for (WebServer webServer : allWebServers) {
                List<WebServer> webServersOfCp = webServersByControlPanel.get(webServer.getControlPanel().getId());

                if (webServersOfCp == null) {
                    webServersOfCp = new ArrayList<WebServer>();
                    webServersOfCp.add(webServer);
                    webServersByControlPanel.put(webServer.getControlPanel().getId(), webServersOfCp);
                } else {
                    webServersOfCp.add(webServer);
                }
            }

            // Sorts web servers by name by natural order (eg. web2 should come before web100)
            for (List<WebServer> webServersOfCp : webServersByControlPanel.values()) {
                Collections.sort(webServersOfCp, new Comparator<WebServer>() {

                    private Comparator<String> delegate = new NaturalOrderComparator();

                    public int compare(WebServer o1, WebServer o2) {
                        return delegate.compare(o1.getName(), o2.getName());
                    }

                });
            }
        }
        return webServersByControlPanel;
    }

    public static class NewRequestEntry {

        private String controlPanel;

        public String getControlPanel() {
            return controlPanel;
        }

        public void setControlPanel(String controlPanel) {
            this.controlPanel = controlPanel;
        }

        private String domainName;

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

    }

    public static class CleanRequestEntry {

        public CleanRequestEntry(String controlPanel, String webServer, String username, String domainName) {
            this.controlPanel = controlPanel;
            this.webServer = webServer;
            this.username = username;
            this.domainName = domainName;
        }

        public CleanRequestEntry(CleanWorkRequest request) {
            this.controlPanel = request.getControlPanel();
            this.webServer = request.getWebServer();
            this.username = request.getWebUsername();
            this.domainName = String.valueOf(request.getAccountId());
        }

        private String controlPanel;

        public String getControlPanel() {
            return controlPanel;
        }

        public void setControlPanel(String controlPanel) {
            this.controlPanel = controlPanel;
        }

        private String webServer;

        public String getWebServer() {
            return webServer;
        }

        public void setWebServer(String webServer) {
            this.webServer = webServer;
        }

        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        private String domainName;

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

    }

    private NewRequestEntry newRequest = new NewRequestEntry();

    public NewRequestEntry getNewRequest() {
        return newRequest;
    }

    public Collection<CleanRequestEntry> getCleanRequests() {
        List<CleanWorkRequest> requests = cleanBrokerBean.getAllWorkOrders();

        Collection<CleanRequestEntry> entries = new ArrayList<CleanRequestEntry>(requests.size());
        for (CleanWorkRequest request : requests)
            entries.add(new CleanRequestEntry(request));

        return entries;
    }

    public String addRequest() {
        cleanBrokerBean.scheduleClean(
                newRequest.getControlPanel(),
                newRequest.getDomainName());

        return "requestAdded";
    }

}

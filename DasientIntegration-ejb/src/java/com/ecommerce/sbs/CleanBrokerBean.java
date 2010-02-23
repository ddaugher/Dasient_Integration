package com.ecommerce.sbs;

import com.ecommerce.dasient.exceptions.CleanWorkUnitNotFoundException;
import com.ecommerce.dasient.exceptions.ControlPanelNotFoundException;
import com.ecommerce.dasient.exceptions.DomainNotFoundException;
import com.ecommerce.dasient.exceptions.PersistBackupException;
import com.ecommerce.dasient.exceptions.RevisionNotFoundException;
import com.ecommerce.dasient.exceptions.RuleNotFoundException;
import com.ecommerce.dasient.exceptions.ScanHistoryNotFoundException;
import com.ecommerce.dasient.exceptions.WebServerMismatchException;
import com.ecommerce.dasient.exceptions.WebServerNotFoundException;
import com.ecommerce.dasient.model.CleanHistory;
import com.ecommerce.dasient.model.CleanFile;
import com.ecommerce.dasient.model.CleanRuleMatch;
import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.PendingClean;
import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.dasient.model.ScanHistory;
import com.ecommerce.dasient.model.WebServer;
import com.ecommerce.dasient.model.WebServerActivity;
import com.ecommerce.dasient.model.WebStorage;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.dasient.vo.CleanWorkResponse;
import com.ecommerce.dasient.vo.DomainCleaned;
import com.ecommerce.utils.EntityMapper;
import com.ecommerce.utils.DasientIntegrationConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;

@Stateless
@LocalBinding(jndiBinding = "DasientIntegration/CleanBrokerBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CleanBrokerBean implements CleanBrokerLocal {

    private static final Logger logger = Logger.getLogger(CleanBrokerBean.class);

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = DasientIntegrationConstants.DOMAINCLEANED_QUEUE)
    private Queue domainCleanedQueue;

    @EJB
    private HsphereInterfaceLocal hsphere;

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CleanWorkRequest> getAllWorkOrders() {
        List<PendingClean> pendingCleans = dasientEM.createNamedQuery("getAllPendingCleans")
                .getResultList();

        return createWorkRequestsFromEntities(pendingCleans);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CleanWorkRequest> getWorkOrders(String webServerName, boolean recordActivity)
            throws WebServerNotFoundException {

        WebServer webServer = findWebServer(webServerName);

        if (recordActivity) {
            recordWebServerActivity(webServer);
        }

        List<PendingClean> pendingCleans = browseQueueForWork(webServer);

        if (recordActivity) {
            for (PendingClean pending : pendingCleans) {
                if (pending.getQueryTime() == null) {
                    pending.setQueryTime(new Date());
                    dasientEM.persist(pending);
                }
            }
        }

        return createWorkRequestsFromEntities(pendingCleans);
    }

    private WebServer findWebServer(String webServerName)
            throws WebServerNotFoundException {

        WebServer webServer;

        try {
            webServer = (WebServer) dasientEM.createNamedQuery("getWebServerByName")
                    .setParameter("name", webServerName).getSingleResult();
        } catch (NoResultException exc) {
            throw new WebServerNotFoundException(webServerName, exc);
        }

        return webServer;
    }

    private void recordWebServerActivity(WebServer webServer)
            throws WebServerNotFoundException {

        WebServerActivity activityEntry = new WebServerActivity();

        activityEntry.setWebServer(webServer);
        activityEntry.setAccessTime(new Date());

        dasientEM.persist(activityEntry);
    }

    @SuppressWarnings("unchecked")
    private List<PendingClean> browseQueueForWork(WebServer webServer) {
        WebStorage webStorage = webServer.getStorage();

        List<PendingClean> pendingCleans;

        if (webStorage != null) {
            pendingCleans = dasientEM.createNamedQuery("getPendingCleansForWebStorage")
                .setParameter("webStorage", webStorage)
                .setMaxResults(webStorage.getMaxConcurrentCleans())
                .getResultList();

            Iterator<PendingClean> it = pendingCleans.iterator();

            while (it.hasNext()) {
                if (!it.next().getWebServer().equals(webServer)) {
                    it.remove();
                }
            }
        }
        else {
            pendingCleans = dasientEM.createNamedQuery("getPendingCleansForWebServer")
                .setParameter("webServer", webServer)
                .getResultList();
        }

        return pendingCleans;
    }

    private List<CleanWorkRequest> createWorkRequestsFromEntities(List<PendingClean> pendingCleans) {
        List<CleanWorkRequest> orders = new ArrayList<CleanWorkRequest>();

        for (PendingClean pending : pendingCleans) {
            CleanWorkRequest order = new CleanWorkRequest();

            order.setWorkUnitId(pending.getId());
            order.setControlPanel(pending.getControlPanel().getName());
            order.setWebServer(pending.getWebServer().getName());
            order.setWebUsername(pending.getWebUsername());
            order.setAccountId(pending.getAccountId());

            List<CleanWorkRequest.DasientCommunication> orderTriggers = new ArrayList<CleanWorkRequest.DasientCommunication>();

            for (ScanHistory trigger : pending.getTriggers()) {
                CleanWorkRequest.DasientCommunication orderTrigger = new CleanWorkRequest.DasientCommunication();

                orderTrigger.setScanHistoryId(trigger.getId());
                orderTrigger.setRawRequest(trigger.getRawRequest());
                orderTrigger.setRawResponse(trigger.getRawResponse());

                orderTriggers.add(orderTrigger);
            }

            order.setTriggers(orderTriggers);

            orders.add(order);
        }

        return orders;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CleanHistory submitWorkResult(CleanWorkResponse workResult)
            throws CleanWorkUnitNotFoundException, WebServerNotFoundException,
            WebServerMismatchException, PersistBackupException, RevisionNotFoundException {

        WebServer webServer = findWebServer(workResult.getWebServer());

        PendingClean pending = findWorkRequest(workResult.getWorkUnitId(), webServer);

        CleanHistory cleanHistory = recordCleanHistoryEntry(webServer, pending, workResult);

        sendDomainCleanedMessage(pending, workResult);

        dasientEM.remove(pending);

        return cleanHistory;
    }

    private PendingClean findWorkRequest(long workUnitId, WebServer webServer)
            throws CleanWorkUnitNotFoundException, WebServerMismatchException {

        PendingClean pending = dasientEM.find(PendingClean.class, workUnitId);

        if (pending == null) {
            throw new CleanWorkUnitNotFoundException(workUnitId);
        }

        if (!pending.getWebServer().equals(webServer)) {
            throw new WebServerMismatchException(
                    pending.getWebServer().getName(), webServer.getName());
        }

        return pending;
    }

    @SuppressWarnings("unchecked")
    private CleanHistory recordCleanHistoryEntry(WebServer webServer, PendingClean pendingClean, CleanWorkResponse workResult)
            throws WebServerNotFoundException, RevisionNotFoundException, RuleNotFoundException {

        Revision revision = findRevision(workResult.getRuleSetId());

        CleanHistory historyEntry = new CleanHistory();

        historyEntry.setId(pendingClean.getId());
        historyEntry.setControlPanel(pendingClean.getControlPanel());
        historyEntry.setScheduleTime(pendingClean.getScheduleTime());
        historyEntry.setStartTime(workResult.getStartTime());
        historyEntry.setFinishTime(workResult.getFinishTime());
        historyEntry.setWebUsername(pendingClean.getWebUsername());
        historyEntry.setWebServer(webServer);
        historyEntry.setRevision(revision);
        historyEntry.setCleanFiles(new ArrayList<CleanFile>());
        historyEntry.setTriggers(new HashSet<ScanHistory>(pendingClean.getTriggers()));

        dasientEM.persist(historyEntry);

        List<RuleRevision> ruleRevisions = dasientEM.createNamedQuery("getRulesByRevision")
                .setParameter("revisionId", workResult.getRuleSetId())
                .getResultList();

        for (CleanWorkResponse.File file : workResult.getFiles()) {
            CleanFile cleanFile = new CleanFile();

            cleanFile.setCleanHistory(historyEntry);
            cleanFile.setPath(file.getPath());
            cleanFile.setHash(file.getHash());
            cleanFile.setChangeTime(file.getChangeTime());
            cleanFile.setModifyTime(file.getModifyTime());
            cleanFile.setNewChangeTime(file.getNewChangeTime());
            cleanFile.setMode(file.getMode());
            cleanFile.setMatches(new ArrayList<CleanRuleMatch>());

            historyEntry.getCleanFiles().add(cleanFile);

            dasientEM.persist(cleanFile);

            for (CleanWorkResponse.File.Match match : file.getMatches()) {
                RuleRevision ruleRevisionMatch = null;

                for (RuleRevision ruleRevision : ruleRevisions) {
                    if (ruleRevision.getRule().getId() == match.getRuleId()) {
                        ruleRevisionMatch = ruleRevision;
                        break;
                    }
                }

                if (ruleRevisionMatch == null) {
                    throw new RuleNotFoundException(match.getRuleId(), workResult.getRuleSetId());
                }

                CleanRuleMatch cleanRuleMatch = new CleanRuleMatch();

                cleanRuleMatch.setFile(cleanFile);
                cleanRuleMatch.setRule(ruleRevisionMatch);
                cleanRuleMatch.setStart(match.getStart());
                cleanRuleMatch.setEnd(match.getEnd());
                cleanRuleMatch.setLiteral(match.getLiteral());

                cleanFile.getMatches().add(cleanRuleMatch);

                dasientEM.persist(cleanRuleMatch);
            }
        }

        return historyEntry;
    }

    private Revision findRevision(long revisionId) throws RevisionNotFoundException {
        Revision revision = dasientEM.find(Revision.class, revisionId);

        if (revision == null) {
            throw new RevisionNotFoundException(revisionId);
        }

        return revision;
    }

    private void sendDomainCleanedMessage(PendingClean cleanMsg, CleanWorkResponse workResult) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);

            DomainCleaned cleanedMsg = new DomainCleaned();

            cleanedMsg.setControlPanel(cleanMsg.getControlPanel().getName());
            cleanedMsg.setWebserver(cleanMsg.getWebServer().getName());
            cleanedMsg.setUsername(cleanMsg.getWebUsername());
            cleanedMsg.setAccountId(cleanMsg.getAccountId());

            producer = session.createProducer(domainCleanedQueue);
            producer.send(EntityMapper.beanToMessage(cleanedMsg, session.createMapMessage()));

        } catch (JMSException exc) {
            throw new EJBException("Failed to send domain cleaned message", exc);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS MessageProducer", exc);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS Session", exc);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exc) {
                    logger.debug("Failed to close JMS Connection", exc);
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PendingClean scheduleClean(String controlPanelName, String domainName)
            throws ControlPanelNotFoundException, DomainNotFoundException {

        ControlPanel controlPanel;

        // Make sure we actually know the control panel
        try {
            controlPanel = (ControlPanel) dasientEM.createNamedQuery("getControlPanelByName")
                    .setParameter("name", controlPanelName)
                    .getSingleResult();
        } catch (NoResultException exc) {
            throw new ControlPanelNotFoundException(controlPanelName, exc);
        }

        // Make sure we actually know the domain
        try {
            dasientEM.createNamedQuery("getDomainByName")
                    .setParameter("controlpanel", controlPanelName)
                    .setParameter("name", domainName)
                    .getSingleResult();
        } catch (NoResultException exc) {
            throw new DomainNotFoundException(domainName, exc);
        }

        HsphereInterfaceLocal.AugmentedInfo augment =
                hsphere.queryAugmentInfo(controlPanel, domainName);

        return createPendingCleanEntry(controlPanelName, augment.webServer.getName(),
                augment.webUsername, augment.accountId, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PendingClean scheduleCleanTriggeredByInfection(String controlPanelName, String webServerName,
            String webUsername, int accountId, long triggeredByScanHistoryId)
            throws ControlPanelNotFoundException, WebServerNotFoundException, ScanHistoryNotFoundException {

        return createPendingCleanEntry(controlPanelName, webServerName, webUsername, accountId, triggeredByScanHistoryId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PendingClean scheduleCleanWithAugmentedInfo(String controlPanelName, String webServerName,
            String webUsername, int accountId) throws ControlPanelNotFoundException, WebServerNotFoundException {

        return createPendingCleanEntry(controlPanelName, webServerName, webUsername, accountId, null);
    }

    private PendingClean createPendingCleanEntry(String controlPanelName, String webServerName,
            String webUsername, int accountId, Long triggeredByScanHistoryId)
            throws ControlPanelNotFoundException, WebServerNotFoundException, ScanHistoryNotFoundException {

        WebServer webServer;

        try {
            webServer = (WebServer) dasientEM.createNamedQuery("getWebServerByName")
                    .setParameter("name", webServerName).getSingleResult();
        } catch (NoResultException exc) {
            throw new WebServerNotFoundException(webServerName, exc);
        }

        ControlPanel controlPanel;

        try {
            controlPanel = (ControlPanel) dasientEM.createNamedQuery("getControlPanelByName")
                    .setParameter("name", controlPanelName)
                    .getSingleResult();
        } catch (NoResultException exc) {
            throw new ControlPanelNotFoundException(controlPanelName, exc);
        }

        PendingClean pending = findPendingCleanForAccount(controlPanel, accountId);

        if (pending == null) {
            pending = new PendingClean();

            pending.setControlPanel(controlPanel);
            pending.setWebServer(webServer);
            pending.setAccountId(accountId);
            pending.setWebUsername(webUsername);
            pending.setScheduleTime(new Date());
            pending.setTriggers(new HashSet<ScanHistory>());

            dasientEM.persist(pending);

            logger.debug(String.format(
                    "Created pending clean entry (pendingId=%d)",
                    pending.getId()));
        }
        else {
            logger.debug(String.format(
                    "Skipped creating pending clean entry, there was already a clean entry for the same account (pendingId=%d)",
                    pending.getId()));
        }

        if (triggeredByScanHistoryId != null) {
            ScanHistory scanHistory = dasientEM.find(ScanHistory.class, triggeredByScanHistoryId);

            if (scanHistory == null)
                throw new ScanHistoryNotFoundException(triggeredByScanHistoryId);

            pending.getTriggers().add(scanHistory);

            dasientEM.persist(pending);
        }

        return pending;
    }

    @SuppressWarnings("unchecked")
    private PendingClean findPendingCleanForAccount(ControlPanel controlPanel, int accountId) {
        List<PendingClean> pending = dasientEM.createNamedQuery("getPendingCleanForAccount")
                .setParameter("controlPanel", controlPanel)
                .setParameter("accountId", accountId)
                .getResultList();

        if (!pending.isEmpty())
            return pending.get(0);
        else
            return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Status getWorkUnitStatus(long workUnitId)
            throws CleanWorkUnitNotFoundException {

        PendingClean pending = dasientEM.find(PendingClean.class, workUnitId);
        if (pending != null)
            return Status.PENDING;

        ScanHistory history = dasientEM.find(ScanHistory.class, workUnitId);
        if (history != null)
            return Status.COMPLETED;

        throw new CleanWorkUnitNotFoundException(workUnitId);
    }

}

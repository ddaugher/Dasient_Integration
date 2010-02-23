package com.ecommerce.sbs;

import com.ecommerce.dasient.exceptions.CleanWorkUnitNotFoundException;
import com.ecommerce.dasient.exceptions.ControlPanelNotFoundException;
import com.ecommerce.dasient.exceptions.DomainNotFoundException;
import com.ecommerce.dasient.exceptions.RevisionNotFoundException;
import com.ecommerce.dasient.exceptions.ScanHistoryNotFoundException;
import com.ecommerce.dasient.exceptions.WebServerMismatchException;
import com.ecommerce.dasient.exceptions.WebServerNotFoundException;
import com.ecommerce.dasient.model.CleanHistory;
import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.dasient.model.PendingClean;
import com.ecommerce.dasient.model.ScanHistory;
import com.ecommerce.dasient.model.WebServer;
import com.ecommerce.dasient.model.WebServerActivity;
import com.ecommerce.dasient.test.EjbTestHelper;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.dasient.vo.CleanWorkResponse;
import com.ecommerce.utils.DasientIntegrationConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.openejb.api.LocalClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

@LocalClient
public class CleanBrokerBeanTest {

    private InitialContext ctx;

    @Before
    public void setUpContainer() throws Exception {
        ctx = EjbTestHelper.createContainer(getClass());
        ctx.bind("inject", this);
    }

    @After
    public void tearDownContainer() throws Exception {
        EjbTestHelper.destroyContainer(ctx);
    }

    @PersistenceContext
    private EntityManager em;

    @EJB
    private CleanBrokerLocal cleanBroker;

    /**
     * Hsphere interface stub so the test will not actually communicate with H-Sphere.
     */

    @Stateless
    public static class HsphereInterfaceStub implements HsphereInterfaceLocal {

        @PersistenceContext
        private EntityManager em;

        @TransactionAttribute(TransactionAttributeType.SUPPORTS)
        public AugmentedInfo queryAugmentInfo(ControlPanel controlPanel, String domainName) {
            AugmentedInfo augment = new AugmentedInfo();

            augment.controlPanel = controlPanel;
            augment.webServer = em.find(WebServer.class, 1);
            augment.webUsername = "testuser";
            augment.accountId = 4;

            return augment;
        }

    }

    /**
     * getAllWorkOrders should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testGetAllWorkOrders() throws Exception {
        List<CleanWorkRequest> workOrders = cleanBroker.getAllWorkOrders();

        assertFalse(workOrders.isEmpty());
    }

    /**
     * getWorkOrders should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testGetWorkOrders() throws Exception {
        List<CleanWorkRequest> workOrders = cleanBroker.getWorkOrders("web.opentransfer.com", false);

        assertFalse(workOrders.isEmpty());
    }

    /**
     * getWorkOrders should fail if a non-existing web server is passed in.
     *
     * @throws Exception
     */

    @Test(expected = WebServerNotFoundException.class)
    public void testGetWorkOrdersFailsIfInvalidWebServer() throws Exception {
        cleanBroker.getWorkOrders("invalid.opentransfer.com", false);
    }

    /**
     * getWorkOrders should add WebServerActivity records if instructed to do so.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testGetWorkOrdersRecordsActivityIfInstructed() throws Exception {
        cleanBroker.getWorkOrders("web.opentransfer.com", true);

        List<WebServerActivity> activity = em.createQuery(
                "from WebServerActivity activity " +
                "where activity.webServer.name = :name")
                .setParameter("name", "web.opentransfer.com")
                .getResultList();

        assertFalse(activity.isEmpty());
    }

    /**
     * getWorkOrders should not add WebServerActivity records if not instructed to do so.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testGetWorkOrdersRecordsActivityIfNotInstructed() throws Exception {
        cleanBroker.getWorkOrders("web.opentransfer.com", false);

        List<WebServerActivity> activity = em.createQuery(
                "from WebServerActivity activity " +
                "where activity.webServer.name = :name")
                .setParameter("name", "web.opentransfer.com")
                .getResultList();

        assertTrue(activity.isEmpty());
    }

    /**
     * getWorkOrders should update the queryTime in the PendingClean records
     * it returns if instructed to do so.
     *
     * @throws Exception
     */

    @Test
    public void testGetWorkOrdersUpdatesQueryTimeIfInstructed() throws Exception {
        List<CleanWorkRequest> workOrders = cleanBroker.getWorkOrders("web.opentransfer.com", true);

        PendingClean pending = (PendingClean) em.createQuery(
                "from PendingClean pending " +
                "where pending.id = :pendingId")
                .setParameter("pendingId", workOrders.get(0).getWorkUnitId())
                .getSingleResult();

        assertNotNull(pending.getQueryTime());
    }

    /**
     * getWorkOrders should not update the queryTime in the PendingClean
     * records it returns if not instructed to do so.
     *
     * @throws Exception
     */

    @Test
    public void testGetWorkOrdersUpdatesQueryTimeIfNotInstructed() throws Exception {
        List<CleanWorkRequest> workOrders = cleanBroker.getWorkOrders("web.opentransfer.com", false);

        PendingClean pending = (PendingClean) em.createQuery(
                "from PendingClean pending " +
                "where pending.id = :pendingId")
                .setParameter("pendingId", workOrders.get(0).getWorkUnitId())
                .getSingleResult();

        assertNull(pending.getQueryTime());
    }

    /**
     * submitWorkResult should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testSubmitWorkResult() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(1);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);
    }

    /**
     * submitWorkResult should fail if a non-existing work unit is passed in.
     *
     * @throws Exception
     */

    @Test(expected = CleanWorkUnitNotFoundException.class)
    public void testSubmitWorkResultFailsIfInvalidWorkUnit() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(0);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);
    }

    /**
     * submitWorkResult should fail if a non-existing web server is passed in.
     *
     * @throws Exception
     */

    @Test(expected = WebServerNotFoundException.class)
    public void testSubmitWorkResultFailsIfInvalidWebServer() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(1);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("invalid.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);
    }

    /**
     * submitWorkResult should fail if the result is submitted by a different
     * web server than the work unit was originally given to.
     *
     * @throws Exception
     */

    @Test(expected = WebServerMismatchException.class)
    public void testSubmitWorkResultFailsIfWebServerMismatch() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(1);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("mismatch.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);
    }

    /**
     * submitWorkResult should fail if the result refers to a non-existing
     * revision.
     *
     * @throws Exception
     */

    @Test(expected = RevisionNotFoundException.class)
    public void testSubmitWorkResultFailsIfInvalidRevision() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(1);
        workResult.setRuleSetId(0);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);
    }

    /**
     * submitWorkResult should remove the PendingClean record it processes.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testSubmitWorkResultRemovesPendingClean() throws Exception {
        long pendingCleanId = 1L;

        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(pendingCleanId);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);

        List<PendingClean> pending = em.createQuery(
                "from PendingClean pending " +
                "where pending.id = :pendingId")
                .setParameter("pendingId", pendingCleanId)
                .getResultList();

        assertTrue(pending.isEmpty());
    }

    /**
     * submitWorkResult should adds a CleanHistory record.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testSubmitWorkResultAddsCleanHistory() throws Exception {
        long pendingCleanId = 1L;

        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(pendingCleanId);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);

        List<CleanHistory> history = em.createQuery(
                "from CleanHistory history " +
                "where history.id = :historyId")
                .setParameter("historyId", pendingCleanId)
                .getResultList();

        assertFalse(history.isEmpty());
    }

    /**
     * submitWorkResult should produces a DomainCleaned message
     *
     * @throws Exception
     */

    @Test
    public void testSubmitWorkProducesDomainCleanedMessage() throws Exception {
        CleanWorkResponse workResult = new CleanWorkResponse();

        workResult.setWorkUnitId(1);
        workResult.setRuleSetId(1);
        workResult.setStartTime(new Date());
        workResult.setFinishTime(new Date());
        workResult.setWebServer("web.opentransfer.com");
        workResult.setFiles(new ArrayList<CleanWorkResponse.File>());

        cleanBroker.submitWorkResult(workResult);

        List<Message> messages = EjbTestHelper.browseMessages(ctx,
                DasientIntegrationConstants.DOMAINCLEANED_QUEUE, null, Message.class);

        assertFalse(messages.isEmpty());
    }

    /**
     * scheduleClean should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testScheduleClean() throws Exception {
        cleanBroker.scheduleClean("cp.opentransfer.com", "test.com");
    }

    /**
     * scheduleClean should fail if a non-existing control panel is passsed.
     *
     * @throws Exception
     */

    @Test(expected = ControlPanelNotFoundException.class)
    public void testScheduleCleanFailsIfInvalidControlPanel() throws Exception {
        cleanBroker.scheduleClean("invalid.opentransfer.com", "test.com");
    }

    /**
     * scheduleClean should fail if a non-existing domain is passed.
     *
     * @throws Exception
     */

    @Test(expected = DomainNotFoundException.class)
    public void testScheduleCleanFailsIfInvalidDomain() throws Exception {
        cleanBroker.scheduleClean("cp.opentransfer.com", "invalid.com");
    }

    /**
     * scheduleClean should produce a CleanDomain message.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleCleanCreatesPendingClean() throws Exception {
        cleanBroker.scheduleClean("cp.opentransfer.com", "test.com");

        List<PendingClean> pending = em.createQuery(
                "from PendingClean pending " +
                "where pending.accountId = :accountId")
                .setParameter("accountId", 4)
                .getResultList();

        assertFalse(pending.isEmpty());
    }

    /**
     * scheduleCleanTriggeredByInfection should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testScheduleCleanTriggeredByInfection() throws Exception {
        int accountId = 2;
        long pendingCleanId = 1L;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId,
                pendingCleanId);
    }

    /**
     * scheduleCleanTriggeredByInfection should fail if a non-existing control panel is passed.
     *
     * @throws Exception
     */

    @Test(expected = ControlPanelNotFoundException.class)
    public void testScheduleCleanTriggeredByInfectionFailsIfInvalidControlPanel() throws Exception {
        int accountId = 2;
        long pendingCleanId = 1L;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "invalid.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId,
                pendingCleanId);
    }

    /**
     * scheduleCleanTriggeredByInfection should fail if a non-existing web server is passed.
     *
     * @throws Exception
     */

    @Test(expected = WebServerNotFoundException.class)
    public void testScheduleCleanTriggeredByInfectionFailsIfInvalidWebServer() throws Exception {
        int accountId = 2;
        long pendingCleanId = 1L;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "invalid.opentransfer.com",
                "testuser",
                accountId,
                pendingCleanId);
    }

    /**
     * scheduleCleanTriggeredByInfection should fail if a non-existing scan history record is passed.
     *
     * @throws Exception
     */

    @Test(expected = ScanHistoryNotFoundException.class)
    public void testScheduleCleanTriggeredByInfectionFailsIfInvalidScanHistory() throws Exception {
        int accountId = 2;
        long pendingCleanId = 3L;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId,
                pendingCleanId);
    }

    /**
     * scheduleCleanTriggeredByInfection should create a PendingClean record,
     * with an association to the ScanHistory record that initiated the action.
     * 
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleCleanTriggeredByInfectionCreatesPendingClean() throws Exception {
        final int accountId = 2;
        final long scanHistoryId = 1;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId,
                scanHistoryId);

        EjbTestHelper.withinTransaction(ctx, new EjbTestHelper.Work() {
            public void doWork() throws Exception {
                List<PendingClean> pending = em.createQuery(
                        "from PendingClean pending " +
                        "where pending.accountId = :accountId")
                        .setParameter("accountId", accountId)
                        .getResultList();

                assertFalse(pending.isEmpty());

                // Verify that the association to ScanHistory is there
                assertTrue(pending.get(0).getTriggers().contains(
                        em.find(ScanHistory.class, scanHistoryId)));
            }
        });
    }

    /**
     * scheduleCleanTriggeredByInfection should consolidate PendingClean
     * records for the same account into a single record with multiple
     * associations to ScanHistory.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleCleanTriggeredByInfectionConsolidatesPendingCleans() throws Exception {
        final int accountId = 1;
        final long scanHistoryId1 = 1;
        final long scanHistoryId2 = 2;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId, scanHistoryId1);

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId, scanHistoryId2);

        EjbTestHelper.withinTransaction(ctx, new EjbTestHelper.Work() {
            public void doWork() throws Exception {
                List<PendingClean> pending = em.createQuery(
                        "from PendingClean pending " +
                        "where pending.accountId = :accountId")
                        .setParameter("accountId", accountId)
                        .getResultList();

                assertFalse(pending.isEmpty());

                // Verify that the association to ScanHistory is there
                assertTrue(pending.get(0).getTriggers().contains(
                        em.find(ScanHistory.class, scanHistoryId1)));

                assertTrue(pending.get(0).getTriggers().contains(
                        em.find(ScanHistory.class, scanHistoryId2)));
            }
        });
    }

    /**
     * scheduleCleanTriggeredByInfection should ignore a second invocation
     * with the same ScanHistory/AccountId combination.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleCleanTriggeredByInfectionIgnoresDuplicateInvocation() throws Exception {
        final int accountId = 2;
        final long scanHistoryId = 2L;

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId, scanHistoryId);

        cleanBroker.scheduleCleanTriggeredByInfection(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId, scanHistoryId);

        EjbTestHelper.withinTransaction(ctx, new EjbTestHelper.Work() {
            public void doWork() throws Exception {
                List<PendingClean> pending = em.createQuery(
                        "from PendingClean pending " +
                        "where pending.accountId = :accountId")
                        .setParameter("accountId", accountId)
                        .getResultList();

                assertFalse(pending.isEmpty());

                // A single association only
                assertEquals(1, pending.get(0).getTriggers().size());
            }
        });
    }

    /**
     * scheduleCleanWithAugmentedInfo should execute successfully.
     *
     * @throws Exception
     */

    @Test
    public void testScheduleCleanWithAugmentedInfo() throws Exception {
        int accountId = 2;

        cleanBroker.scheduleCleanWithAugmentedInfo(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId);
    }

    /**
     * scheduleCleanWithAugmentedInfo should fail if a non-existing control panel is passed.
     *
     * @throws Exception
     */

    @Test(expected = ControlPanelNotFoundException.class)
    public void testScheduleCleanWithAugmentedInfoFailsIfInvalidControlPanel() throws Exception {
        int accountId = 2;

        cleanBroker.scheduleCleanWithAugmentedInfo(
                "invalid.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId);
    }

    /**
     * scheduleCleanWithAugmentedInfo should fail if a non-existing web server is passed.
     *
     * @throws Exception
     */

    @Test(expected = WebServerNotFoundException.class)
    public void testScheduleCleanWithAugmentedInfoFailsIfInvalidWebServer() throws Exception {
        int accountId = 2;

        cleanBroker.scheduleCleanWithAugmentedInfo(
                "cp.opentransfer.com",
                "invalid.opentransfer.com",
                "testuser",
                accountId);
    }

    /**
     * scheduleCleanWithAugmentedInfo should create a PendingClean record.
     *
     * @throws Exception
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testScheduleCleanWithAugmentedInfoCreatesPendingClean() throws Exception {
        int accountId = 2;

        cleanBroker.scheduleCleanWithAugmentedInfo(
                "cp.opentransfer.com",
                "web.opentransfer.com",
                "testuser",
                accountId);

        List<PendingClean> pending = em.createQuery(
                "from PendingClean pending " +
                "where pending.accountId = :accountId")
                .setParameter("accountId", accountId)
                .getResultList();

        assertFalse(pending.isEmpty());
    }

}
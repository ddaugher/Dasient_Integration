package com.ecommerce.clients;

import com.ecommerce.dasient.model.ControlPanel;
import com.ecommerce.utils.DasientIntegrationConstants;
import java.util.Date;
import java.util.List;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.ResourceAdapter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.jobs.ee.ejb.EJB3InvokerJob;

/**
 * A Job to synchronize data with the H-Sphere control panels.
 *
 * The purpose of this job is to spawn other jobs, one for each control panel.
 *
 * Using a separate jobs per control panel has the advantage that if a connection
 * to one control panel has issues, that does not stop the others from syncing.
 *
 * Another advantage is that all control panels will sync in parallel,
 * not one after each other.
 *
 * @see HsphereSyncJob
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "cronTrigger", propertyValue = DasientIntegrationConstants.HSPHERE_SYNC_CRON_SCHEDULE)
})
@ResourceAdapter("quartz-ra.rar")
@TransactionManagement(TransactionManagementType.CONTAINER)
// This might fix the "org.jboss.aop.DispatcherConnectException: EJB container is not completely started, or is stopped."
// which *rarely* occurs during JBoss startup (can't reliably reproduce the issue):
//@Depends({
//    "jboss.ejb:service=EJBDeployer",
//    "jboss.j2ee:ear=DasientIntegration.ear,jar=DasientIntegration-ejb.jar,name=HsphereSyncJob,service=EJB3"
//})
public class HsphereSyncJob implements Job {

    private static final Logger logger = Logger.getLogger(HsphereSyncJob.class);

    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    /**
     * Spawns a sub-job for each control panel that data should be synced with.
     *
     * @param jobExecutionContext execution context of this job
     * @throws JobExecutionException if the scheduler is not able to schedule the sub-jobs
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("HsphereSyncJob started");

        // (JBoss 5.1) By default the thread a Quartz job runs in is using the
        // Quartz ClassLoader, which means that no classes are available that
        // the Quartz ClassLoader can not resolve.
        //
        // JBoss has a strong ClassLoader isolation, which means the EAR and
        // Quartz are using separate ClassLoaders. Quartz will not be able to
        // resolve any classes defined within the EAR, or classes defined in
        // libraries contained in the EAR.
        //
        // That in turn means for example that attempting to utilize any EJBs
        // will fail because their local/remote interfaces can't be resolved.
        //
        // As a workaround we tell this thread to use this EARs ClassLoader
        // while the job is running.

        ClassLoader earlierClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            List<ControlPanel> controlPanels = getControlPanels();

            try {
                for (ControlPanel controlPanel : controlPanels) {
                    try {
                        scheduleSyncWithControlPanel(jobExecutionContext.getScheduler(), controlPanel);
                    } catch (ObjectAlreadyExistsException exc) {
                        // This is recoverable, it merily means the job for this control panel is running already.
                        // We ignore it and just continue with the next control panel.
                        logger.info(String.format(
                                "Sync Job for H-Sphere CP%d is already running, skipping",
                                controlPanel.getId()));
                    }
                }
            } catch (SchedulerException exc) {

                // Should log it as ERROR here, because Quartz will log
                // a JobExecutionException as INFO only.
                logger.error(exc);

                throw new JobExecutionException(exc);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(earlierClassLoader);
        }

        logger.debug("HsphereSyncJob finished");
    }

    /**
     * Returns a list of control panels that should be synced with.
     *
     * It is possible to temporarily turn of syncing with a control panel by setting a flag in the database.
     * This method will filter out control panels for which the syncing is disabled.
     *
     * @return a list of control panels that should be synced with
     */
    @SuppressWarnings("unchecked")
    private List<ControlPanel> getControlPanels() {
        return dasientEM.createNamedQuery("getControlPanelsToSync").getResultList();
    }

    /**
     * Schedules a job to sync with a single H-Sphere control panel.
     *
     * The job is scheduled to run only once and to fire immediately.
     *
     * @param scheduler the scheduler that manages the jobs
     * @param controlPanel the control panel to schedule the sync for
     * @throws SchedulerException if the scheduler is not able to schedule the job
     */
    private void scheduleSyncWithControlPanel(Scheduler scheduler, ControlPanel controlPanel) throws SchedulerException {
        // No two jobs with the same name can run simultaneously.
        //
        // Setting the job name to contain the H-Sphere CP ID serves as a way
        // to prevent running two sync jobs for the same CP simultaneously.
        //
        // If a job with the same name is already running, Quartz throws a
        // org.quartz.ObjectAlreadyExistsException

        JobDetail jobDetail = new JobDetail();
        jobDetail.setName(String.format("hsphere-cp%d-sync", controlPanel.getId()));
        jobDetail.setGroup("hsphere-sync");
        jobDetail.setJobClass(EJB3InvokerJob.class);

        JobDataMap params = jobDetail.getJobDataMap();
        params.put(EJB3InvokerJob.CONTEXT_CLASS_LOADER, getClass().getClassLoader());
        params.put(EJB3InvokerJob.EJB_JNDI_NAME_KEY, "DasientIntegration/HsphereSyncBean/local");
        params.put(EJB3InvokerJob.EJB_INTERFACE_NAME_KEY, HsphereSyncLocal.class.getName());
        params.put(EJB3InvokerJob.EJB_METHOD_KEY, "syncControlPanel");
        params.put(EJB3InvokerJob.EJB_ARGS_KEY, new Object[]{controlPanel.getId()});
        params.put(EJB3InvokerJob.EJB_ARG_TYPES_KEY, new Class<?>[]{int.class});

        SimpleTrigger jobTrigger = new SimpleTrigger();

        jobTrigger.setName(String.format("hsphere-cp%d-sync", controlPanel.getId()));
        jobTrigger.setGroup("hsphere-sync");

        jobTrigger.setStartTime(new Date());
        jobTrigger.setRepeatCount(0);

        scheduler.scheduleJob(jobDetail, jobTrigger);
    }
}

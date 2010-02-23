package com.ecommerce.clients;

import com.ecommerce.utils.DasientIntegrationConstants;
import java.util.Date;
import org.apache.log4j.Logger;
import javax.ejb.MessageDriven;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import javax.ejb.ActivationConfigProperty;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.ejb3.annotation.ResourceAdapter;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "cronTrigger", propertyValue = DasientIntegrationConstants.ARCHIVER_CRON_SCHEDULE)
})
@ResourceAdapter("quartz-ra.rar")
public class Archiver implements Job {

    private static final Logger logger = Logger.getLogger(Archiver.class);

    private static final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

	@PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

	public void execute(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {

		logger.info("Archiver job initiated");

        truncateWebServerActivity();
    }

    private Date getNowMinusXDays(int maxAge) {
        Date threshold = new Date();

        threshold.setTime(threshold.getTime() - maxAge * MILLISECONDS_PER_DAY);

        return threshold;
    }

    private void truncateWebServerActivity() {
        Integer maxAge = Integer.getInteger("web_server_activity_max_age", 7);

        int rowCount = dasientEM.createNamedQuery("truncateWebServerActivity")
                .setParameter("threshold", getNowMinusXDays(maxAge))
                .executeUpdate();

		logger.info(String.format("Truncated %d web server activity records", rowCount));
	}

}

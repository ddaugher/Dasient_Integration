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
import com.ecommerce.dasient.model.PendingClean;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.dasient.vo.CleanWorkResponse;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for the clean work broker.
 */

@Local
public interface CleanBrokerLocal {

    public enum Status {
        PENDING,
        COMPLETED
    }

    /**
     * Returns the status of a specific work unit
     *
     * @param workUnitId The identifier of the work unit
     * @return unkown if the work unit does not exist, either PENDING or COMPLETED
     * @throws CleanWorkUnitNotFoundException if there is no work unit with that identifier
     */

    Status getWorkUnitStatus(long workUnitId)
            throws CleanWorkUnitNotFoundException;

    /**
     * Returns a list of all clean work orders currently in the queue.
     * 
     * @return a collection of clean work orders that are currently scheduled.
     */

    List<CleanWorkRequest> getAllWorkOrders();

    /**
     * Returns a list of clean work orders currently in the queue.
     *
     * The servlet that is supposed to call this method should have already
     * authenticated the web server, no additional authentication is performed
     * by this method.
     *
     * If the request to browse the work orders was initiated by a web server,
     * recordActivity should be set to true. This will record the web servers
     * activity in the database. Web servers that are not regularily reporting
     * activity can be considered offline.
     *
     * The specified web server name may be null, in which case all clean work
     * orders in the queue will be returned, regardless of the web server they
     * are for.
     *
     * This method will not consume the clean work orders, it just browses
     * them. Subsequent calls to this method will return the same result. The
     * method submitWorkResult is responsible for consuming the work orders.
     *
     * @param webServerName the fully qualified domain name of the web server to return the work orders for, or null to return work orders for all web servers.
     * @param recordActivity true, if activity from the web server should be recorded.
     * @return a collection of clean work orders that are currently scheduled.
     * @throws WebServerNotFoundException if the specified web server was not found in the database.
     */
    List<CleanWorkRequest> getWorkOrders(String webServerName, boolean recordActivity)
            throws WebServerNotFoundException;

    /**
     * Records the result of a clean work order.
     *
     * This will consume the work order, subsequent calls to getWorkOrders()
     * will not return the given clean work order any more.
     *
     * Attaching a backup file is optional, workResult.getBackupStream() can
     * be left as null when no backup is needed.
     *
     * @param workResult the details provided by the web server that processed the clean domain work unit.
     * @throws CleanWorkUnitNotFoundException if no work unit could be found with the gived workUnitId.
     * @throws WebServerNotFoundException if the specified web server was not found in the database.
     * @throws WebServerMismatchException if the web server that reported the result is not the same as the one the work order was given to.
     * @throws PersistBackupException if the bean failed to persist the backup stream.
     * @throws RevisionNotFoundException if the work result refers to a non-existing revision.
     * @throws RuleNotFoundException if there is a rule id that referes to a non-existing rule or a rule that did not exist during the given revision.
     */
    CleanHistory submitWorkResult(CleanWorkResponse workResult)
            throws CleanWorkUnitNotFoundException, WebServerNotFoundException,
                   WebServerMismatchException, PersistBackupException,
                   RevisionNotFoundException, RuleNotFoundException;

    /**
     * Adds a clean domain request to the queue.
     *
     * @param controlPanelName the fully qualified domain name of the control panel the domain is hosted at.
     * @param domainName the fully qualified domain name of the domain to clean.
     * @throws ControlPanelNotFoundException if there is no known control panel with the specified name
     * @throws DomainNotFoundException if there is no known domain with the specified name
     */
    PendingClean scheduleClean(String controlPanelName, String domainName)
            throws ControlPanelNotFoundException, DomainNotFoundException;

    /**
     * Adds a clean domain request that was triggered by an infection to the queue.
     *
     * This method should be used to schedule a clean, if all information
     * needed for performing the clean is known.
     *
     * @param controlPanelName the fully qualified domain name of the control panel the domain is hosted at.
     * @param webServerName the name of the web server that the domain is hosted on
     * @param webUsername the name of the user on the web server that owns the files to be cleaned
     * @param accountId the H-Sphere account id of the account ot be cleaned
     * @param triggeredByScanHistoryId the id of the ScanHistory record that triggered the clean
     * @throws ControlPanelNotFoundException if there is no known control panel with the specified name
     * @throws WebServerNotFoundException if there is no known web server with the specified name
     * @throws ScanHistoryNotFoundException if the specified scan history record can not be found in the database
     */
    PendingClean scheduleCleanTriggeredByInfection(String controlPanelName, String webServerName,
            String webUsername, int accountId, long triggeredByScanHistoryId)
            throws ControlPanelNotFoundException, WebServerNotFoundException, ScanHistoryNotFoundException;

    /**
     * Adds a clean domain request with all info necessary to perform the clean.
     *
     * This method should be used if all information about the clean is known,
     * to avoid extra roundtrips to H-Sphere.
     *
     * @param controlPanelName the fully qualified domain name of the control panel the domain is hosted at.
     * @param webServerName the name of the web server that the domain is hosted on
     * @param webUsername the name of the user on the web server that owns the files to be cleaned
     * @param accountId the H-Sphere account id of the account ot be cleaned
     * @throws ControlPanelNotFoundException if there is no known control panel with the specified name
     * @throws WebServerNotFoundException if there is no known web server with the specified name
     * @throws ScanHistoryNotFoundException if the specified scan history record can not be found in the database
     */

    PendingClean scheduleCleanWithAugmentedInfo(String controlPanelName, String webServerName,
            String webUsername, int accountId) throws ControlPanelNotFoundException, WebServerNotFoundException;
}

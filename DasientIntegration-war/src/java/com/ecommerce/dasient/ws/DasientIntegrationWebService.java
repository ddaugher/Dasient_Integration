package com.ecommerce.dasient.ws;

import com.ecommerce.dasient.exceptions.DasientWebServiceException;
import com.ecommerce.dasient.model.PendingClean;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.sbs.CleanBrokerLocal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * A web service serving as general-purpose interface for the external world to talk with the application.
 */
@WebService
public class DasientIntegrationWebService {

    /**
     * Returns a list of all work units currently in the queue.
     *
     * @param domainName the fully qualified domain name of the domain to clean.
     * @throws DasientWebServiceException in case of an error
     */

    @WebMethod
    public CleanWorkRequest[] getAllWorkUnits()
        throws DasientWebServiceException {

        try {
            CleanBrokerLocal cleanBroker = (CleanBrokerLocal) new InitialContext()
                    .lookup("DasientIntegration/CleanBrokerBean/local");

            List<CleanWorkRequest> pending = cleanBroker.getAllWorkOrders();

            return pending.toArray(new CleanWorkRequest[pending.size()]);

        } catch (NamingException exc) {
            throw new DasientWebServiceException(exc);
        } catch (RuntimeException exc) {
            throw new DasientWebServiceException(exc);
        }

    }

    /**
     * Queries whether a work unit is still PENDING or already COMPLETED.
     *
     * @param workUnitId the identifier of the work unit to query the status of.
     * @throws DasientWebServiceException if a work unit with the given identifier does not exist.
     */

    @WebMethod
    public CleanBrokerLocal.Status getWorkUnitStatus(
            @WebParam(name = "workUnitId") long workUnitId)
            throws DasientWebServiceException {

        try {
            CleanBrokerLocal cleanBroker = (CleanBrokerLocal) new InitialContext()
                    .lookup("DasientIntegration/CleanBrokerBean/local");

            return cleanBroker.getWorkUnitStatus(workUnitId);

        } catch (NamingException exc) {
            throw new DasientWebServiceException(exc);
        } catch (RuntimeException exc) {
            throw new DasientWebServiceException(exc);
        }
    }

    /**
     * Adds a clean domain request to the queue.
     *
     * @param controlPanel the fully qualified domain name of the control panel the domain is hosted at.
     * @param domainName the fully qualified domain name of the domain to clean.
     * @return the Id of the pending clean record that was created in the database
     * @throws DasientWebServiceException in case of an error
     */
    @WebMethod
    public long scheduleClean(
            @WebParam(name = "controlPanel") String controlPanel,
            @WebParam(name = "domainName") String domainName)
            throws DasientWebServiceException {

        try {
            CleanBrokerLocal cleanBroker = (CleanBrokerLocal) new InitialContext()
                    .lookup("DasientIntegration/CleanBrokerBean/local");

            PendingClean pending = cleanBroker.scheduleClean(controlPanel, domainName);

            return pending.getId();

        } catch (NamingException exc) {
            throw new DasientWebServiceException(exc);
        } catch (RuntimeException exc) {
            throw new DasientWebServiceException(exc);
        }
    }

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
     * @return the Id of the pending clean record that was created in the database
     * @throws DasientWebServiceException in case of an error
     */
    @WebMethod
    public long scheduleCleanWithAugmentedInfo(
            @WebParam(name = "controlPanel") String controlPanelName,
            @WebParam(name = "webServer") String webServerName,
            @WebParam(name = "webUsername") String webUsername,
            @WebParam(name = "accountId") int accountId)
            throws DasientWebServiceException {

        try {
            CleanBrokerLocal cleanBroker = (CleanBrokerLocal) new InitialContext()
                    .lookup("DasientIntegration/CleanBrokerBean/local");

            PendingClean pending = cleanBroker.scheduleCleanWithAugmentedInfo(
                    controlPanelName, webServerName,
                    webUsername, accountId);

            return pending.getId();

        } catch (NamingException exc) {
            throw new DasientWebServiceException(exc);
        } catch (RuntimeException exc) {
            throw new DasientWebServiceException(exc);
        }
    }

}

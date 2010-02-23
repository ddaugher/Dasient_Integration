package com.ecommerce.clients;

import com.ecommerce.dasient.model.Domain;
import com.ecommerce.dasient.model.ScanHistory;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.ejb3.annotation.LocalBinding;

/**
 *
 * @author djdaugherty
 */
@Stateless
@LocalBinding(jndiBinding = "DasientIntegration/ProcessScanResultBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ProcessScanResultBean implements ProcessScanResultLocal {

	private static final Logger logger = Logger.getLogger(ProcessScanResultBean.class);
	@PersistenceContext(unitName = "DasientPU")
	private EntityManager dasientEM;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Domain getDomainById(Long domainId, String domainName) {

		Domain domain = null;
		try {
			// retrieve named domain from DB
			//int id = new Integer(Long.toString(domainId)).intValue();
			domain = (Domain) dasientEM.createNamedQuery("getDomainById").setParameter("id", domainId).getSingleResult();

		} catch (NoResultException exc) {

			if (domainName != null) {
				throw new NoResultException(String.format("No Domain entity with name: %d found", domainName));
			}

		}

		return domain;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void persistDomainNextScanUpdate(Long domainId, String domainName, Date nextScanDate) {

        logger.debug(String.format("Setting next scan date of domain %2$s (%1$d) to %3$tF %3$tT %3$tZ",
                domainId, domainName, nextScanDate));

		Domain domain = null;
		try {
			// retrieve named domain from DB
			//int id = new Integer(Long.toString(domainId)).intValue();
			domain = (Domain) dasientEM.createNamedQuery("getDomainById").setParameter("id", domainId).getSingleResult();

			// update the next scan date value
			domain.setNextScan(nextScanDate);
			domain.setStatus(null);

			// persist to the DB
			dasientEM.persist(domain);

        } catch (NoResultException exc) {

            throw new NoResultException(String.format("No Domain entity with id: %d found", domainId));

		}
    }


	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
	public int hasRejectedScansInARow(String domainName, int limitResultCount) throws NoResultException {

		Collection<ScanHistory> scanHistorys;
		int count = 0;

		try {

			logger.info("retrieving scan history entities for domain " + domainName + " in order to determine number of rejections within last : " + limitResultCount + " scans has occurred!");
			Query q = dasientEM.createNamedQuery("getScanHistoryEntities").setParameter("hostname", domainName);
			q.setMaxResults(limitResultCount);
			scanHistorys = q.getResultList();

		} catch (NoResultException exc) {

			throw new NoResultException("No ScanHistory entities found!");

		}

		// check to determine if an infected scan is contained within the result set
		for (ScanHistory item : scanHistorys) {

			if ( item.getMaliciousUrls().size() > 0 ) {

				logger.info("rejection found !!! - incrementing counter!");
				count++;

			}
		}

		return count;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
	public boolean getScanHistoryInfectedCountForDomain(String domainName, int limitResultCount) throws NoResultException {

		Collection<ScanHistory> scanHistorys;

		try {

			logger.info("retrieving scan history entities for domain " + domainName + " in order to determine if 'infected' scan within last " + limitResultCount + " has occurred!");
			Query q = dasientEM.createNamedQuery("getScanHistoryEntities").setParameter("hostname", domainName);
			q.setMaxResults(limitResultCount);
			scanHistorys = q.getResultList();

		} catch (NoResultException exc) {

			throw new NoResultException("No ScanHistory entities found!");

		}

		// check to determine if an infected scan is contained within the result set
		for (ScanHistory item : scanHistorys) {

			if ( item.getMaliciousUrls().size() > 0 ) {

				logger.info("infected scan found !!! - returning true!");
				return true;

			}
		}

		return false;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
	public boolean hasBeenRejectedWithinLastScans(String domainName, int limitScanCount) throws NoResultException {

		Collection<ScanHistory> scanHistorys;

		try {

			logger.info("retrieving scan history entities for domain " + domainName + " in order to determine if 'rejected' scan within last " + limitScanCount + " scans has occurred!");
			Query q = dasientEM.createNamedQuery("getScanHistoryEntities").setParameter("hostname", domainName);
			q.setMaxResults(limitScanCount);
			scanHistorys = q.getResultList();

		} catch (NoResultException exc) {

			throw new NoResultException("No ScanHistory entities found!");

		}

		// check to determine if an infected scan is contained within the result set
		for (ScanHistory item : scanHistorys) {

			if ( item.getStatus().equalsIgnoreCase("IGNORED") ) {

				logger.info("IGNORED found !!! - returning true!");
				return true;

			}
		}

		return false;
	}


	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
	public int getScanHistoryCountForDomain(String domainName) throws NoResultException {

		Collection<ScanHistory> scanHistorys;

		try {

			logger.info("retrieving scan history entities in order to determine total number of previous scans for domain : " + domainName);
			Query q = dasientEM.createNamedQuery("getScanHistoryEntities").setParameter("hostname", domainName);
			scanHistorys = q.getResultList();

		} catch (NoResultException exc) {

			throw new NoResultException("No ScanHistory entities found!");

		}

		return scanHistorys.size();
	}
}

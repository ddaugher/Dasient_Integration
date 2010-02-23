package com.ecommerce.clients;

import com.ecommerce.dasient.model.Failure;
import com.ecommerce.dasient.model.MaliciousSourceCodeSnippets;
import com.ecommerce.dasient.model.MaliciousUrl;
import com.ecommerce.dasient.model.SuspiciousUrl;
import com.ecommerce.dasient.model.OnBlacklist;
import com.ecommerce.dasient.model.ScanHistory;
import com.ecommerce.dasient.model.ScannedUrl;
import com.ecommerce.dasient.model.SuspiciousSourceCodeSnippets;
import com.ecommerce.dasient.vo.result.DasientJSONResult;
import com.ecommerce.dasient.vo.result.FailureResult;
import com.ecommerce.dasient.vo.result.MaliciousResultUrl;
import com.ecommerce.dasient.vo.result.SuspiciousResultUrl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.jboss.ejb3.annotation.LocalBinding;

/**
 *
 * @author djdaugherty
 */
@Stateless
@LocalBinding(jndiBinding = "DasientIntegration/DasientResultHandlerBean/local")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DasientResultHandlerBean implements DasientResultHandlerLocal {

	private static final Logger logger = Logger.getLogger(DasientResultHandlerBean.class);
	@PersistenceContext(unitName = "DasientPU")
	private EntityManager dasientEM;

	/**
	 * will persist complete ScanHistory entity and all children
	 * @param result DasientJSONResult
	 * @param rawResponse raw json string as returned from dasient
	 * @return id value returned from persistence DB
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ScanHistory persistScanHistory(DasientJSONResult result, String rawResponse) {

		ScanHistory sh = new ScanHistory();
		sh.setCompletedAt(result.getResult().getCompletedAt());
		sh.setHostname(result.getResult().getHostname());
		sh.setRawResponse(rawResponse);
		sh.setRequestId(result.getResult().getRequestId());
		sh.setStatus(result.getResult().getStatus());

		// set OnBlacklist from result
		Set<OnBlacklist> blackListSet = handleOnBlacklistSet(result.getResult().getOnBlacklist(), sh);
		sh.setOnBlacklists(blackListSet);

		// set Scanned Urls from result
		Set<ScannedUrl> scannedUrlSet = handleScannedUrls(result.getResult().getScannedUrls(), sh);
		sh.setScannedUrls(scannedUrlSet);

		// set Malicious Urls from result
		Set<MaliciousUrl> maliciousUrlSet = handleMaliciousUrls(result.getResult().getMaliciousUrls(), sh);
		sh.setMaliciousUrls(maliciousUrlSet);

		// set Suspicious Urls from result
		Set<SuspiciousUrl> suspiciousUrlSet = handleSuspiciousUrls(result.getResult().getSuspiciousUrls(), sh);
		sh.setSuspiciousUrls(suspiciousUrlSet);

		// set Failure from result
		Set<Failure> failureSet = handleFailures(result.getResult().getFailures(), sh);
		sh.setFailures(failureSet);

		dasientEM.persist(sh);

		return sh;
	}

	private Set<OnBlacklist> handleOnBlacklistSet(ArrayList<String> onBlackList, ScanHistory sh) {
		// add the onBlacklist values from the result
		logger.debug("calling handleOnBlacklistSet");
		Set<OnBlacklist> blackListSet = Collections.synchronizedSet(new HashSet<OnBlacklist>());
		Collection<String> blacklist = onBlackList;
		for (String item : blacklist) {
			OnBlacklist bl = new OnBlacklist();
			bl.setName(item.toString());
			bl.setScanHistory(sh);
			logger.debug(String.format("adding %s to the blackListSet", item.toString()));
			blackListSet.add(bl);
		}
		return blackListSet;
	}

	private Set<Failure> handleFailures(ArrayList<FailureResult> failures, ScanHistory sh) {
		// add the failure values from the result
		logger.debug("calling handleFailures");
		Set<Failure> failureSet = Collections.synchronizedSet(new HashSet<Failure>());
		Collection<FailureResult> failure = failures;
		for (FailureResult item : failure) {
			Failure f = new Failure();
			f.setCode(item.getCode());
			f.setReason(item.getReason());
			f.setScanHistory(sh);
			logger.debug(String.format("adding %s to the failureSet", item.toString()));
			failureSet.add(f);
		}
		return failureSet;
	}

	private Set<ScannedUrl> handleScannedUrls(ArrayList<String> scannedUrls, ScanHistory sh) {
		// add the scannedUrls from the result
		Set<ScannedUrl> scannedUrlSet = Collections.synchronizedSet(new HashSet<ScannedUrl>());
		List<String> scannedList = scannedUrls;
		for (String item : scannedList) {
			ScannedUrl su = new ScannedUrl();
			su.setUrl(item.toString());
			su.setScanHistory(sh);
			logger.debug(String.format("adding %s to the scannedUrlSet", item.toString()));
			scannedUrlSet.add(su);
		}
		return scannedUrlSet;
	}

	private Set<MaliciousUrl> handleMaliciousUrls(ArrayList<MaliciousResultUrl> urls, ScanHistory sh) {
		// add the maliciousUrlsSize from the result
		Set<MaliciousUrl> maliciousUrlSet = Collections.synchronizedSet(new HashSet<MaliciousUrl>());
		List<MaliciousResultUrl> maliciousUrls = urls;
		for (Iterator it = maliciousUrls.iterator(); it.hasNext();) {
			// this seems like a hack... not sure why the deserialization of a JSON object via the json serializer
			// makes the object dependent on the MorphDynaBean class
			MorphDynaBean entry = (MorphDynaBean) it.next();
			MaliciousUrl mu = new MaliciousUrl();
			mu.setUrl((String) entry.get("url"));
			MaliciousSourceCodeSnippets snippet = new MaliciousSourceCodeSnippets();
			snippet.setMaliciousUrl(mu);
			//snippet.setSnippet((String)entry.get("maliciousSourceCodeSnippets"));
			Set<MaliciousSourceCodeSnippets> maliciousSourceCodeSet = Collections.synchronizedSet(new HashSet<MaliciousSourceCodeSnippets>());
			Collection g = (ArrayList) entry.get("maliciousSourceCodeSnippets");
			for (Iterator snippets = g.iterator(); snippets.hasNext();) {
				MorphDynaBean snip = (MorphDynaBean) snippets.next();
				MaliciousSourceCodeSnippets scs = new MaliciousSourceCodeSnippets();
				scs.setMaliciousUrl(mu);
				scs.setSnippet((String) snip.get("snippet"));
				maliciousSourceCodeSet.add(scs);
			}
			mu.setMaliciousSourceCodeSnippets(maliciousSourceCodeSet);
			mu.setScanHistory(sh);
			logger.debug(String.format("adding %s to the maliciousUrlSet", entry.get("url")));
			maliciousUrlSet.add(mu);
		}
		return maliciousUrlSet;
	}

	private Set<SuspiciousUrl> handleSuspiciousUrls(ArrayList<SuspiciousResultUrl> urls, ScanHistory sh) {
		Set<SuspiciousUrl> suspiciousUrlSet = Collections.synchronizedSet(new HashSet<SuspiciousUrl>());
		List<SuspiciousResultUrl> suspiciousUrls = urls;
		for (Iterator it = suspiciousUrls.iterator(); it.hasNext();) {
			// this seems like a hack... not sure why the deserialization of a JSON object via the json serializer
			// makes the object dependent on the MorphDynaBean class
			MorphDynaBean entry = (MorphDynaBean) it.next();
			SuspiciousUrl su = new SuspiciousUrl();
			su.setUrl((String) entry.get("url"));
			SuspiciousSourceCodeSnippets snippet = new SuspiciousSourceCodeSnippets();
			snippet.setSuspiciousUrl(su);

			Set<SuspiciousSourceCodeSnippets> suspiciousSourceCodeSet = Collections.synchronizedSet(new HashSet<SuspiciousSourceCodeSnippets>());
			Collection g = (ArrayList) entry.get("suspiciousSourceCodeSnippets");
			for (Iterator snippets = g.iterator(); snippets.hasNext();) {
				MorphDynaBean snip = (MorphDynaBean) snippets.next();
				SuspiciousSourceCodeSnippets scs = new SuspiciousSourceCodeSnippets();
				scs.setSuspiciousUrl(su);
				scs.setSnippet((String) snip.get("snippet"));
				suspiciousSourceCodeSet.add(scs);
			}
			su.setSuspiciousSourceCodeSnippets(suspiciousSourceCodeSet);
			su.setScanHistory(sh);
			logger.debug(String.format("adding %s to the suspiciousUrlSet", entry.get("url")));
			suspiciousUrlSet.add(su);
		}
		return suspiciousUrlSet;
	}
}
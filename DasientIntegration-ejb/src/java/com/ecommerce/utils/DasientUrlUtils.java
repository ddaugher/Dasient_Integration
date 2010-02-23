package com.ecommerce.utils;

import com.ecommerce.dasient.exceptions.UnableToDetermineDasientUrlException;
import org.apache.log4j.Logger;

/**
 * The DasientUrlUtils clss is a helper class to build the full dasient url.
 *
 * @author djdaugherty
 */
public class DasientUrlUtils {

	static Logger logger = Logger.getLogger(DasientUrlUtils.class);

	/**
	 * URI utility class to assist in the creation of project related URI's
	 */
	public DasientUrlUtils() {
	}

	/**
	 * generate the Request URI specific to Dasient... this values are stored in the
	 * dasient.properties file and pulled into the application at runtime.  The dasient.properties
	 * file is managed via the jboss properties listener and can be requeried at runtime by
	 * touching the properties-service.xml file.
	 * @return a fully qualified URI used to submit request to the Dasient API
	 * @throws UnableToDetermineDasientUrlException
	 */
	public static String generateDasientRequestUrl() throws UnableToDetermineDasientUrlException {

		StringBuffer url = new StringBuffer();

		try {
			String dasientUrl = System.getProperty("dasient_url");
			String apiId = System.getProperty("api_id");
			String secretKey = System.getProperty("secret_key");

			// check to determine if any of the required props are null
			if (dasientUrl == null || apiId == null || secretKey == null) {
				throw new Exception();
			}

			url.append(dasientUrl);
			url.append("?api_id=");
			url.append(apiId);
			url.append("&secret_key=");
			url.append(secretKey);

		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new UnableToDetermineDasientUrlException();
		}

		return url.toString();
	}

	/**
	 * retrieves the dasient hostname from the dasient.properties settings
	 * @return hostname
	 * @throws UnableToDetermineDasientUrlException
	 */
	public static String getDasientHostString() throws UnableToDetermineDasientUrlException {

		StringBuffer hostString = new StringBuffer();

		try {
			String dasientUrl = System.getProperty("dasient_url");

			// check to determine if any of the required props are null
			if (dasientUrl == null) {
				throw new Exception();
			}

			hostString.append(dasientUrl);

		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new UnableToDetermineDasientUrlException();
		}

		return hostString.toString();
	}

	/**
	 * retrieves the dasient hostname from the dasient.properties settings
	 * @return query string
	 * @throws UnableToDetermineDasientUrlException
	 */
	public static String getDasientQueryString() throws UnableToDetermineDasientUrlException {

		StringBuffer queryString = new StringBuffer();

		try {
			String apiId = System.getProperty("api_id");
			String secretKey = System.getProperty("secret_key");

			queryString.append("?api_id=");
			queryString.append(apiId);
			queryString.append("&secret_key=");
			queryString.append(secretKey);

		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new UnableToDetermineDasientUrlException();
		}

		return queryString.toString();
	}
}
package com.ecommerce.dasient.broker;

import com.ecommerce.dasient.exceptions.UnableToProcessDasientResultException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import com.ecommerce.dasient.vo.result.DasientJSONResult;
import com.ecommerce.dasient.vo.result.MaliciousResultUrl;
import com.ecommerce.dasient.vo.result.SuspiciousResultUrl;
import com.ecommerce.sbs.DasientResultBrokerLocal;
import com.ecommerce.utils.JSONUtils;
import java.io.BufferedReader;
import net.sf.json.JsonConfig;
import net.sf.json.util.JavaIdentifierTransformer;
import org.apache.log4j.Logger;

public class DasientResultBrokerServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(DasientResultBrokerServlet.class);
	private static String SUBMIT_RESULT_PATH = "/submitResult";
	@EJB(mappedName="DasientIntegration/DasientResultBrokerBean/local")
	private DasientResultBrokerLocal dasientResultBrokerBean;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String method = request.getMethod();
		String pathInfo = request.getPathInfo();

		// accept HTTP POST requests at /result
		if (SUBMIT_RESULT_PATH.equals(pathInfo)) {
			if ("POST".equals(method)) {
				processSubmitResult(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, request.getRequestURI());
			}
		} // anything else is unknown
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
		}

	}

	private void processSubmitResult(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// retrieve json payload from 'request'

			// any request to process ?
			if (request == null) {
				throw new IOException("Invalid request");
			}

			StringBuffer jsonString = new StringBuffer();
			try {

				// read body as String and response it to the client
				BufferedReader br = request.getReader();
				String line = null;
				jsonString = new StringBuffer();

				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();

			} catch (Exception e) {
				// pass it on ...
				throw new IOException("Request failed" + e);
			}

			// TODO : temporary set of jsonString to dummy value for testing
			//jsonString = new StringBuffer(getDummyDasientResult());
			logger.debug("jsonString - " + jsonString);

			// parse json payload into DasientJSONResult Bean
			String jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(DasientJSONResult.class, jsonString.toString());
			jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(MaliciousResultUrl.class, jsonTemp);
			jsonTemp = JSONUtils.mapJsonAttrsToJavaAttrs(SuspiciousResultUrl.class, jsonTemp);
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJavaIdentifierTransformer(JavaIdentifierTransformer.CAMEL_CASE);
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonTemp, jsonConfig);
			logger.debug("jsonObject - " + jsonObject);

			DasientJSONResult result = (DasientJSONResult) JSONObject.toBean(jsonObject, DasientJSONResult.class);

			boolean includeMaliciousUrls = Boolean.getBoolean("includeMaliciousUrls");
			boolean includeSuspiciousUrls = Boolean.getBoolean("includeSuspiciousUrls");
			boolean scanEverything = Boolean.getBoolean("cleanEverything");

			boolean isInfected = false;

			// setting the 'includeMaliciousUrls' property = true will include the check for inclusion
			boolean containsMaliciousUrls = false;
			if ( includeMaliciousUrls ) {
				containsMaliciousUrls = result.getResult().getMaliciousUrls().size() > 0;
			}

			// setting the 'includeSuspiciousUrls' property = true will include the check for inclusion
			boolean containsSuspiciousUrls = false;
			if ( includeSuspiciousUrls ) {
				containsSuspiciousUrls = result.getResult().getSuspiciousUrls().size() > 0;
			}

			// if the scan contains maliciousUrls or suspiciousUrls, then mark the scan as infected.
			// as an added feature... if for some reason, we want to scan everything, setting the 'scanEverything' flag = true
			// will mark all scans as infected, which will trigger all scans to be cleaned.
			if ( containsMaliciousUrls == true || containsSuspiciousUrls == true || scanEverything == true ) {
				isInfected = true;
			}

            dasientResultBrokerBean.submitResult(result.getResult().getRequestId(), jsonTemp, isInfected);

		} catch (UnableToProcessDasientResultException exc) {
			throw new ServletException("Failed to process incoming Dasient result.", exc);
		} catch (Exception e) {
			throw new ServletException("Failed to process incoming Dasient result.", e);
		} finally {
		}
		// This output serves no purpose, it is just here to mitigate
		// the awkwardness of an empty result
		PrintWriter out = response.getWriter();
		try {
			out.print("result acknowledged");
		} finally {
			out.close();
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// pathInfo not required here... just have not removed the code
		String pathInfo = request.getPathInfo();
		processRequest(request, response);

	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String pathInfo = request.getPathInfo();

		if (SUBMIT_RESULT_PATH.equals(pathInfo)) {
			processSubmitResult(request, response);
		} else {
			processRequest(request, response);
		}

	}

	/**
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Dasient Result Broker Servlet";
	}
}

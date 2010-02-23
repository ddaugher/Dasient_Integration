package com.ecommerce.dasient.vo.request;

import com.ecommerce.utils.JSONUtils;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.JavaIdentifierTransformer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author djdaugherty
 */
public class DasientRequestTest {

	public DasientRequestTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of setRequest method, of class DasientJSONRequest.
	 */
	@Test
	public void testSetRequest() {
		System.out.println("setRequest");
		DasientJSONRequest instance = new DasientJSONRequest();
		Request req = instance.getRequest();
		req.setHostname("http://www.hostname.com");
		instance.setRequest(req);

		assertNotNull(instance);
		assertNotNull(req);
		assertNotNull(instance.getRequest());
		assertEquals("http://www.hostname.com", instance.getRequest().getHostname());
	}

	/**
	 * Test of getRequest method, of class DasientJSONRequest.
	 */
	@Test
	public void testGetRequest() {
		System.out.println("getRequest");
		DasientJSONRequest instance = new DasientJSONRequest();
		Request req = instance.getRequest();
		req.setHostname("http://www.hostname.com");
		instance.setRequest(req);

		assertNotNull(instance);
		assertNotNull(req);
		assertNotNull(instance.getRequest());
		assertEquals("http://www.hostname.com", instance.getRequest().getHostname());
	}

	@Test
	public void testDasientRequestToJson() {
		System.out.println("testDasientRequestToJson");
		DasientJSONRequest dReq = new DasientJSONRequest();
		Request req = dReq.getRequest();
		req.setHostname("http://wwww.test.com");
		req.setRequestType("top-level-scan");
		req.setHostIp("123.123.123.123");
		req.setServerhost("web31.opentransfer.com");
		req.setResponseUrl("http://www.responsetothis.com");

		dReq.setRequest(req);

		String convertedVal = "";

		try {
			convertedVal = JSONUtils.mapObjectToJsonString(dReq);
		} catch (Exception ex) {
			ex.printStackTrace();
			//Logger.getLogger(DasientRequestTest.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println(convertedVal);

		//assertNotNull(jsonObject);
		assertNotNull(convertedVal);
	}

	@Test
	public void testJsonStringToDasientRequest() {
		System.out.println("testJsonStringToDasientRequest");
		DasientJSONRequest dReq = new DasientJSONRequest();
		Request req = dReq.getRequest();
		req.setHostname("http://www.test.com");
		req.setRequestType("top-level-scan");
		req.setHostIp("123.123.123.123");
		req.setServerhost("web31.opentransfer.com");
		req.setResponseUrl("http://www.responsetothis.com");

		dReq.setRequest(req);

		String convertedVal = "";

		try {
			convertedVal = JSONUtils.mapObjectToJsonString(dReq);
		} catch (Exception ex) {
			ex.printStackTrace();
			//Logger.getLogger(DasientRequestTest.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println("converted json string - " + convertedVal);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJavaIdentifierTransformer(JavaIdentifierTransformer.CAMEL_CASE);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(convertedVal, jsonConfig);
		System.out.println("jsonObject - " + jsonObject);

		DasientJSONRequest t = (DasientJSONRequest) JSONObject.toBean(jsonObject, DasientJSONRequest.class);

		assertNotNull(jsonObject);
		assertNotNull(t);
		assertEquals("http://www.test.com", t.getRequest().getHostname());
		assertNotSame("http://www.fail.com", t.getRequest().getHostname());
	}
}

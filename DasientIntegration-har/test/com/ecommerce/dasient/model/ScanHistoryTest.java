/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ecommerce.dasient.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
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
public class ScanHistoryTest {

    public ScanHistoryTest() {
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
	 * Test of getId method, of class ScanHistory.
	 */
	@Test
	public void testGetId() {
		System.out.println("getId");
		ScanHistory instance = new ScanHistory();
		Long expResult = 12345l;
		instance.setId(expResult);
		Long result = instance.getId();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getCreateTms method, of class ScanHistory.
	 */
	@Test
	public void testGetCreateTms() {
		System.out.println("getCreateTms");
		ScanHistory instance = new ScanHistory();
		Date expResult = Calendar.getInstance().getTime();
		instance.setCreateTms(expResult);
		Date result = instance.getCreateTms();
		assertNotNull(result);
	}

	/**
	 * Test of setCreateTms method, of class ScanHistory.
	 */
	@Test
	public void testSetCreateTms() {
		System.out.println("setCreateTms");
		Date tms = Calendar.getInstance().getTime();
		ScanHistory instance = new ScanHistory();
		instance.setCreateTms(tms);

		assertNotNull(instance.getCreateTms());
	}

	/**
	 * Test of getRequestId method, of class ScanHistory.
	 */
	@Test
	public void testGetRequestId() {
		System.out.println("getRequestId");
		ScanHistory instance = new ScanHistory();
		String expResult = "12345";
		instance.setRequestId(expResult);
		String result = instance.getRequestId();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setRequestId method, of class ScanHistory.
	 */
	@Test
	public void testSetRequestId() {
		System.out.println("setRequestId");
		ScanHistory instance = new ScanHistory();
		String requestId = "12345";
		instance.setRequestId(requestId);
		String result = instance.getRequestId();
		assertEquals(requestId, result);
	}

	/**
	 * Test of getCompletedAt method, of class ScanHistory.
	 */
	@Test
	public void testGetCompletedAt() {
		System.out.println("getCompletedAt");
		ScanHistory instance = new ScanHistory();
		String expResult = "111111";
		instance.setCompletedAt(expResult);
		String result = instance.getCompletedAt();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setCompletedAt method, of class ScanHistory.
	 */
	@Test
	public void testSetCompletedAt() {
		System.out.println("setCompletedAt");
		String completedAt = "111111";
		ScanHistory instance = new ScanHistory();
		instance.setCompletedAt(completedAt);
		String result = instance.getCompletedAt();
		assertEquals(completedAt, result);
	}

	/**
	 * Test of getStatus method, of class ScanHistory.
	 */
	@Test
	public void testGetStatus() {
		System.out.println("getStatus");
		ScanHistory instance = new ScanHistory();
		String expResult = "COMPLETED";
		instance.setStatus(expResult);
		String result = instance.getStatus();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setStatus method, of class ScanHistory.
	 */
	@Test
	public void testSetStatus() {
		System.out.println("setStatus");
		ScanHistory instance = new ScanHistory();
		String expResult = "COMPLETED";
		instance.setStatus(expResult);
		String result = instance.getStatus();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getHostname method, of class ScanHistory.
	 */
	@Test
	public void testGetHostname() {
		System.out.println("getHostname");
		ScanHistory instance = new ScanHistory();
		String expResult = "www.testname.com";
		instance.setHostname(expResult);
		String result = instance.getHostname();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setHostname method, of class ScanHistory.
	 */
	@Test
	public void testSetHostname() {
		System.out.println("setHostname");
		ScanHistory instance = new ScanHistory();
		String expResult = "www.testname.com";
		instance.setHostname(expResult);
		String result = instance.getHostname();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getOnBlacklists method, of class ScanHistory.
	 */
	@Test
	public void testGetOnBlacklists() {
		System.out.println("getOnBlacklists");
		ScanHistory instance = new ScanHistory();
		Set expResult = null;
		Set result = instance.getOnBlacklists();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setOnBlacklists method, of class ScanHistory.
	 */
	@Test
	public void testSetOnBlacklists() {
		System.out.println("setOnBlacklists");
		Set<OnBlacklist> onBlacklists = null;
		ScanHistory instance = new ScanHistory();
		instance.setOnBlacklists(onBlacklists);
	}

	/**
	 * Test of getScannedUrls method, of class ScanHistory.
	 */
	@Test
	public void testGetScannedUrls() {
		System.out.println("getScannedUrls");
		ScanHistory instance = new ScanHistory();
		Set expResult = null;
		Set result = instance.getScannedUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setScannedUrls method, of class ScanHistory.
	 */
	@Test
	public void testSetScannedUrls() {
		System.out.println("setScannedUrls");
		Set<ScannedUrl> scannedUrls = null;
		ScanHistory instance = new ScanHistory();
		instance.setScannedUrls(scannedUrls);
	}

	/**
	 * Test of getMaliciousUrls method, of class ScanHistory.
	 */
	@Test
	public void testGetMaliciousUrls() {
		System.out.println("getMaliciousUrls");
		ScanHistory instance = new ScanHistory();
		Set expResult = null;
		Set result = instance.getMaliciousUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setMaliciousUrls method, of class ScanHistory.
	 */
	@Test
	public void testSetMaliciousUrls() {
		System.out.println("setMaliciousUrls");
		Set<MaliciousUrl> maliciousUrls = null;
		ScanHistory instance = new ScanHistory();
		instance.setMaliciousUrls(maliciousUrls);
	}

	/**
	 * Test of getSuspiciousUrls method, of class ScanHistory.
	 */
	@Test
	public void testGetSuspiciousUrls() {
		System.out.println("getSuspiciousUrls");
		ScanHistory instance = new ScanHistory();
		Set expResult = null;
		Set result = instance.getSuspiciousUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setSuspiciousUrls method, of class ScanHistory.
	 */
	@Test
	public void testSetSuspiciousUrls() {
		System.out.println("setSuspiciousUrls");
		Set<SuspiciousUrl> suspiciousUrls = null;
		ScanHistory instance = new ScanHistory();
		instance.setSuspiciousUrls(suspiciousUrls);
	}

	/**
	 * Test of getIgnoredRequest method, of class ScanHistory.
	 */
	@Test
	public void testGetIgnoredRequest() {
		System.out.println("getIgnoredRequest");
		ScanHistory instance = new ScanHistory();
		IgnoredRequest expResult = null;
		IgnoredRequest result = instance.getIgnoredRequest();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setIgnoredRequest method, of class ScanHistory.
	 */
	@Test
	public void testSetIgnoredRequest() {
		System.out.println("setIgnoredRequest");
		IgnoredRequest ignoredRequest = null;
		ScanHistory instance = new ScanHistory();
		instance.setIgnoredRequest(ignoredRequest);
	}

	/**
	 * Test of getRawRequest method, of class ScanHistory.
	 */
	@Test
	public void testGetRawRequest() {
		System.out.println("getRawRequest");
		ScanHistory instance = new ScanHistory();
		String expResult = "";
		String result = instance.getRawRequest();
		assertNull(result);
	}

	/**
	 * Test of setRawRequest method, of class ScanHistory.
	 */
	@Test
	public void testSetRawRequest() {
		System.out.println("setRawRequest");
		String rawRequest = "";
		ScanHistory instance = new ScanHistory();
		instance.setRawRequest(rawRequest);
	}

	/**
	 * Test of getRawResponse method, of class ScanHistory.
	 */
	@Test
	public void testGetRawResponse() {
		System.out.println("getRawResponse");
		ScanHistory instance = new ScanHistory();
		String expResult = "";
		String result = instance.getRawResponse();
		assertNull(result);
	}

	/**
	 * Test of setRawResponse method, of class ScanHistory.
	 */
	@Test
	public void testSetRawResponse() {
		System.out.println("setRawResponse");
		String rawResponse = "";
		ScanHistory instance = new ScanHistory();
		instance.setRawResponse(rawResponse);
	}

	/**
	 * Test of hashCode method, of class ScanHistory.
	 */
	@Test
	public void testHashCode() {
		System.out.println("hashCode");
		ScanHistory instance = new ScanHistory();
		int expResult = 0;
		int result = instance.hashCode();
		assertEquals(expResult, result);
	}

	/**
	 * Test of equals method, of class ScanHistory.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");
		Object object = null;
		ScanHistory instance = new ScanHistory();
		boolean expResult = false;
		boolean result = instance.equals(object);
		assertEquals(expResult, result);
	}

	/**
	 * Test of toString method, of class ScanHistory.
	 */
	@Test
	public void testToString() {
		System.out.println("toString");
		ScanHistory instance = new ScanHistory();
		String expResult = "com.ecommerce.dasient.model.ScanHistory[id=123]";

		instance.setId(123l);
		String result = instance.toString();
		assertEquals(expResult, result);
	}
}
package com.ecommerce.dasient.vo.result;

import java.util.ArrayList;
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
public class ResultTest {

    public ResultTest() {
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
	 * Test of setRequestId method, of class Result.
	 */
	@Test
	public void testSetRequestId() {
		System.out.println("setRequestId");
		String expId = "";
		Result instance = new Result();
		instance.setRequestId(expId);

		String id = instance.getRequestId();

		assertEquals(expId, id);
	}

	/**
	 * Test of getRequestId method, of class Result.
	 */
	@Test
	public void testGetRequestId() {
		System.out.println("getRequestId");
		String expId = "";
		Result instance = new Result();
		instance.setRequestId(expId);

		String id = instance.getRequestId();

		assertEquals(expId, id);
	}

	/**
	 * Test of setCompletedAt method, of class Result.
	 */
	@Test
	public void testSetCompletedAt() {
		System.out.println("setCompletedAt");
		String expCompletedAt = "123";
		Result instance = new Result();
		instance.setCompletedAt(expCompletedAt);

		String completedAt = instance.getCompletedAt();

		assertEquals(expCompletedAt, completedAt);
	}

	/**
	 * Test of getCompletedAt method, of class Result.
	 */
	@Test
	public void testGetCompletedAt() {
		System.out.println("getCompletedAt");
		String expCompletedAt = "123";
		Result instance = new Result();
		instance.setCompletedAt(expCompletedAt);

		String completedAt = instance.getCompletedAt();

		assertEquals(expCompletedAt, completedAt);
	}

	/**
	 * Test of setStatus method, of class Result.
	 */
	@Test
	public void testSetStatus() {
		System.out.println("setStatus");
		String status = "";
		Result instance = new Result();
		instance.setStatus(status);
	}

	/**
	 * Test of getStatus method, of class Result.
	 */
	@Test
	public void testGetStatus() {
		System.out.println("getStatus");
		Result instance = new Result();
		String expResult = "";
		String result = instance.getStatus();
		assertNull(result);
	}

	/**
	 * Test of setHostname method, of class Result.
	 */
	@Test
	public void testSetHostname() {
		System.out.println("setHostname");
		String hostname = "";
		Result instance = new Result();
		instance.setHostname(hostname);
	}

	/**
	 * Test of getHostname method, of class Result.
	 */
	@Test
	public void testGetHostname() {
		System.out.println("getHostname");
		Result instance = new Result();
		String expResult = "hostname";
		instance.setHostname(expResult);
		String result = instance.getHostname();
		assertEquals(expResult, result);
	}

	/**
	 * Test of addOnBlacklist method, of class Result.
	 */
	@Test
	public void testAddOnBlacklist() {
		System.out.println("addOnBlacklist");
		String blacklist = "";
		Result instance = new Result();
		instance.addOnBlacklist(blacklist);
	}

	/**
	 * Test of setOnBlacklist method, of class Result.
	 */
	@Test
	public void testSetOnBlacklist() {
		System.out.println("setOnBlacklist");
		ArrayList<String> onBlacklist = null;
		Result instance = new Result();
		instance.setOnBlacklist(onBlacklist);
	}

	/**
	 * Test of getOnBlacklist method, of class Result.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testGetOnBlacklist() {
		System.out.println("getOnBlacklist");
		Result instance = new Result();
		ArrayList expResult = null;
		instance.setOnBlacklist(expResult);
		ArrayList result = instance.getOnBlacklist();
		assertEquals(expResult, result);
	}

	/**
	 * Test of onBlacklistSizeI method, of class Result.
	 */
	@Test
	public void testOnBlacklistSizeI() {
		System.out.println("onBlacklistSizeI");
		Result instance = new Result();
		int expResult = 0;
		int result = instance.onBlacklistSizeI();
		assertEquals(expResult, result);
	}

	/**
	 * Test of addScannedUrl method, of class Result.
	 */
	@Test
	public void testAddScannedUrl() {
		System.out.println("addScannedUrl");
		String url = "";
		Result instance = new Result();
		instance.addScannedUrl(url);
	}

	/**
	 * Test of setScannedUrls method, of class Result.
	 */
	@Test
	public void testSetScannedUrls() {
		System.out.println("setScannedUrls");
		ArrayList<String> urls = null;
		Result instance = new Result();
		instance.setScannedUrls(urls);
	}

	/**
	 * Test of getScannedUrls method, of class Result.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testGetScannedUrls() {
		System.out.println("getScannedUrls");
		Result instance = new Result();
		ArrayList expResult = null;
		instance.setScannedUrls(expResult);
		ArrayList result = instance.getScannedUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of scannedUrlsSize method, of class Result.
	 */
	@Test
	public void testScannedUrlsSize() {
		System.out.println("scannedUrlsSize");
		Result instance = new Result();
		int expResult = 0;
		int result = instance.scannedUrlsSize();
		assertEquals(expResult, result);
	}

	/**
	 * Test of addMaliciousUrls method, of class Result.
	 */
	@Test
	public void testAddMaliciousUrls() {
		System.out.println("addMaliciousUrls");
		MaliciousResultUrl url = null;
		Result instance = new Result();
		instance.addMaliciousUrls(url);
	}

	/**
	 * Test of setMaliciousUrls method, of class Result.
	 */
	@Test
	public void testSetMaliciousUrls() {
		System.out.println("setMaliciousUrls");
		ArrayList<MaliciousResultUrl> urls = null;
		Result instance = new Result();
		instance.setMaliciousUrls(urls);
	}

	/**
	 * Test of getMaliciousUrls method, of class Result.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testGetMaliciousUrls() {
		System.out.println("getMaliciousUrls");
		Result instance = new Result();
		ArrayList expResult = null;
		instance.setMaliciousUrls(expResult);
		ArrayList result = instance.getMaliciousUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of maliciousUrlsSize method, of class Result.
	 */
	@Test
	public void testMaliciousUrlsSize() {
		System.out.println("maliciousUrlsSize");
		Result instance = new Result();
		int expResult = 0;
		int result = instance.maliciousUrlsSize();
		assertEquals(expResult, result);
	}

	/**
	 * Test of addSuspiciousUrls method, of class Result.
	 */
	@Test
	public void testAddSuspiciousUrls() {
		System.out.println("addSuspiciousUrls");
		SuspiciousResultUrl url = null;
		Result instance = new Result();
		instance.addSuspiciousUrls(url);
	}

	/**
	 * Test of setSuspiciousUrls method, of class Result.
	 */
	@Test
	public void testSetSuspiciousUrls() {
		System.out.println("setSuspiciousUrls");
		ArrayList<SuspiciousResultUrl> urls = null;
		Result instance = new Result();
		instance.setSuspiciousUrls(urls);
	}

	/**
	 * Test of getSuspiciousUrls method, of class Result.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testGetSuspiciousUrls() {
		System.out.println("getSuspiciousUrls");
		Result instance = new Result();
		ArrayList expResult = null;
		instance.setSuspiciousUrls(expResult);
		ArrayList result = instance.getSuspiciousUrls();
		assertEquals(expResult, result);
	}

	/**
	 * Test of suspiciousUrlsSize method, of class Result.
	 */
	@Test
	public void testSuspiciousUrlsSize() {
		System.out.println("suspiciousUrlsSize");
		Result instance = new Result();
		int expResult = 0;
		int result = instance.suspiciousUrlsSize();
		assertEquals(expResult, result);
	}
}
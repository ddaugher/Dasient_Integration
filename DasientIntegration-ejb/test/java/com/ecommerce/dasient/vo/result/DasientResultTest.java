package com.ecommerce.dasient.vo.result;

import java.util.ArrayList;
import net.sf.json.JSONObject;
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
public class DasientResultTest {

	public DasientResultTest() {
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

	@Test
	public void testSetResult() {

		ArrayList<String> blackList = new ArrayList<String>();
		blackList.add("google.com");

		System.out.println("getResult");
		DasientJSONResult instance = new DasientJSONResult();
		Result res = new Result();
		res.setRequestId("16");
		res.setCompletedAt("now");
		res.setStatus("COMPLETE");
		res.setHostname("http://www.hostname.com");
		res.setOnBlacklist(blackList);
		instance.setResult(res);

		assertNotNull(instance.getResult());
	}

	/**
	 * Test of getResult method, of class DasientJSONResult.
	 */
	@Test
	public void testGetResult() {

		ArrayList<String> blackList = new ArrayList<String>();
		blackList.add("google.com");

		System.out.println("getResult");
		DasientJSONResult instance = new DasientJSONResult();
		Result res = new Result();
		res.setRequestId("16");
		res.setCompletedAt("now");
		res.setStatus("COMPLETE");
		res.setHostname("http://www.hostname.com");
		res.setOnBlacklist(blackList);
		instance.setResult(res);

		assertNotNull(instance.getResult());
	}

	/**
	 * Test of setResult method, of class DasientJSONResult.
	 */
	@Test
	public void testDasientResultToJson() {

		ArrayList<String> blackList = new ArrayList<String>();
		blackList.add("google.com");

		System.out.println("setResult");
		DasientJSONResult instance = new DasientJSONResult();
		Result res = new Result();
		res.setRequestId("16");
		res.setCompletedAt("now");
		res.setStatus("COMPLETE");
		res.setHostname("http://www.hostname.com");
		res.setOnBlacklist(blackList);

		ArrayList<MaliciousResultUrl> urls = new ArrayList<MaliciousResultUrl>(0);
		MaliciousResultUrl url = new MaliciousResultUrl();
		url.setUrl("http://www.maliciousurl.com");
		Snippet snip1 = new Snippet();
		snip1.setSnippet("snip1");
		url.addCodeSnippet(snip1);
		MaliciousResultUrl url2 = new MaliciousResultUrl();
		url2.setUrl("http://www.maliciousurl.com");
		Snippet snip2 = new Snippet();
		snip2.setSnippet("snip2");
		url2.addCodeSnippet(snip2);

		urls.add(url);
		urls.add(url2);

		res.setMaliciousUrls(urls);

		ArrayList<String> scannedUrls = new ArrayList<String>();
		scannedUrls.add("scan1");
		scannedUrls.add("scan2");
		res.setScannedUrls(scannedUrls);
		instance.setResult(res);

		JSONObject jsonObject = JSONObject.fromObject(instance);
		System.out.println(jsonObject);

		assertNotNull(jsonObject);
	}

	/**
	 */
	@Test
	public void testJsonToDasientResult() {

		ArrayList<String> blackList = new ArrayList<String>();
		blackList.add("google.com");

		System.out.println("setResult");
		DasientJSONResult instance = new DasientJSONResult();
		Result res = new Result();
		res.setRequestId("16");
		res.setCompletedAt("now");
		res.setStatus("COMPLETE");
		res.setHostname("http://www.hostname.com");
		res.setOnBlacklist(blackList);

		ArrayList<MaliciousResultUrl> urls = new ArrayList<MaliciousResultUrl>(0);
		MaliciousResultUrl url = new MaliciousResultUrl();
		url.setUrl("http://www.maliciousurl.com");
		Snippet snip1 = new Snippet();
		snip1.setSnippet("snip1");
		url.addCodeSnippet(snip1);
		MaliciousResultUrl url2 = new MaliciousResultUrl();
		url2.setUrl("http://www.maliciousurl.com");
		Snippet snip2 = new Snippet();
		snip2.setSnippet("snip2");
		url2.addCodeSnippet(snip2);

		urls.add(url);
		urls.add(url2);

		res.setMaliciousUrls(urls);

		ArrayList<SuspiciousResultUrl> urls2 = new ArrayList<SuspiciousResultUrl>(0);
		SuspiciousResultUrl url3 = new SuspiciousResultUrl();
		url3.setUrl("http://www.suspiciousurl.com");
		Snippet snip3 = new Snippet();
		snip3.setSnippet("snip3");
		url3.addCodeSnippet(snip3);
		SuspiciousResultUrl url4 = new SuspiciousResultUrl();
		url4.setUrl("http://www.maliciousurl.com");
		Snippet snip4 = new Snippet();
		snip4.setSnippet("snip4");
		url4.addCodeSnippet(snip4);

		urls2.add(url3);
		urls2.add(url4);

		res.setSuspiciousUrls(urls2);

		ArrayList<String> scannedUrls = new ArrayList<String>();
		scannedUrls.add("scan1");
		scannedUrls.add("scan2");
		res.setScannedUrls(scannedUrls);
		instance.setResult(res);

		JSONObject jsonObject = JSONObject.fromObject(instance);
		System.out.println(jsonObject);

		DasientJSONResult t = (DasientJSONResult) JSONObject.toBean(jsonObject, DasientJSONResult.class);
		assertNotNull(t);
		assertEquals("COMPLETE", t.getResult().getStatus());
	}
}

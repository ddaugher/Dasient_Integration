package com.ecommerce.dasient.vo.response;

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
public class DasientResponseTest {

	public DasientResponseTest() {
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
	 * Test of setAck method, of class DasientJSONResponse.
	 */
	@Test
	public void testSetAck() {
		System.out.println("setAck");
		DasientJSONResponse instance = new DasientJSONResponse();
		ResponseAck ack = new ResponseAck();
		ack.setRequestId("16");
		ack.setStatusUrl("http://www.statusurl.com");
		instance.setRequestAck(ack);
		// TODO review the generated test code and remove the default call to fail.
		assertNotNull(instance);
		assertNotNull(instance.getRequestAck());
	}

	/**
	 * Test of getAck method, of class DasientJSONResponse.
	 */
	@Test
	public void testGetAck() {
		System.out.println("getAck");
		DasientJSONResponse res = new DasientJSONResponse();
		ResponseAck ack = new ResponseAck();
		ack.setRequestId("16");
		ack.setStatusUrl("http://www.statusurl.com");
		res.setRequestAck(ack);
		assertNotNull(res);
		assertNotNull(ack);
		assertNotNull(res.getRequestAck());
		assertEquals("16", ack.getRequestId());
		assertEquals("16", res.getRequestAck().getRequestId());
		assertEquals("http://www.statusurl.com", ack.getStatusUrl());
		assertEquals("http://www.statusurl.com", res.getRequestAck().getStatusUrl());
	}

	/**
	 * Test of setRequestIgnored method, of class DasientJSONResponse.
	 */
	@Test
	public void testSetIgnored() {
		System.out.println("setRequestIgnored");
		DasientJSONResponse instance = new DasientJSONResponse();
		ResponseIgnored ignored = new ResponseIgnored();
		ignored.setActualIp("123.123.123.123");
		ignored.setRequestedIp("123.123.123.123");
		instance.setRequestIgnored(ignored);

		assertNotNull(instance);
		assertNotNull(ignored);
		assertNotNull(instance.getRequestIgnored());
		assertEquals("123.123.123.123", instance.getRequestIgnored().getActualIp());
		assertEquals("123.123.123.123", ignored.getActualIp());
		assertEquals("123.123.123.123", instance.getRequestIgnored().getRequestedIp());
		assertEquals("123.123.123.123", ignored.getRequestedIp());
	}

	/**
	 * Test of getRequestIgnored method, of class DasientJSONResponse.
	 */
	@Test
	public void testGetIgnored() {
		System.out.println("getRequestIgnored");
		DasientJSONResponse instance = new DasientJSONResponse();
		ResponseIgnored ignored = new ResponseIgnored();
		ignored.setActualIp("123.123.123.123");
		ignored.setRequestedIp("123.123.123.123");
		instance.setRequestIgnored(ignored);

		assertNotNull(instance);
		assertNotNull(ignored);
		assertNotNull(instance.getRequestIgnored());
		assertEquals("123.123.123.123", instance.getRequestIgnored().getActualIp());
		assertEquals("123.123.123.123", ignored.getActualIp());
		assertEquals("123.123.123.123", instance.getRequestIgnored().getRequestedIp());
		assertEquals("123.123.123.123", ignored.getRequestedIp());
	}

	@Test
	public void testResponseAckToJson() {
		System.out.println("testResponseAckToJson");

		DasientJSONResponse res = new DasientJSONResponse();
		ResponseAck ack = new ResponseAck();
		ack.setRequestId("16");
		ack.setStatusUrl("http://www.statusurl.com");
		res.setRequestAck(ack);

		JSONObject jsonObject = JSONObject.fromObject(res);
		System.out.println(jsonObject);

		assertNotNull(jsonObject);
	}

	@Test
	public void testResponseIgnoredToJson() {
		System.out.println("testResponseIgnoredToJson");

		DasientJSONResponse res = new DasientJSONResponse();
		ResponseIgnored ig = new ResponseIgnored();
		ig.setActualIp("123.123.123.123");
		ig.setRequestedIp("234.234.234.234");
		res.setRequestIgnored(ig);

		JSONObject jsonObject = JSONObject.fromObject(res);
		System.out.println(jsonObject);

		assertNotNull(jsonObject);
	}

	@Test
	public void testJsonToDasientResponse() {
		System.out.println("testJsonToDasientResponse");

		DasientJSONResponse res = new DasientJSONResponse();
		ResponseIgnored ig = new ResponseIgnored();
		ig.setActualIp("123.123.123.123");
		ig.setRequestedIp("234.234.234.234");
		res.setRequestIgnored(ig);

		JSONObject jsonObject = JSONObject.fromObject(res);
		System.out.println(jsonObject);

		DasientJSONResponse t = (DasientJSONResponse) JSONObject.toBean(jsonObject, DasientJSONResponse.class);
		assertNotNull(t);
		assertEquals("123.123.123.123", t.getRequestIgnored().getActualIp());
	}
}

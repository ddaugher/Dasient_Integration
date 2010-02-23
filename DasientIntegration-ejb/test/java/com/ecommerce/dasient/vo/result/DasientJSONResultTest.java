/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ecommerce.dasient.vo.result;

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
public class DasientJSONResultTest {

    public DasientJSONResultTest() {
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
	 * Test of setResult method, of class DasientJSONResult.
	 */
	@Test
	public void testSetResult() {
		System.out.println("setResult");
		Result expResult = null;
		DasientJSONResult instance = new DasientJSONResult();
		instance.setResult(expResult);

		Result res = instance.getResult();

		assertEquals(expResult, res);
	}

	/**
	 * Test of getResult method, of class DasientJSONResult.
	 */
	@Test
	public void testGetResult() {
		System.out.println("getResult");
		Result expResult = null;
		DasientJSONResult instance = new DasientJSONResult();
		instance.setResult(expResult);

		Result res = instance.getResult();

		assertEquals(expResult, res);
	}

}
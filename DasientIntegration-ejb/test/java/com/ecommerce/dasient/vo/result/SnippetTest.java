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
public class SnippetTest {

    public SnippetTest() {
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
	 * Test of setSnippet method, of class Snippet.
	 */
	@Test
	public void testSetSnippet() {
		System.out.println("setSnippet");
		String snippet = "snip1";
		Snippet instance = new Snippet();
		instance.setSnippet(snippet);

		assertEquals("snip1", instance.getSnippet());
	}

	/**
	 * Test of getSnippet method, of class Snippet.
	 */
	@Test
	public void testGetSnippet() {
		System.out.println("getSnippet");
		Snippet instance = new Snippet();
		instance.setSnippet("snip1");
		String expResult = "snip1";
		String result = instance.getSnippet();
		assertEquals(expResult, result);
	}

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class MaliciousResultUrlTest {

    public MaliciousResultUrlTest() {
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
	 * Test of setUrl method, of class MaliciousResultUrl.
	 */
	@Test
	public void testSetUrl() {
		System.out.println("setUrl");
		String expUrl = "www.testurl.com";
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.setUrl(expUrl);
		String url = instance.getUrl();

		assertEquals(expUrl, url);
	}

	/**
	 * Test of getUrl method, of class MaliciousResultUrl.
	 */
	@Test
	public void testGetUrl() {
		System.out.println("getUrl");
		String expUrl = "www.testurl.com";
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.setUrl(expUrl);
		String url = instance.getUrl();

		assertEquals(expUrl, url);
	}

	/**
	 * Test of addCodeSnippet method, of class MaliciousResultUrl.
	 */
	@Test
	public void testAddCodeSnippet() {
		System.out.println("addCodeSnippet");
		Snippet expSnippet = null;
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.addCodeSnippet(expSnippet);
		int snippetCount = instance.getMaliciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of setMaliciousSourceCodeSnippets method, of class MaliciousResultUrl.
	 */
	@Test
	public void testSetMaliciousSourceCodeSnippets() {
		System.out.println("setMaliciousSourceCodeSnippets");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.setMaliciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getMaliciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of getMaliciousSourceCodeSnippets method, of class MaliciousResultUrl.
	 */
	@Test
	public void testGetMaliciousSourceCodeSnippets() {
		System.out.println("getMaliciousSourceCodeSnippets");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.setMaliciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getMaliciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of MaliciousSourceCodeSnippetsSize method, of class MaliciousResultUrl.
	 */
	@Test
	public void testMaliciousSourceCodeSnippetsSize() {
		System.out.println("MaliciousSourceCodeSnippetsSize");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		MaliciousResultUrl instance = new MaliciousResultUrl();
		instance.setMaliciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getMaliciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

}
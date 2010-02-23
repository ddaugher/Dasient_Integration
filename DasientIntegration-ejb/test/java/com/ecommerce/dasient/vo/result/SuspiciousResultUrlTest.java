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
public class SuspiciousResultUrlTest {

    public SuspiciousResultUrlTest() {
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
	 * Test of setUrl method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testSetUrl() {
		System.out.println("setUrl");
		String expUrl = "www.testurl.com";
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.setUrl(expUrl);
		String url = instance.getUrl();

		assertEquals(expUrl, url);
	}

	/**
	 * Test of getUrl method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testGetUrl() {
		System.out.println("getUrl");
		String expUrl = "www.testurl.com";
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.setUrl(expUrl);
		String url = instance.getUrl();

		assertEquals(expUrl, url);
	}

	/**
	 * Test of addCodeSnippet method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testAddCodeSnippet() {
		System.out.println("addCodeSnippet");
		Snippet expSnippet = null;
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.addCodeSnippet(expSnippet);
		int snippetCount = instance.getSuspiciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of setSuspiciousSourceCodeSnippets method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testSetSuspiciousSourceCodeSnippets() {
		System.out.println("setSuspiciousSourceCodeSnippets");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.setSuspiciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getSuspiciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of getSuspiciousSourceCodeSnippets method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testGetSuspiciousSourceCodeSnippets() {
		System.out.println("getSuspiciousSourceCodeSnippets");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.setSuspiciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getSuspiciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

	/**
	 * Test of SuspiciousSourceCodeSnippetsSize method, of class SuspiciousResultUrl.
	 */
	@Test
	public void testSuspiciousSourceCodeSnippetsSize() {
		System.out.println("SuspiciousSourceCodeSnippetsSize");
		ArrayList<Snippet> codeSnippets = new ArrayList<Snippet>(1);
		Snippet snippet = new Snippet();
		snippet.setSnippet("test");
		codeSnippets.add(snippet);
		SuspiciousResultUrl instance = new SuspiciousResultUrl();
		instance.setSuspiciousSourceCodeSnippets(codeSnippets);

		int snippetCount = instance.getSuspiciousSourceCodeSnippets().size();

		assertTrue(snippetCount>0);
	}

}
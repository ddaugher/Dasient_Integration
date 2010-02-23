package com.ecommerce.utils;

import java.util.HashMap;
import java.util.Map;
import javax.jms.MapMessage;
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
public class EntityMapperTest {

    public EntityMapperTest() {
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
	 * Test of messageToMap method, of class EntityMapper.
	 */
	@Test
	public void testMessageToMap() throws Exception {
		System.out.println("messageToMap");
		Map expResult = null;
		MapMessage message = null;
		Map result = EntityMapper.messageToMap(message);
		assertEquals(expResult, result);
	}

	/**
	 * Test of messageToBean method, of class EntityMapper.
	 */
	@Test
	public void testMessageToBean() throws Exception {
		System.out.println("messageToBean");
		MapMessage msg = null;
		Object bean = null;
		Object expResult = null;
		Object result = EntityMapper.messageToBean(msg, bean);
		assertEquals(expResult, result);
	}

	/**
	 * Test of mapToBean method, of class EntityMapper.
	 */
	@Test
	public void testMapToBean() {
		System.out.println("mapToBean");
		Map<String, Object> map = null;
		Object bean = null;
		Object expResult = null;
		Object result = EntityMapper.mapToBean(map, bean);
		assertEquals(expResult, result);
	}

	/**
	 * Test of beanToMap method, of class EntityMapper.
	 */
	@Test
	public void testBeanToMap() {
		System.out.println("beanToMap");
		Object bean = null;
		Map expResult = null;
		Map result = EntityMapper.beanToMap(bean);
		assertEquals(expResult, result);
	}

	/**
	 * Test of beanToMessage method, of class EntityMapper.
	 */
	@Test
	public void testBeanToMessage() throws Exception {
		System.out.println("beanToMessage");
		Object bean = null;
		MapMessage message = null;
		MapMessage expResult = null;
		MapMessage result = EntityMapper.beanToMessage(bean, message);
		assertEquals(expResult, result);
	}

	/**
	 * Test of mapToMessage method, of class EntityMapper.
	 */
	@Test
	public void testMapToMessage() throws Exception {
		System.out.println("mapToMessage");
		Map<String, Object> map = null;
		MapMessage message = null;
		MapMessage expResult = null;
		MapMessage result = EntityMapper.mapToMessage(map, message);
		assertEquals(expResult, result);
	}

	/**
	 * Test of mapEntity method, of class EntityMapper.
	 */
	@Test
	public void testMapEntity() throws Exception {
		System.out.println("mapEntity");
		boolean failOnException = false;
		Map<String, Object> map = null;
		Class entity = null;
		Object expResult = null;
		Object result = EntityMapper.mapEntity(failOnException, map, entity);
		assertEquals(expResult, result);
	}

	/**
	 * Test of mapEntityToHashMap method, of class EntityMapper.
	 */
	@Test
	public void testMapEntityToHashMap() throws Exception {
		System.out.println("mapEntityToHashMap");
		boolean failOnException = false;
		Object entity = null;
		HashMap expResult = null;
		HashMap result = EntityMapper.mapEntityToHashMap(failOnException, entity);
		assertEquals(expResult, result);
	}

	/**
	 * Test of extractMapFromMessage method, of class EntityMapper.
	 */
	@Test
	public void testExtractMapFromMessage() throws Exception {
		System.out.println("extractMapFromMessage");
		MapMessage message = null;
		Map expResult = null;
		Map result = EntityMapper.extractMapFromMessage(message);
		assertEquals(expResult, result);
	}
}
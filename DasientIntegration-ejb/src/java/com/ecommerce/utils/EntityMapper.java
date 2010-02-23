package com.ecommerce.utils;

import com.ecommerce.utils.annotations.EntityMapping;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;

/**
 * @author djdaugherty
 */
public abstract class EntityMapper {

	/**
	 * Extracts the contents of a JMS MapMessage as a Map.
     * 
	 * @param message the message to convert
	 * @return the resulting Map
	 * @throws JMSException if thrown by JMS methods
	 */
	public static Map<String, Object> messageToMap(MapMessage message) throws JMSException {

		// check if incoming MesageMap is null
		if (message == null)
			return null;

		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration en = message.getMapNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			map.put(key, message.getObject(key));
		}
		return map;
	}

	/**
	 * Copies the entries in a JMS MapMessage into the properties of a Bean.
     *
     * Typical usage:
     *   MessageBean bean = EntityMapper.messageToBean(msg, new MessageBean());
     *
	 * @param message the message to read the properties from
	 * @param bean the bean to populate, can be any class
	 * @return the same bean that was passed in
	 * @throws JMSException if thrown by JMS methods
	 */
    public static <T> T messageToBean(MapMessage msg, T bean) throws JMSException {

		if (msg == null)
			return null;

        return mapToBean(messageToMap(msg), bean);
    }

	/**
	 * Copies the entries in a Map into the properties of a Bean.
     *
     * Typical usage:
     *   MessageBean bean = EntityMapper.mapToBean(map, new MessageBean());
     *
	 * @param map the map to read the properties from
	 * @param bean the bean to populate, can be any class
	 * @return the same bean that was passed in
	 */
    @SuppressWarnings("unchecked")
    public static <T> T mapToBean(Map<String, Object> map, T bean) {

		if (map == null || map.isEmpty())
			return null;

        Map beanMap = new BeanMap(bean);
        beanMap.putAll(map);
        return bean;
    }

	/**
	 * Extracts the properties of a Bean as a Map.
     *
     * Typical usage:
     *   Map<String, Object> map = EntityMapper.beanToMap(bean);
     *
	 * @param bean the bean to read the properties from
	 * @return a map containing the properties of the bean with their values
	 */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {

		if (bean == null)
			return null;

        Map<String, Object> map = new HashMap<String, Object>();

        BeanMap beanMap = new BeanMap(bean);
        Iterator readableProps = beanMap.keyIterator();
        while (readableProps.hasNext()) {
            String propName = (String) readableProps.next();

            // Only read properties that have a setter too (eg. skips getClass())
            if (beanMap.getWriteMethod(propName) != null) {
                map.put(propName, beanMap.get(propName));
            }
        }

        return map;
    }

	/**
	 * Copies the properties of a Bean into a JMS MapMessage.
     *
     * Typical usage:
     *   MapMessage msg = EntityMapper.beanToMessage(bean, session.createMapMessage());
     * 
	 * @param bean the bean to read the properties from
	 * @param message the JMS MapMessage to populate
	 * @return the same message that was passed in
	 * @throws JMSException if thrown by JMS methods
	 */
    public static MapMessage beanToMessage(Object bean, MapMessage message) throws JMSException {

		if (message == null)
			return null;

		if (bean == null)
			return null;

        return mapToMessage(beanToMap(bean), message);
    }

	/**
	 * Copies the properties of a Map into a JMS MapMessage.
     *
     * Typical usage:
     *   MapMessage msg = EntityMapper.beanToMessage(bean, session.createMapMessage());
     *
	 * @param map the map to read the properties from
	 * @param message the JMS MapMessage to populate
	 * @return the same message that was passed in
	 * @throws JMSException if thrown by JMS methods
	 */
    public static MapMessage mapToMessage(Map<String, Object> map, MapMessage message) throws JMSException {

		if (map==null)
			return null;

		if (message==null)
			return null;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                message.setObject(entry.getKey(), entry.getValue());
            }
        }
        return message;
    }

	private static Logger logger = Logger.getLogger(EntityMapper.class);

	/**
	 * @param failOnException boolean indicating whether the method should return an exception if the method is unable
	 *                        to set any incoming value
	 * @param map
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public static Object mapEntity(boolean failOnException, Map<String, Object> map, Class entity) throws Exception {

		if (map == null)
			return null;

		if (entity == null)
			return null;

		logger.debug("Processing the following entity - " + entity.toString());
        Object obj = entity.newInstance();

		// retrieve all of the methods declared within the entity and superclasses
		Method[] methods = entity.getMethods();
		for (Method m : methods) {

			// only process the setter methods... ignore everything else
			if (!isValidSetter(m.getName())) {
				continue;
			}

			EntityMapping messMap = m.getAnnotation(EntityMapping.class);
			if (messMap != null) {
				try {
					logger.debug("Annotation found! - Processing method - " + m.getName());
					logger.debug("Annotation.hashmap key value = " + messMap.keyValue());
					logger.debug("Annotation.value = " + map.get(messMap.keyValue()));

					Object arglist[] = new Object[1];
					arglist[0] = map.get(messMap.keyValue());
					logger.debug("invoking " + m.getName() + " on " + entity.getName() + " , setting value = " + map.get(messMap.keyValue()).toString());
					m.invoke(obj, arglist);
				} catch (Exception e) {
					if (failOnException) {
						logger.error(MessageFormat.format("EntityMapper.mapEntity throwing Exception - {0} in error", m.getName()));
						throw e;
					} else {
						//logger.error(MessageFormat.format("EntityMapper.mapEntity throwing Exception - {0} in error", m.getName()));
						//logger.error(MessageFormat.format("failOnException = FALSE, continuing...{0}", m.getName()));
					}
				}
			}
		}

		return obj;
	}

    public static HashMap<String, Object> mapEntityToHashMap(boolean failOnException, Object entity) throws Exception {

		if (entity == null)
			return null;

		HashMap<String, Object> map = new HashMap<String, Object>();

		// retrieve all of the methods declared within the entity and superclasses
		Method[] methods = entity.getClass().getMethods();
		for (Method m : methods) {

			// only process the getter methods... ignore everything else
			if (!isValidGetter(m.getName())) {
				continue;
			}

			EntityMapping messMap = m.getAnnotation(EntityMapping.class);
			if (messMap != null) {
				try {
					logger.debug("Annotation found! - Processing method - " + m.getName());

					Object arglist[] = new Object[0];
					//arglist[0] = map.get(messMap.keyValue());
					//logger.info("invoking " + m.getName() + " on " + entity.getName() + " , setting value = " + map.get(messMap.keyValue()).toString());
					//map.put(messMap.keyValue(), m.invoke(entity, arglist));
					Object val = m.invoke(entity, arglist);
					map.put(messMap.keyValue(), val);

					logger.debug("setting " + messMap.keyValue() + " = " + val.toString());
					//logger.debug("adding value " + m.invoke(entity) + " to method " + messMap.keyValue());
				} catch (Exception e) {
					if (failOnException) {
						logger.error(MessageFormat.format("EntityMapper.mapEntity throwing Exception - {0} in error", m.getName()));
						throw e;
					} else {
						//logger.error(MessageFormat.format("EntityMapper.mapEntity throwing Exception - {0} in error", m.getName()));
						//logger.error(MessageFormat.format("failOnException = FALSE, continuing...{0}", m.getName()));
					}
				}
			}
		}
		return map;
	}

	/**
	 * Extract a Map from the given {@link MapMessage}.
	 * @param message the message to convert
	 * @return the resulting Map
	 * @throws JMSException if thrown by JMS methods
	 */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> extractMapFromMessage(MapMessage message) throws JMSException {

		if (message == null)
			return null;

		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> en = message.getMapNames();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			map.put(key, message.getObject(key));
		}
		return map;
	}

	private static boolean isValidGetter(String method) {
		boolean ret = false;

		if (method.startsWith("get") || (method.startsWith("is"))) {
			ret = ("getClass".indexOf(method) == -1);
		}

		return ret;
	}

	private static boolean isValidSetter(String method) {
		boolean ret = false;

		if (method.startsWith("set")) {
			ret = ("setClass".indexOf(method) == -1);
		}

		return ret;
	}
}

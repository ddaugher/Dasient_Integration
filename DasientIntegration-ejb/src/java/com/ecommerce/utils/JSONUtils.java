package com.ecommerce.utils;

import com.ecommerce.utils.annotations.JsonAttribute;
import com.ecommerce.utils.annotations.JsonFieldRecursive;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import java.text.MessageFormat;
import net.sf.json.JSONObject;

/**
 * Utility class providing JSON utilties for the Dasient Integration project
 * @author djdaugherty
 */
public class JSONUtils {

	static Logger logger = Logger.getLogger(JSONUtils.class);

	private static enum Direction {

		JAVATOJSON, JSONTOJAVA
	}

	private JSONUtils() {
	}

	/**
	 * maps a java bean (object) to a fully qualified json string.  The java object
	 * must follow the specification for a java bean and if json specific attribute
	 * names do not match the java specification for a setter/getter... the JsonAttribute
	 * annotation must be completed in order to assist the method.
	 * @param entity java object to be mapped to JSON string
	 * @return jsonstring well-formed json string... representing passed in java object
	 * @throws Exception
	 */
	public static String mapObjectToJsonString(Object entity) throws Exception {

		JSONObject jsonObject = JSONObject.fromObject(entity);
		String subString = jsonObject.toString();

		Class cls = entity.getClass();
		logger.debug(MessageFormat.format("calling mapObjectToJsonString on class - {0}", cls.getName()));

		// first replace all of the methods at the object level
		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {

			JsonAttribute jAttr = m.getAnnotation(JsonAttribute.class);
			if (jAttr != null && isValidGetter(m.getName())) {
				logger.debug(MessageFormat.format("method - {0}", m.getName()));
				logger.debug("replacing " + jAttr.javaString() + " with " + jAttr.jsonString());

				subString = subString.replaceAll(jAttr.javaString(), jAttr.jsonString());
			}
		}

		// gather each field within the object and determine if the fields should be
		// recursively queried for additional info via the JsonFieldRecursive annotation
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {

			logger.debug(MessageFormat.format("field - {0}", f.getName()));

			// if the annotation requests recursion... keep drilling for more fields and methods.
			JsonFieldRecursive jsonAttr = f.getAnnotation(JsonFieldRecursive.class);
			if (jsonAttr != null && jsonAttr.recursive() == JsonFieldRecursive.Recursive.YES) {

				// process the field
				subString = processJavaField(f, subString);

			}
		}

		return subString;
	}

	/**
	 * maps json attributes within the json string to the appropriate java attributes, based on the
	 * values stored within the jsonattribute annation within the java object.  This method is required
	 * prior to deserializing a json string into a java object.  The requirements specified by the java
	 * specification for a setter/getter/attribute are different than the json specification.  The
	 * JsonAttribute annation is used to handle this mapping back and forth.:
	 * @param cls java class to be mapped to
	 * @param jsonString incoming jsonstring to be mapped to the passed class
	 * @return converted string
	 * @throws Exception
	 */
	public static String mapJsonAttrsToJavaAttrs(Class cls, String jsonString) throws Exception {

		String subString = jsonString;

		//Class cls = entity.getClass();
		logger.debug(MessageFormat.format("calling mapJsonToJava on class - {0}", cls.getName()));

		// first replace all of the methods at the object level
		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {

			JsonAttribute jAttr = m.getAnnotation(JsonAttribute.class);
			if (jAttr != null && isValidGetter(m.getName())) {
				logger.debug(MessageFormat.format("method - {0}", m.getName()));
				logger.debug("replacing " + jAttr.jsonString() + " with " + jAttr.javaString());

				subString = subString.replaceAll(jAttr.jsonString(), jAttr.javaString());
			}
		}

		// gather each field within the object and determine if the fields should be
		// recursively queried for additional info via the JsonFieldRecursive annotation
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {

			logger.debug(MessageFormat.format("field - {0}", f.getName()));

			// if the annotation requests recursion... keep drilling for more fields and methods.
			JsonFieldRecursive jsonAttr = f.getAnnotation(JsonFieldRecursive.class);
			if (jsonAttr != null && jsonAttr.recursive() == JsonFieldRecursive.Recursive.YES) {

				// process the field
				subString = processJsonField(f, subString);

			}
		}

		return subString;
	}

	private static String processJavaField(Field f, String jsonString) {
		return processField(f, jsonString, Direction.JAVATOJSON);
	}

	private static String processJsonField(Field f, String jsonString) {
		return processField(f, jsonString, Direction.JSONTOJAVA);
	}

	private static String processField(Field f, String jsonString, Direction direction) {

		Class cls = f.getType();
		logger.debug(MessageFormat.format("field - {0}", f.getName()));
		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {

			JsonAttribute jAttr = m.getAnnotation(JsonAttribute.class);
			if (jAttr != null && isValidGetter(m.getName())) {
				logger.debug(MessageFormat.format("method - {0}", m.getName()));

				if (direction == Direction.JAVATOJSON) {
					logger.debug("replacing " + jAttr.javaString() + " with " + jAttr.jsonString());
					jsonString = jsonString.replaceAll(jAttr.javaString(), jAttr.jsonString());
				} else if (direction == Direction.JSONTOJAVA) {
					logger.debug("replacing " + jAttr.jsonString() + " with " + jAttr.javaString());
					jsonString = jsonString.replaceAll(jAttr.jsonString(), jAttr.javaString());

				} else {
					// need to do something here
				}
			}
		}

		Field[] fields = cls.getDeclaredFields();
		for (Field f2 : fields) {
			JsonFieldRecursive jsonAttr = f2.getAnnotation(JsonFieldRecursive.class);
			if (jsonAttr != null && jsonAttr.recursive() == JsonFieldRecursive.Recursive.YES) {

				logger.debug(MessageFormat.format("field - {0}", f2.getName()));
				if (direction == Direction.JAVATOJSON) {
					jsonString = processField(f2, jsonString, Direction.JAVATOJSON);
				} else if (direction == Direction.JSONTOJAVA) {
					jsonString = processField(f2, jsonString, Direction.JSONTOJAVA);
				} else {
					// need to do something here
				}

			}
		}

		return jsonString;
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
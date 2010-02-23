package com.ecommerce.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to document the mapping values between a java object and a json attribute structure
 *
 * @author djdaugherty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonAttribute {

	/**
	 * Java name for method
	 * @return
	 */
	String javaString();
	/**
	 * JSON name for method
	 * @return
	 */
	String jsonString();
}

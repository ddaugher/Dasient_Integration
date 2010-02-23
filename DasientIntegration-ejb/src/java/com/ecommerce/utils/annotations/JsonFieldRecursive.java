package com.ecommerce.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonFieldRecursive {
	public enum Recursive { YES, NO }
	Recursive recursive() default Recursive.NO;
}

package com.justinmobile.core.domain;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface DateFormat {
	
	String format() default "yyyy-MM-dd HH:mm:ss";

}

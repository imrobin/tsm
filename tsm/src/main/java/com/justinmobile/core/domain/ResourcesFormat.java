package com.justinmobile.core.domain;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ResourcesFormat {
	
	String resource() default "i18n/messages";
	
	String key();

}

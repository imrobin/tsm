package com.justinmobile.tsm.endpoint.webservice.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethod {
	
	Type value() default Type.NULL;

	public enum Type {
		NULL, APDU,;
	}
}

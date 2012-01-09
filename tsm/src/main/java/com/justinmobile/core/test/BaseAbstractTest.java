package com.justinmobile.core.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.utils.reflection.ReflectionUtils;

/**
 * 子类可以重写注释，来改变配置文件的位置和是否回滚等信息
 * @author peak
 *
 */
@ContextConfiguration(locations={"classpath:applicationContext-test.xml"})
@TransactionConfiguration(defaultRollback=false)
public abstract class BaseAbstractTest extends AbstractTransactionalJUnit4SpringContextTests {	
	
	protected void executeSql(String sql) {
		if (StringUtils.isNotEmpty(sql)) {
			simpleJdbcTemplate.getJdbcOperations().execute(sql);
		}
	}
	
	protected void setSimpleProperties(Object o) {
		Set<Field> fields = ReflectionUtils.getAllDeclareFields(o);
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			String methodName = "set" + StringUtils.capitalize(field.getName());
			try {
				Method method = o.getClass().getMethod(methodName, field.getType());
				Random r = new Random();
				int value = r.nextInt(10);
				String typeName = field.getType().getSimpleName();
				if ("String".equals(typeName)) {
					method.invoke(o, String.valueOf(value));
				} else if ("LONG".equals(typeName.toUpperCase()) && !("id".equals(field.getName()))) {
					method.invoke(o, Long.valueOf(value));
				} else if (("Integer".equals(typeName) || "int".equals(typeName) ) && !("id".equals(field.getName()))) {
					method.invoke(o, Integer.valueOf(value));
				} else if ("Calendar".equals(typeName)) {
					method.invoke(o, Calendar.getInstance());
				}	
			} catch (SecurityException e) {
				logger.warn("SecurityException: "+ methodName +" method");
			} catch (NoSuchMethodException e) {
				logger.warn("NoSuchMethodException: "+ methodName +" method");
			} catch (IllegalArgumentException e) {
				logger.warn("IllegalArgumentException: invoke method "+ methodName +" ");
			} catch (IllegalAccessException e) {
				logger.warn("IllegalAccessException: invoke method "+ methodName +" ");
			} catch (InvocationTargetException e) {
				logger.warn("InvocationTargetException: invoke method "+ methodName +" ");
			}			
		}
	}
	
}

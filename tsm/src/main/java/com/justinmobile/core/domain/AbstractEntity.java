package com.justinmobile.core.domain;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.EnumJsonable;

/**
 * 对象父类，实现Serializable接口 重写equals和hashCode方法：根据id来判断对象的相等 子类必须有id的属性
 * 
 * @author peak
 */
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 5674301313143471084L;

	private final String[] SIMPLE_TYPE = new String[] { "String", "Long", "Integer", "int", "long", "Float", "float", "Double", "double",
			"BigDecimal", "Date", "Calendar", "Boolean", "boolean" };

	public abstract Object getId();

	@Override
	public boolean equals(Object object) {
		if (object == null || (!(object instanceof AbstractEntity) && (this.getClass().getName().equals(object.getClass().getName()))))
			return false;
		AbstractEntity other = (AbstractEntity) object;
		return getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		if (null == getId()) {
			return super.hashCode();
		}
		return getId().hashCode();
	}

	@Override
	public String toString() {
		try {
			// ToStringBuilder.reflectionToString(this,
			// ToStringStyle.SHORT_PREFIX_STYLE);
			return JsonBinder.buildNonNullBinder().toJson(this.toMap(null, null));
		} catch (Exception e) {
			return super.toString();
		}
	}

	/**
	 * 将域对象转成Map，key是fieldName，value是fieldValue<br/>
	 * 不需要的属性在excludeField写明，全需要填NULL，空格隔开，例如：name id status
	 * 需要级联的属性在includeFields写明，不需要填NULL，空格隔开, 例如：customer.user.loginname<br/>
	 * 通过DateFormat来对日期类型格式化<br/>
	 * 通过DecimalFormat来对带小数的数字类型格式化<br/>
	 * 通过ResourcesFormat来对需要转义的类型格式化，例如：0:好,1:不好<br/>
	 * 
	 * @param excludeField
	 * @param includeCascadeFields
	 * @return Map<String, Object>
	 * @see DateFormat, DecimalFormat, ResourcesFormat
	 */
	public Map<String, Object> toMap(String excludeField, String includeCascadeField) {
		return toMap(null, excludeField, includeCascadeField);
	}

	/**
	 * 加入，可以忽略掉格式化的情况
	 * 
	 * @param ignoreFormatField
	 * @param excludeField
	 * @param includeCascadeField
	 * @see toMap(String excludeField, String includeCascadeField)
	 * @return
	 */
	public Map<String, Object> toMap(String ignoreFormatField, String excludeField, String includeCascadeField) {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		String[] excludeFields = StringUtils.split(excludeField);
		for (Field field : fields) {
			// 去掉静态和终态的field
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			try {
				// 得到field的类型简称和field的值
				String simpleTypeName = field.getType().getSimpleName();
				String fieldName = field.getName();
				if (excludeFields != null) {
					if (ArrayUtils.contains(excludeFields, fieldName)) {
						continue;
					}
				}
				boolean accessible = field.isAccessible();
				// 开放field的访问权限
				field.setAccessible(true);
				Object value = field.get(this);
				if (value == null) {// 为NULL的数据不加入到Map中
					continue;
				}
				if (ArrayUtils.contains(SIMPLE_TYPE, simpleTypeName)) {
					Annotation[] annotations = field.getAnnotations();
					if (ArrayUtils.isEmpty(annotations)) {
						map.put(fieldName, value);
					} else {
						String formatValue = null;
						if (StringUtils.isNotBlank(ignoreFormatField)) {
							String[] ignoreFormatFields = StringUtils.split(ignoreFormatField);
							if (ArrayUtils.contains(ignoreFormatFields, fieldName)) {
								map.put(fieldName, value);
							} else {
								formatValue = formatValue(simpleTypeName, value, annotations);
							}
						} else {
							formatValue = formatValue(simpleTypeName, value, annotations);
						}
						if (formatValue != null) {
							map.put(fieldName, formatValue);
							map.put(fieldName + "Original", value); // map中加入原值，有时用得上
						}
					}
				} else if (value instanceof EnumJsonable) {
					map.put(fieldName, ((EnumJsonable) value).toJson());
				}
				// 恢复field访问属性
				field.setAccessible(accessible);
			} catch (Exception e) {
				e.printStackTrace();
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR, e);
			}
		}
		// 传进的参数做为一个属性加到map中
		addIncludeField(map, includeCascadeField);
		return map;
	}

	private String formatValue(String simpleTypeName, Object value, Annotation[] annotations) {
		String formatValue = null;
		for (Annotation annotation : annotations) {
			if (annotation instanceof DateFormat) {// 日期格式化
				DateFormat dateFormat = (DateFormat) annotation;
				if (simpleTypeName.equals("Date")) {
					formatValue = DateFormatUtils.format((Date) value, dateFormat.format());
				} else if (simpleTypeName.equals("Calendar")) {
					formatValue = DateFormatUtils.format((Calendar) value, dateFormat.format());
				}
			} else if (annotation instanceof DecimalNumberFormat) {// 小数格式化
				DecimalNumberFormat decimalNumberFormat = (DecimalNumberFormat) annotation;
				DecimalFormat format = new DecimalFormat(decimalNumberFormat.format());
				formatValue = format.format(value);
			} else if (annotation instanceof ResourcesFormat) {// 转义格式化
				ResourcesFormat resourcesFormat = (ResourcesFormat) annotation;
				try {
					ResourceBundle bundle = ResourceBundle.getBundle(resourcesFormat.resource());
					String text = bundle.getString(resourcesFormat.key());
					String afterString = StringUtils.substringAfter(text, String.valueOf(value) + ":");
					if (StringUtils.indexOf(afterString, ",") == -1) {
						formatValue = afterString;
					} else {
						formatValue = StringUtils.substringBefore(afterString, ",");
					}
				} catch (NullPointerException e) {
					// key为null
					formatValue = String.valueOf(value);
				} catch (MissingResourceException e) {
					// 配置到文件或者key
					formatValue = String.valueOf(value);
				}

			}
		}
		return formatValue;
	}

	private void addIncludeField(Map<String, Object> map, String includeCascadeField) {
		String[] includeFields = StringUtils.split(includeCascadeField);
		if (ArrayUtils.isNotEmpty(includeFields)) {
			for (String includeField : includeFields) {
				String firstFieldName = StringUtils.substringBefore(includeField, ".");
				if (StringUtils.isNotBlank(firstFieldName)) {
					try {
						Field field = this.getClass().getDeclaredField(firstFieldName);
						String[] includeFieldNames = StringUtils.split(includeField, ".");
						Object value = getincludeFieldValue(includeFieldNames, 1, field, this);
						if (value != null) {
							map.put(StringUtils.replace(includeField, ".", "_"), value);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new PlatformException(PlatformErrorCode.PARAM_ERROR, e);
					}
				}
			}
		}
	}

	private Object getincludeFieldValue(String[] includeFieldNames, int index, Field field, Object obj) throws Exception {
		Object result = null;
		// 取到子类的field
		Field newField = field.getType().getDeclaredField(includeFieldNames[index]);
		// 开发读写权限
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		boolean a = newField.isAccessible();
		newField.setAccessible(true);
		// 得到子类对象
		Object cascadeObject = field.get(obj);
		if (cascadeObject == null) {
			Method m = obj.getClass().getDeclaredMethod("get" + StringUtils.capitalize(field.getName()));
			cascadeObject = m.invoke(obj);
			if (cascadeObject == null) {
				return null;
			}
		}
		// 得到子类field的值
		Object value = newField.get(cascadeObject);
		if (value == null) {

			Method m = cascadeObject.getClass().getDeclaredMethod("get" + StringUtils.capitalize(newField.getName()));
			value = m.invoke(cascadeObject);
			if (value == null) {
				return null;
			}
		}
		if (index == includeFieldNames.length - 1) {
			Annotation[] annotations = newField.getAnnotations();
			if (ArrayUtils.isEmpty(annotations)) {
				result = value;
			} else {
				String formatValue = formatValue(newField.getType().getSimpleName(), value, annotations);
				if (formatValue != null) {
					result = formatValue;
				}
			}

		} else {
			result = getincludeFieldValue(includeFieldNames, ++index, newField, cascadeObject);
		}
		field.setAccessible(accessible);
		newField.setAccessible(a);
		return result;
	}

	public static void main(String[] args) {
	}

}

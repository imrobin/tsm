package com.justinmobile.core.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PlatformException extends RuntimeException {

	private static final long serialVersionUID = 7946023196149777499L;

	protected static final String ERROR_BUNDLE = "i18n/errors";

	protected static final Properties p = getProperties(ERROR_BUNDLE);

	protected PlatformErrorCode errorCode;

	protected String realCode;

	private Object[] arguments;

	private static Properties getProperties(String baseName) {
		try {
			ResourceBundle rb = ResourceBundle.getBundle(baseName, Locale.getDefault());
			return MapUtils.toProperties(MapUtils.toMap(rb));
		} catch (Exception e) {
			// 未取到配置文件就返回null
			return null;
		}

	}

	public PlatformErrorCode getErrorCode() {
		return errorCode;
	}

	public PlatformException(PlatformErrorCode errorCode, Object... arguments) {
		super();
		this.errorCode = errorCode;
		this.realCode = errorCode.getErrorCode();
		this.arguments = arguments;
	}
	
	public PlatformException(PlatformErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
		this.realCode = errorCode.getErrorCode();
	}

	/**
	 * 取得配置文件的内容，或者使用默认值
	 */
	@Override
	public String getMessage() {
		String notMessage = "not error, not message";
		if (errorCode == null || StringUtils.isBlank(realCode)) {
			return notMessage;
		} else {
			String defaultMessage = errorCode.getDefaultMessage();
			if (p == null) {
				if (StringUtils.isBlank(defaultMessage)) {
					return notMessage;
				} else {
					return MessageFormat.format(defaultMessage, this.arguments);
				}
			} else {
				String message = p.getProperty(realCode, defaultMessage);
				if (ArrayUtils.isEmpty(this.arguments)) {
					return message;
				} else {
					return MessageFormat.format(message, this.arguments);
				}
			}
		}
	}

}

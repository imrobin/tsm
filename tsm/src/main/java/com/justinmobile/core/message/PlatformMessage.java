package com.justinmobile.core.message;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

/**
 * 异常代码，加入默认说明，这样减少配置文件的写入了
 * @author peak
 *
 */
public enum PlatformMessage {
	
	DEFAULT("-1", "默认，无具体信息"),
	
	SUCCESS("00000000", "操作成功"),
	WEB_SERVICE_SUCCESS("00000000", "操作成功"),
	PROVIDER_WEB_SERVICE_SUCCESS("0000", "操作成功"),
	CLIENT_SUCCESS("000000", "操作成功"),
	
	TRANS_EXCESSIVING("FFFFFF", "事务处理中"),
	TRANS_EXCEPTION_CLOSED("AAAAAA", "异常事务关闭"),
	
	PLATFORM_NAME("000001", "多应用管理平台"),
	
	PASSWORD_RESET("100000", "重置密码成功"),
	CAPTCHA_ERROR("100001", "验证码输入错误"),
	
	SMS_ACTIVE_MESSAGE("100002", "尊敬的用户：恭喜您注册成功，您的激活码为：{0}"),
	SMS_CHANGE_PWD("100003", "尊敬的用户：密码修改已成功，您的新的密码为：{0}"),
	SMS_VALIDATE_CODE("100004","尊敬的用户：密码修改申请成功，您的验证码为：{0}"),
	
	MOBILE_LOGIN("00000000", "开始登录"),
	MOBILE_REGISTER("00000001", "开始注册"),
	MOBILE_NOTIFY_IMSI("00000002","IMSI通知"),
	MOBILE_CANCEL("00000003","开始注销"),
	MOBILE_MISMATCH_CHALLENGE_NO("90000001", "未收到上行短信"),
	MOBILE_REGISTERED("90000002", "终端已绑定"),
	MOBILE_IN_BLACK_LIST("90000003", "终端在黑名单"), 
	;

	private String code;

	private String defaultMessage;
	
	private ResourceBundle rb = null;

	PlatformMessage(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
		try {
			rb = ResourceBundle.getBundle("i18n/messages");
		} catch (Exception e) {
			//无配置文件就使用默认message
		}
	}

	public String getCode() {
		return this.code;
	}

	public String getDefaultMessage(Object... arguments) {
		return MessageFormat.format(defaultMessage, arguments);
	}
	
	public String getMessage(Object... arguments) {
		try {
			String message = null;
			if (rb != null) {
				message = rb.getString(getCode());
			}
			if (StringUtils.isBlank(message)) {
				message = getDefaultMessage(arguments);
			} else {
				message = MessageFormat.format(message, arguments);
			}
			return message;
		} catch (Exception e) {
			return getDefaultMessage(arguments);
		}
	}

}

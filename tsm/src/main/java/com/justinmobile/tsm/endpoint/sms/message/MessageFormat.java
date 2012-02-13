package com.justinmobile.tsm.endpoint.sms.message;

public enum MessageFormat {
	
	/** 信息格式0：ASCII串 */
	MSG_FORMAT_TYPE_ASCII(0x00, "byte"),
	
	/** 信息格式3：短信写卡操作 */
	MSG_FORMAT_TYPE_CARD_OPT(0x03, "byte"),

	/** 信息格式4：二进制信息 */
	MSG_FORMAT_TYPE_BYTE(0x04, "byte"),

	/** 信息格式:8：UCS2编码 */
	MSG_FORMAT_TYPE_UCS2(0x08, "iso-10646-ucs-2"),

	/** 信息格式15：含GB汉字 */
	MSG_FORMAT_TYPE_GBK(0x0F, "gbk"),

	/** 信息格式246，卡商内部指令 */
	MSG_FORMAT_TYPE_F6(0xF6, "byte"),

	;
	
	private int value;
	
	private String format;
	
	public int getValue() {
		return value;
	}
	
	public String getFormat() {
		return format;
	}

	MessageFormat(int value, String format) {
		this.value = value;
		this.format = format;
	}

	public static MessageFormat valueOf(int value) {
		MessageFormat[] formats = MessageFormat.values();
		for (MessageFormat format : formats) {
			if (format.getValue() == value) {
				return format;
			}
		}
		return null;
	}
}

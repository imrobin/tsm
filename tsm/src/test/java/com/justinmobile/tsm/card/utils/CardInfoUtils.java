package com.justinmobile.tsm.card.utils;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.tsm.card.domain.CardInfo;

public class CardInfoUtils {
	
	/**
	 * 创建对象用于测试
	 * 
	 * @return 除以下字段外，都null<br/>
	 *         ardNo：8位随机数字字符<br/>
	 */
	public static CardInfo createDefult() {
		CardInfo card = new CardInfo();
		card.setCardNo(RandomStringUtils.randomAlphanumeric(8));
		return card;
	}
}

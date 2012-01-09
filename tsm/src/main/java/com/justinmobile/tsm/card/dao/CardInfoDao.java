package com.justinmobile.tsm.card.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.card.domain.CardInfo;

public interface CardInfoDao extends EntityDao<CardInfo, Long> {

	/**
	 * 根据卡号找卡信息
	 * 
	 * @param cardNo
	 *            卡号
	 * @return 卡信息
	 */
	CardInfo getByCardNo(String cardNo);
}
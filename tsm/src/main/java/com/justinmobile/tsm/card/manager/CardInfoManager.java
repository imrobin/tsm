package com.justinmobile.tsm.card.manager;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.card.domain.CardInfo;

public interface CardInfoManager extends EntityManager<CardInfo> {

	/**
	 * 根据卡号找卡信息
	 * 
	 * @param cardNo
	 *            卡号
	 * @return 卡信息
	 */
	public CardInfo getByCardNo(String cardNo);

	public CardInfo getByMobileNo(String mobileNo);

	/**
	 * 创建记录如果当前卡号从未注册
	 * 
	 * @param cardNo
	 *            卡号
	 * @return
	 * @throws CARD_IS_NOT_SUPPOT
	 *             卡不支持tsm
	 * @throws CARD_IS_DISABLE
	 *             卡状态为“不可用”
	 */
	CardInfo buildCardInfoIfNotExist(String cardNo);

	/**
	 * 生成手机钱包登录用的token
	 * @param cardNo
	 * @param imsi
	 * @param challengeNo
	 * @param mobileNo
	 */
	void generateToken(String cardNo, String imsi, String challengeNo, String mobileNo);

	/**
	 * 根据卡号简单检测条件
	 * @param cardNo
	 */
	public void checkCard(String cardNo);

	public void generateToken(CardInfo card);
}
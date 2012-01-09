package com.justinmobile.tsm.card.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;

@Service("cardInfoManager")
public class CardInfoManagerImpl extends EntityManagerImpl<CardInfo, CardInfoDao> implements CardInfoManager {

	@Autowired
	private CardInfoDao cardInfoDao;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Override
	public CardInfo getByCardNo(String cardNo) {
		return cardInfoDao.getByCardNo(cardNo);
	}

	public CardInfo getByMobileNo(String mobileNo) throws PlatformException {
		try {
			return cardInfoDao.findUniqueEntity("from " + CardInfo.class.getName() + " as c where c.customer.mobileNo = ?", mobileNo);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		}
	}

	@Override
	public CardInfo buildCardInfoIfNotExist(String cardNo) {
		try {
			CardInfo cardInfo = cardInfoDao.findUniqueByProperty("cardNo", cardNo);
			if (null == cardInfo) {
				CardBaseInfo cardBaseInfo = cardBaseInfoManager.getCardBaseInfoByCardNo(cardNo);
				if (null != cardBaseInfo) {
					cardInfo = new CardInfo();
					cardInfo.setCardNo(cardNo);
					cardInfo.setCardType(CardInfo.CARD_TYPE_NORMAL);
					cardInfo.setCardBaseInfo(cardBaseInfo);
					cardInfo.setAvailableNonevolatileSpace(cardBaseInfo.getTotalRomSize());
					cardInfo.setAvailableVolatileSpace(cardBaseInfo.getTotalRamSize().intValue());
					cardInfo.setStatus(CardInfo.STATUS_ENABLE);
					cardInfoDao.saveOrUpdate(cardInfo);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
				}
			} else {
				if (cardInfo.getStatus().equals(CardInfo.STATUS_DISABLE)) {
					throw new PlatformException(PlatformErrorCode.CARD_IS_DISABLE);
				}
			}
			return cardInfo;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void generateToken(String cardNo, String imsi, String challengeNo, String mobileNo) {
		try {
			CardInfo card = cardInfoDao.getByCardNo(cardNo);
			if (null == card) {// 为空表示设备未绑定
				card = buildCardInfoIfNotExist(cardNo);
			}

			card.setMobileNo(mobileNo);
			card.setChallengeNo(challengeNo);

			generateToken(card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void generateToken(CardInfo card) {
		StringBuilder sb = new StringBuilder();
		sb.append(card.getCardNo()).append(card.getImsi()).append(card.getChallengeNo());

		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		// false 表示：生成32位的Hex版, 这也是encodeHashAsBase64的, Acegi 默认配置; true
		// 表示：生成24位的Base64版
		md5.setEncodeHashAsBase64(false);
		String token = md5.encodePassword(sb.toString(), "12345");

		card.setToken(token);

		cardInfoDao.saveOrUpdate(card);
	}

	@Override
	public void checkCard(String cardNo) {
		try {
			// 1.检测是否卡片存在
			CardInfo cardInfo = cardInfoDao.getByCardNo(cardNo);
			if (null == cardInfo) {
				throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
			} else {
				// 2.判断卡片状态
				/*
				 * if(cardInfo.getStatus().equals(CardInfo.STATUS_DISABLE)){
				 * throw new
				 * PlatformException(PlatformErrorCode.TRANS_CARD_DISABLE);
				 * }else{
				 */
				// 3.判断是否有customercardInfo
				CustomerCardInfo cci = customerCardInfoManager.getByCardNoCancelAndReplaced(cardNo);
				if (null == cci) {
					throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_BIND);
					/* } */
				}
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
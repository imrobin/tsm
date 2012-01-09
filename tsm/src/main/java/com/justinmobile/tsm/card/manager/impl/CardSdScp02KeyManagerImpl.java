package com.justinmobile.tsm.card.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.card.dao.CardSdScp02KeyDao;
import com.justinmobile.tsm.card.domain.CardSdScp02Key;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardSdScp02KeyManager;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Service("cardSdScp02KeyManager")
public class CardSdScp02KeyManagerImpl extends EntityManagerImpl<CardSdScp02Key, CardSdScp02KeyDao> implements CardSdScp02KeyManager {

	@Autowired
	private CardSdScp02KeyDao cardSdScp02KeyDao;

	public CardSdScp02Key getByCardSdKeyProfile(CardSecurityDomain cardSecurityDomain, KeyProfile keyProfile) {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" from ").append(CardSdScp02Key.class.getName());
			hql.append(" where cardSecurityDomain.id=? and keyProfile.id=? and status=? ");
			return cardSdScp02KeyDao.findUniqueEntity(hql.toString(), cardSecurityDomain.getId(), keyProfile.getId(),
					Integer.valueOf(CardSdScp02Key.VALID_STATUS));
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	public KeyProfile getKeyProfile(CardSecurityDomain cardSecurityDomain, int keyType) {
		try {
			CardSdScp02Key cardSdScp02Key = cardSdScp02KeyDao.findUniqueEntity("from " + CardSdScp02Key.class.getName()
					+ " as csk where csk.cardSecurityDomain.id = ? and csk.keyProfile.keyType = ? and csk.status = ?",
					cardSecurityDomain.getId(), keyType, CardSdScp02Key.VALID_STATUS);
			return cardSdScp02Key.getKeyProfile();
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	
}
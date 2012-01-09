package com.justinmobile.tsm.card.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardLoadFileDao;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;

@Service("cardLoadFileManager")
public class CardLoadFileManagerImpl extends EntityManagerImpl<CardLoadFile, CardLoadFileDao> implements CardLoadFileManager {

	@Autowired
	private CardLoadFileDao cardLoadFileDao;

	@Override
	public CardLoadFile getByCardAndLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion) {
		try {
			return cardLoadFileDao.getByCardAndLoadFileVersion(card, loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardLoadFile getByAidAndCardNo(String aid, String cardNo) {
		try {
			return cardLoadFileDao.getByAidAndCardNo(aid, cardNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardLoadFile> getCardLoadFileBySd(long sdId, String cardNo) throws PlatformException {
		try {
			return cardLoadFileDao.getCardLoadFileBySd(sdId, cardNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardLoadFile> getByCardAndLoadFileSd(CardInfo card, SecurityDomain sd) {
		try {
			return cardLoadFileDao.getByCardAndLoadFileSd(card, sd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardLoadFile> getByCard(CardInfo card) {
		try {
			return cardLoadFileDao.findByProperty("card", card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
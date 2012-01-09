package com.justinmobile.tsm.card.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.card.dao.CardAppletDao;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardAppletManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;

@Service("cardAppletManager")
public class CardAppletManagerImpl extends EntityManagerImpl<CardApplet, CardAppletDao> implements CardAppletManager {

	@Autowired
	private CardAppletDao cardAppletDao;

	@Autowired
	private CardInfoManager cardManager;

	@Override
	public boolean isIntallOnCard(String cardNo, String appletAid) {
		try {
			CardApplet cardApplet = cardAppletDao.getBycardNoAndAppletAid(cardNo, appletAid);
			return cardApplet != null;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplet> getByCardNoAndAppAid(String cardNo, String appAid) {
		try {
			return cardAppletDao.getByCardNoAndAppAid(cardNo, appAid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardApplet getByCardNoAndAppletAid(String cardNo, String appletAid) throws PlatformException {
		try {
			return cardAppletDao.getBycardNoAndAppletAid(cardNo, appletAid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplet> getByCardNoAndApplicationVersionThatCreateLoadFileVersion(String cardNo, ApplicationVersion applicationVersion,
			LoadFileVersion loadFileVersion) {
		try {
			CardInfo card = cardManager.getByCardNo(cardNo);
			return cardAppletDao.getByCardNoAndApplicationVersionThatCreateLoadFileVersion(card, applicationVersion, loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplet> getByCardAndAppSd(String cardNo, String sdAid) throws PlatformException {
		try {
			return cardAppletDao.getByCardAndAppSd(cardNo, sdAid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplet> getByCard(CardInfo card) {
		try {
			return cardAppletDao.findByProperty("card", card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardApplet getByCardAndApplet(CardInfo card, Applet applet) {
		try {
			return cardAppletDao.getByCardAndApplet(card, applet);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplet> getByCardNoThatCreateLoadFileVersion(String cardNo, LoadFileVersion loadFileVersion) {
		try {
			CardInfo card = cardManager.getByCardNo(cardNo);
			return cardAppletDao.getByCardNoThatCreateLoadFileVersion(card, loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
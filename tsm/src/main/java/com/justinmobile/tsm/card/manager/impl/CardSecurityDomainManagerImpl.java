package com.justinmobile.tsm.card.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.dao.CardSecurityDomainDao;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.engine.TransactionHelper;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("cardSecurityDomainManager")

public class CardSecurityDomainManagerImpl extends EntityManagerImpl<CardSecurityDomain, CardSecurityDomainDao> implements
		CardSecurityDomainManager {

	@Autowired
	private CardSecurityDomainDao cardSecurityDomainDao;

	@Autowired
	private CardInfoManager cardInfoManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private CardLoadFileManager cardLoadFileManager;

	@SuppressWarnings("unused")
	@Autowired
	private TransactionHelper transactionHelper;

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Override
	public CardSecurityDomain getByCardNoAid(String cardNo, String aid) {
		try {
			return cardSecurityDomainDao.getByCardNoAid(cardNo, aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardSecurityDomain getbySdAndCard(CardInfo card, SecurityDomain securityDomain) {
		try {
			String hql = "from " + CardSecurityDomain.class.getName() + " as csd where csd.card = ? and csd.sd = ?";
			return cardSecurityDomainDao.findUniqueEntity(hql, card, securityDomain);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardSecurityDomain getISdByCci(CustomerCardInfo cci) {
		try {
			String hql = "from " + CardSecurityDomain.class.getName() + " as csd where csd.card = ? and csd.sd.model = ?";
			return cardSecurityDomainDao.findUniqueEntity(hql, cci.getCard(), SecurityDomain.MODEL_ISD);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardSecurityDomain> getByCard(CardInfo card) {
		try {
			return cardSecurityDomainDao.findByProperty("card", card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkAndGetDelAppForDelSd(Map<String, Object> resultMap, String cardNo, String sdId) {
		try {
			List<Map<String, Object>> delAppList = new ArrayList<Map<String, Object>>();
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			SecurityDomain sd = securityDomainManager.load(Long.valueOf(sdId));

			this.checkDeletable(card, sd);
			List<CardApplication> caList = cardApplicationManager.getByCardAndApplicationSd(card, sd);
			List<CardLoadFile> clfList = cardLoadFileManager.getCardLoadFileBySd(sd.getId(), cardNo);

			/*
			 * for (CardApplication cardApplication : caList) { if
			 * (CardApplication.STATUS_UNDOWNLOAD.intValue() !=
			 * cardApplication.getStatus().intValue()) { throw new
			 * PlatformException(PlatformErrorCode.CARD_SD_RELATING_OBJECT); } }
			 */

			Set<Application> delAppSet = new HashSet<Application>();
			for (CardApplication ca : caList) {
				if (ca.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD
						&& ca.getStatus().intValue() != CardApplication.STATUS_DOWNLOADED
						&& ca.getStatus().intValue() != CardApplication.STATUS_INSTALLED) {
					delAppSet.add(ca.getApplicationVersion().getApplication());
				}
			}
			for (CardLoadFile clf : clfList) {
				Set<ApplicationLoadFile> alfSet = clf.getLoadFileVersion().getApplicationLoadFiles();
				for (ApplicationLoadFile alf : alfSet) {
					delAppSet.add(alf.getApplicationVersion().getApplication());
				}
			}

			for (Application app : delAppSet) {
				Map<String, Object> delAppMap = new HashMap<String, Object>();
				delAppMap.put("aid", app.getAid());
				delAppMap.put("operation", LocalTransaction.Operation.DELETE_APP.getType());
				delAppList.add(delAppMap);
			}

			if (delAppList.size() > 0) {
				Map<String, Object> delAppMap = new HashMap<String, Object>();
				delAppMap.put("aid", sd.getAid());
				delAppMap.put("operation", LocalTransaction.Operation.DELETE_SD.getType());
				delAppList.add(delAppMap);
				resultMap.put("options", delAppList);
				resultMap.put("delList", true);
			} else {
				resultMap.put("delList", false);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkDeletable(CardInfo card, SecurityDomain securityDomain) {
		if (securityDomain.isIsd()) {// 如果是主安全域，抛出异常
			throw new PlatformException(PlatformErrorCode.ISD_CAN_NOT_DELETE);
		}

		if (SecurityDomain.CANNOT_DELETE == securityDomain.getDeleteRule().intValue()) {// 如果安全域的删除规则是“不能删除”，抛出异常
			throw new PlatformException(PlatformErrorCode.SD_NOT_DEL_BY_RULE);
		}

		if (isPreset(card, securityDomain)) {// 如果安全域是预置的，不能删除
			throw new PlatformException(PlatformErrorCode.SD_IS_PRSET_FOR_NOT_DEL);
		}
	}

	private boolean isPreset(CardInfo card, SecurityDomain securityDomain) {
		CardBaseSecurityDomain cardBaseSecurityDomain = cardBaseSecurityDomainManager.getBySdAndCardBaseId(securityDomain,
				card.getCardBaseInfo());
		if (null != cardBaseSecurityDomain) {
			if (CardBaseSecurityDomain.PRESET == cardBaseSecurityDomain.getPreset().intValue()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void checkDel(String cardNo, String sdId) {
		try {
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			SecurityDomain sd = securityDomainManager.load(Long.valueOf(sdId));
			this.checkDeletable(card, sd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardSecurityDomain> getByCardThatOnCard(String cardNo) {
		try {
			CardInfo card = cardInfoManager.getByCardNo(cardNo);

			Set<Integer> status = new HashSet<Integer>();
			status.add(CardSecurityDomain.STATUS_CREATED);
			status.add(CardSecurityDomain.STATUS_KEY_UPDATED);
			status.add(CardSecurityDomain.STATUS_PERSO);
			status.add(CardSecurityDomain.STATUS_LOCK);

			return cardSecurityDomainDao.getByCardThatInStatus(card, status);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
    
	@Override
	@Transactional
	public List<SecurityDomain> getSdByCardNo(String cardNo) {
		try {
			return cardSecurityDomainDao.getSdByCardNo(cardNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}
	
}
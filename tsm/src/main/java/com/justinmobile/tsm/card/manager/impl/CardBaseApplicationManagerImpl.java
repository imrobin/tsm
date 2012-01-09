package com.justinmobile.tsm.card.manager.impl;

import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardBaseLoadFileManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;

@Service("cardBaseApplicationManager")
public class CardBaseApplicationManagerImpl extends EntityManagerImpl<CardBaseApplication, CardBaseApplicationDao> implements
		CardBaseApplicationManager {

	@Autowired
	private CardBaseApplicationDao cardBaseApplicationDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Autowired
	private CardBaseLoadFileManager cardBaseLoadFileManager;

	@Autowired
	private CardInfoDao cardInfoDao;

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	/*
	 * (non Javadoc) <p>Title: doLink</p> <p>Description: </p>
	 * 
	 * @param appVer
	 * 
	 * @param cardids
	 * 
	 * @see
	 * com.justinmobile.tsm.card.manager.CardBaseApplicationManager#doLink(java
	 * .lang.String, java.lang.String[]) 应用发布时关联版本
	 */
	@Override
	public void doLink(String[] appVer, String[] cardids) {
		try {
			for (int j = 0; j < cardids.length; j++) {
				String[] cardidGet = cardids[j].split(":");
				CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardidGet[0]));
				for (int i = 0; i < appVer.length; i++) {
					ApplicationVersion appver = applicationVersionManager.load(Long.valueOf(appVer[i]));
					int presetMode = Integer.parseInt(cardidGet[1]);
					saveCardBaseApplication(cbi, presetMode, appver);
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

	private void doLinkCardBaseLoadFileForAppver(CardBaseApplication cba) {
		ApplicationVersion appVer = cba.getApplicationVersion();
		Set<ApplicationLoadFile> loadFileSet = appVer.getApplicationLoadFiles();
		for (ApplicationLoadFile alf : loadFileSet) {
			CardBaseLoadFile cblf = cardBaseLoadFileManager.getByCardBaseAndLoadFile(cba.getCardBase(), alf.getLoadFileVersion());
			if (null == cblf) {
				CardBaseLoadFile checkCBLF = cardBaseLoadFileManager.getByLoadfileAndCardbase(alf.getLoadFileVersion().getLoadFile(),
						cba.getCardBase());
				if (null != checkCBLF) {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_LOADFILEVERSION_FOR_LOADFILE, alf.getLoadFileVersion()
							.getLoadFile().getName());
				}
				SecurityDomain sd = alf.getLoadFileVersion().getLoadFile().getSd();
				CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cba.getCardBase());
				if (null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
					cblf = new CardBaseLoadFile();
					cblf.setCardBaseInfo(cba.getCardBase());
					cblf.setLoadFileVersion(alf.getLoadFileVersion());
					cardBaseLoadFileManager.saveOrUpdate(cblf);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST, "文件\""
							+ alf.getLoadFileVersion().getLoadFile().getName() + "\"该版本");
				}
			}
		}
	}

	@Override
	public List<CardBaseApplication> getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(CardBaseInfo cardBaseInfo,
			Application app) {
		try {
			return cardBaseApplicationDao.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(cardBaseInfo, app);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardBaseApplication> findByApplicationVersion(ApplicationVersion av) {
		try {
			return cardBaseApplicationDao.findByProperty("applicationVersion", av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.justinmobile.tsm.card.manager.CardBaseApplicationManager#cardBaseDoLink
	 * (java.lang.String[], java.lang.String) 卡批次关联应用版本
	 */
	@Override
	public void cardBaseDoLink(String[] appVerIdS, String cardId) {
		try {
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardId));
			for (int i = 0; i < appVerIdS.length; i++) {
				String[] appverIdArray = appVerIdS[i].split(":");
				int presetMode = Integer.parseInt(appverIdArray[1]);
				ApplicationVersion appver = applicationVersionManager.load(Long.valueOf(appverIdArray[0]));
				saveCardBaseApplication(cbi, presetMode, appver);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public CardBaseApplication saveCardBaseApplication(CardBaseInfo cbi, int presetMode, ApplicationVersion appver) {
		CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, appver);
		if (null == cba) {
			cba = new CardBaseApplication();
			cba.setCardBase(cbi);
			cba.setApplicationVersion(appver);
			cba.setPresetMode(presetMode);
			if (cba.isPreset()) {
				List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
				if (checkList.size() > 0) {
					throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARDNAME, cbi.getName(), "应用");
				}
				Application application = appver.getApplication();
				CardBaseApplication checkCba = cardBaseApplicationDao.getByApplicationAndCardbaseAndPresetMode(application, cbi);
				if (null != checkCba) {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_APPVER_FOR_APPLICATION, application.getName());
				}
				SecurityDomain sd = application.getSd();
				CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cbi);
				if (null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
					doLinkCardBaseLoadFileForAppver(cba);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST, "应用\""
							+ cba.getApplicationVersion().getApplication().getName() + "\"该版本");
				}
			}
			cardBaseApplicationDao.saveOrUpdate(cba);
		}
		return cba;
	}

	@Override
	public void delLink(String cbaId) {
		try {
			String[] cbainfo = cbaId.split(":");
			CardBaseApplication cba = cardBaseApplicationDao.load(Long.valueOf(cbainfo[0]));
			CardBaseInfo cbi = cba.getCardBase();
			if (cba.getPreset().equals(Boolean.TRUE)) {
				List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
				if (checkList.size() > 0) {
					throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
				}
				removeAppVerContactLoadFileInCardBase(cba);
			}
			cardBaseApplicationDao.remove(cba);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void removeAppVerContactLoadFileInCardBase(CardBaseApplication cba) {
		Set<ApplicationLoadFile> alfSet = cba.getApplicationVersion().getApplicationLoadFiles();
		for (ApplicationLoadFile alf : alfSet) {
			boolean flag = true;
			Set<ApplicationLoadFile> contactAlfSet = alf.getLoadFileVersion().getApplicationLoadFiles();
			for (ApplicationLoadFile contactAlf : contactAlfSet) {
				ApplicationVersion av = contactAlf.getApplicationVersion();
				List<CardBaseApplication> cbaList = cardBaseApplicationDao.findByProperty("applicationVersion", av);
				for (CardBaseApplication contactCba : cbaList) {
					if (contactCba.getPreset() && contactCba.getCardBase().equals(cba.getCardBase()) && contactCba.getId() != cba.getId()) {
						flag = false;
					}
				}
			}
			if (flag) {
				CardBaseLoadFile cardBaseLoadFile = cardBaseLoadFileManager.getByCardBaseAndLoadFile(cba.getCardBase(),
						alf.getLoadFileVersion());
				if (null != cardBaseLoadFile) {
					cardBaseLoadFileManager.remove(cardBaseLoadFile);
				}
			}
		}
	}

	@Override
	public void changePrest(String cbaId) {
		try {
			String[] cbaPrests = cbaId.split(":");
			CardBaseApplication cba = cardBaseApplicationDao.load(Long.valueOf(cbaPrests[0]));
			CardBaseInfo cbi = cba.getCardBase();
			List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
			if (checkList.size() > 0) {
				throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
			}
			if (Integer.parseInt(cbaPrests[1]) == CardBaseApplication.MODE_CREATE) {
				SecurityDomain sd = cba.getApplicationVersion().getApplication().getSd();
				CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cba.getCardBase());
				if (null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {// 判断所属安全域是否存在
					if (cba.getApplicationVersion().getStatus().intValue() == ApplicationVersion.STATUS_ARCHIVE.intValue()) {
						throw new PlatformException(PlatformErrorCode.CANT_CHANGE_BY_APP_ARCHIVE);
					}
					// 判断是否已经有所属应用的其他版本预置
					CardBaseApplication presetCba = cardBaseApplicationDao.getByApplicationAndCardbaseAndPresetMode(cba
							.getApplicationVersion().getApplication(), cbi);
					if (null != presetCba) {
						if (presetCba.getId().intValue() != cba.getId().intValue()) {
							throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_APPVER_FOR_APPLICATION, cba
									.getApplicationVersion().getApplication().getName());
						}
					}
					if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY) {
						cba.setPresetMode(Integer.valueOf(cbaPrests[1]));
						doLinkCardBaseLoadFileForAppver(cba);
					} else if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_PERSONAL) {
						cba.setPresetMode(Integer.valueOf(cbaPrests[1]));
					}
					doLinkCardBaseLoadFileForAppver(cba);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST, "应用\""
							+ cba.getApplicationVersion().getApplication().getName() + "\"该版本");
				}
			} else if (Integer.parseInt(cbaPrests[1]) == CardBaseApplication.MODE_PERSONAL) {
				SecurityDomain sd = cba.getApplicationVersion().getApplication().getSd();
				CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cba.getCardBase());
				if (null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {// 判断所属安全域是否存在
					if (cba.getApplicationVersion().getStatus().intValue() == ApplicationVersion.STATUS_ARCHIVE.intValue()) {
						throw new PlatformException(PlatformErrorCode.CANT_CHANGE_BY_APP_ARCHIVE);
					}
					// 判断是否已经有所属应用的其他版本预置
					CardBaseApplication presetCba = cardBaseApplicationDao.getByApplicationAndCardbaseAndPresetMode(cba
							.getApplicationVersion().getApplication(), cbi);
					if (null != presetCba) {
						if (presetCba.getId().intValue() != cba.getId().intValue()) {
							throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_APPVER_FOR_APPLICATION, cba
									.getApplicationVersion().getApplication().getName());
						}
					}
					if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY) {
						cba.setPresetMode(Integer.valueOf(cbaPrests[1]));
						doLinkCardBaseLoadFileForAppver(cba);
					} else if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
						cba.setPresetMode(Integer.valueOf(cbaPrests[1]));
					}
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST, "应用\""
							+ cba.getApplicationVersion().getApplication().getName() + "\"该版本");
				}
			} else {
				if (cba.isPreset()) {
					removeAppVerContactLoadFileInCardBase(cba);
				}
				cba.setPresetMode(Integer.valueOf(cbaPrests[1]));
			}
			cardBaseApplicationDao.saveOrUpdate(cba);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardBaseApplication getByCardBaseAndApplicationVersion(CardBaseInfo cardBaseInfo, ApplicationVersion applicationVersion) {
		try {
			return cardBaseApplicationDao.getByCardBaseAndAppver(cardBaseInfo, applicationVersion);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardBaseApplication> getBySDandCBIAndPreset(SecurityDomain securityDomain, CardBaseInfo cbi) {
		try {
			return cardBaseApplicationDao.getBySDandCBIAndPreset(securityDomain, cbi);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardBaseApplication getByCardBaseAndApplicationThatPreset(CardBaseInfo cardBase, Application application) {
		try {
			return cardBaseApplicationDao.getByApplicationAndCardbaseAndPresetMode(application, cardBase);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}
}
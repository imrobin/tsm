package com.justinmobile.tsm.card.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.dao.CardBaseSecurityDomainDao;
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

@Service("cardBaseSecurityDomainManager")
public class CardBaseSecurityDomainManagerImpl extends EntityManagerImpl<CardBaseSecurityDomain, CardBaseSecurityDomainDao> implements
		CardBaseSecurityDomainManager {

	@Autowired
	private CardBaseSecurityDomainDao cardBaseSecurityDomainDao;
	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;
	@Autowired
	private SecurityDomainManager securityDomainManager;
	@Autowired
	private CardInfoDao cardInfoDao;
	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;
	@Autowired
	private CardBaseLoadFileManager cardBaseLoadFileManager;

	@Override
	public void doLink(String cardid, String sdid, String preset, String presetKeyVersion, String presetMode) {
		try {
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardid));
			List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
			SecurityDomain sd = securityDomainManager.load(Long.valueOf(sdid));
			CardBaseSecurityDomain cbsd = cardBaseSecurityDomainDao.getByCardBaseAndSd(cbi, sd);
			if (null == cbsd) {
				cbsd = new CardBaseSecurityDomain();
				cbsd.setCardBaseInfo(cbi);
				cbsd.setSecurityDomain(sd);
				if (preset.equals(String.valueOf(CardBaseSecurityDomain.PRESET))) {
					if (checkList.size() > 0) {
						throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
					}
					cbsd.setPreset(CardBaseSecurityDomain.PRESET);
					cbsd.setPresetMode(Integer.valueOf(presetMode));
					if (Integer.valueOf(presetMode).intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
						cbsd.setPresetKeyVersion(Integer.valueOf(presetKeyVersion));
					}
				} else {
					cbsd.setPreset(CardBaseSecurityDomain.UNPRESET);
				}
				cardBaseSecurityDomainDao.saveOrUpdate(cbsd);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void delLink(String cbsddId) {
		try {
			CardBaseSecurityDomain cbsd = cardBaseSecurityDomainDao.load(Long.valueOf(cbsddId));
			CardBaseInfo cbi = cbsd.getCardBaseInfo();
			if (cbsd.getPreset().intValue()  == CardBaseSecurityDomain.PRESET) {
				List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
				if (checkList.size() > 0) {
					throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
				}
				checkDependentBySD(cbsd.getSecurityDomain(),cbi);
			}
			cardBaseSecurityDomainDao.remove(cbsd);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void checkDependentBySD(SecurityDomain securityDomain, CardBaseInfo cbi) {
		List<CardBaseApplication> cbaList = cardBaseApplicationManager.getBySDandCBIAndPreset(securityDomain,cbi);
		if(CollectionUtils.isNotEmpty(cbaList)){
			throw new PlatformException(PlatformErrorCode.DEPENDENT_SD);
		}
		List<CardBaseLoadFile> loadFileList =  cardBaseLoadFileManager.getBySdAndCardBase(securityDomain,cbi);
		if(CollectionUtils.isNotEmpty(loadFileList)){
			throw new PlatformException(PlatformErrorCode.DEPENDENT_SD);
		}
	}

	@Override
	public void changePrest(String cbsddId, String sdId, String preset, String presetKeyVersion, String presetMode) {
		try {
			CardBaseSecurityDomain cbsd = cardBaseSecurityDomainDao.load(Long.valueOf(cbsddId));
			CardBaseInfo cbi = cbsd.getCardBaseInfo();
			boolean isOrg = checkIsOrgin(sdId, preset, presetKeyVersion, presetMode, cbsd);
			if (!isOrg) {
				List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
				if (checkList.size() > 0) {
					throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
				}
			}
			SecurityDomain sd = securityDomainManager.load(Long.valueOf(sdId));
			if (sd.getId() != cbsd.getSecurityDomain().getId()) {
				CardBaseSecurityDomain preCbsd = cardBaseSecurityDomainDao.getByCardBaseAndSd(cbsd.getCardBaseInfo(), sd);
				if (null != preCbsd) {
					throw new PlatformException(PlatformErrorCode.PRE_CBSD_IS_EXIST);
				}
			}
			if (cbsd.getPreset().intValue() == CardBaseSecurityDomain.UNPRESET) {
				if (Integer.valueOf(preset).intValue() == CardBaseSecurityDomain.PRESET) {
					if (cbsd.getSecurityDomain().getStatus().intValue() == SecurityDomain.STATUS_ARCHIVED) {
						throw new PlatformException(PlatformErrorCode.CANT_CHANGE_BY_SD_ARCHIVE);
					}
					cbsd.setPreset(Integer.valueOf(preset));
					cbsd.setPresetMode(Integer.valueOf(presetMode));
					if (Integer.valueOf(presetMode).intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
						cbsd.setPresetKeyVersion(Integer.valueOf(presetKeyVersion));
					}
				}
			} else {
				if (Integer.valueOf(preset).intValue() == CardBaseSecurityDomain.UNPRESET) {
					if (cbsd.getSecurityDomain().isIsd()) {
						throw new PlatformException(PlatformErrorCode.CANT_ISD_NOT_UNPSET);
					}
					checkDependentBySD(sd,cbi);
					cbsd.setPreset(Integer.valueOf(preset));
					cbsd.setPresetMode(null);
					cbsd.setPresetKeyVersion(null);
				} else {
					if (cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_CARETE) {
						if (Integer.valueOf(presetMode).intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
							cbsd.setPresetMode(Integer.valueOf(presetMode));
							cbsd.setPresetKeyVersion(Integer.valueOf(presetKeyVersion));
						}
					} else {
						if(sd.getId().longValue() != cbsd.getSecurityDomain().getId().longValue()) {
							checkDependentBySD(cbsd.getSecurityDomain(),cbi);
						}
						if (Integer.valueOf(presetMode).intValue() == CardBaseSecurityDomain.PRESET_MODE_CARETE) {
							checkDependentBySD(cbsd.getSecurityDomain(),cbi);
							cbsd.setPresetMode(Integer.valueOf(presetMode));
							cbsd.setPresetKeyVersion(null);
						}else{
							cbsd.setPresetKeyVersion(Integer.valueOf(presetKeyVersion));
						}
					}
				}
			}
			cbsd.setSecurityDomain(sd);
			cardBaseSecurityDomainDao.saveOrUpdate(cbsd);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private boolean checkIsOrgin(String sdId, String preset, String presetKeyVersion, String presetMode, CardBaseSecurityDomain cbsd) {
		boolean isOrg = false;
		if (cbsd.getPreset().intValue() == CardBaseSecurityDomain.UNPRESET && cbsd.getPreset().intValue() == Integer.valueOf(preset)) {
			return true;
		}
		if (cbsd.getSecurityDomain().getId().intValue() == Long.valueOf(sdId).intValue()
				&& cbsd.getPreset().intValue() == Long.valueOf(preset).intValue()) {
			if (cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET) {
				if (cbsd.getPresetMode().intValue() == Long.valueOf(presetMode).intValue()) {
					if (cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
						if (cbsd.getPresetKeyVersion().intValue() == Integer.valueOf(presetKeyVersion).intValue()) {
							isOrg = true;
						}
					} else {
						isOrg = true;
					}
				}
			} else {
				isOrg = true;
			}
		}
		return isOrg;
	}

	@Override
	public List<CardBaseSecurityDomain> findBySecurityDomain(SecurityDomain sd) {
		try {
			return cardBaseSecurityDomainDao.findByProperty("securityDomain", sd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardBaseSecurityDomain getBySdAndCardBaseId(SecurityDomain sd, CardBaseInfo cbi) throws PlatformException {
		try {
			return cardBaseSecurityDomainDao.getByCardBaseAndSd(cbi, sd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Boolean checkSDisISD(String cbsdId) {
		try {
			CardBaseSecurityDomain cbsd = this.load(Long.valueOf(cbsdId));
			return cbsd.getSecurityDomain().isIsd();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
    
	@Override
	public List<CardBaseSecurityDomain> getByCardBase(CardBaseInfo cbi) throws PlatformException {
		
		try {
			return cardBaseSecurityDomainDao.getUninstallSdByCardBase(cbi);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
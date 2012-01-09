package com.justinmobile.tsm.card.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;
import com.justinmobile.tsm.card.dao.CardBaseLoadFileDao;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardBaseLoadFileManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;

@Service("cardBaseLoadFileManager")
public class CardBaseLoadFileManagerImpl extends EntityManagerImpl<CardBaseLoadFile, CardBaseLoadFileDao> implements CardBaseLoadFileManager {

	@Autowired
	private CardBaseLoadFileDao cardBaseLoadFileDao;
	@Autowired
	private LoadFileVersionManager loadFileVersionManager;
	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;
	@Autowired
	private CardInfoDao cardInfoDao;
	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;
	
	@Override
	public List<CardBaseLoadFile> getBaseLoadFileByCardBase(CardBaseInfo cbi) {
		try {
			return cardBaseLoadFileDao.findByProperty("cardBaseInfo", cbi);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public void doLink(String cardid, String loadfileIds) {
		try {
			String[] loadfileIdArray = loadfileIds.split(",");
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardid));
			List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
			if(checkList.size() > 0){
				throw new  PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
			}else{
				for(int i=0;i<loadfileIdArray.length;i++){
					LoadFileVersion lfv = loadFileVersionManager.load(Long.valueOf(loadfileIdArray[i]));
					CardBaseLoadFile cblf = cardBaseLoadFileDao.getByCardBaseAndVersion(cbi,lfv);
					if(null == cblf){
						CardBaseLoadFile checkCBLF = this.getByLoadfileAndCardbase(lfv.getLoadFile(),cbi);
						if(null != checkCBLF){
							throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_LOADFILEVERSION_FOR_LOADFILE,lfv.getLoadFile().getName());
						}
						SecurityDomain sd = lfv.getLoadFile().getSd();
						CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd,cbi);
						if(null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED){
							cblf = new CardBaseLoadFile();
							cblf.setCardBaseInfo(cbi);
							cblf.setLoadFileVersion(lfv);
							cardBaseLoadFileDao.saveOrUpdate(cblf);
						} else {
							throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST,"文件\"" + lfv.getLoadFile().getName() + "\"该版本");
						}
					}
				}
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
	public CardBaseLoadFile getByCardBaseAndLoadFile(CardBaseInfo cardBase, LoadFileVersion loadFileVersion) {
		try {
			return cardBaseLoadFileDao.getByCardBaseAndVersion(cardBase, loadFileVersion);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void delLink(String cbldId) {
		try {
			CardBaseLoadFile cbld = cardBaseLoadFileDao.load(Long.valueOf(cbldId));
			CardBaseInfo cbi = cbld.getCardBaseInfo();
			List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
			if(checkList.size() > 0){
				throw new  PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
			}else{
//				cbi.setAvlEepromSize(cbi.getAvlEepromSize() + cbld.getLoadFileVersion().getSpaceInfo().getNvm());
//				cbi.setAvlRamSize(cbi.getAvlRamSize() + cbld.getLoadFileVersion().getSpaceInfo().getRam());
				cardBaseLoadFileDao.remove(cbld);
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
	public CardBaseLoadFile getByLoadfileAndCardbase(LoadFile loadFile, CardBaseInfo cardBase) {
		try {
			return cardBaseLoadFileDao.getByLoadfileAndCardbase(loadFile,cardBase);
		} catch (PlatformException pe) {	
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardBaseLoadFile> getBySdAndCardBase(SecurityDomain securityDomain, CardBaseInfo cbi) {
		try {
			return cardBaseLoadFileDao.getBySdAndCardBase(securityDomain,cbi);
		} catch (PlatformException pe) {	
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}}
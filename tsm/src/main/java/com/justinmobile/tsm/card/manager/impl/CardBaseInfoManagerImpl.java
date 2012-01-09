package com.justinmobile.tsm.card.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationVersionTestReportManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.dao.CardBaseInfoDao;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;

@Service("cardBaseInfoManager")
public class CardBaseInfoManagerImpl extends EntityManagerImpl<CardBaseInfo, CardBaseInfoDao> implements CardBaseInfoManager {

	@Autowired
	private CardBaseInfoDao cardBaseInfoDao;
	
	@Autowired
	private CardInfoDao cardInfoDao;
	
	@Autowired
	private SecurityDomainManager securityDomainManager;
	
	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;
	
	@Autowired
	private ApplicationVersionTestReportManager applicationVersionTestReportManager;
	
	@Override
	public void addCardBaseInfo(CardBaseInfo cbi) {
		try {
			if(cbi.getStartNo().length() == 19){
				cbi.setStartNo(cbi.getStartNo() + "0");
			}
			if(cbi.getEndNo().length() == 19){
				cbi.setEndNo(cbi.getEndNo() + "F");
			}
			List<CardBaseInfo> list = cardBaseInfoDao.findByProperty("batchNo", cbi.getBatchNo());
			if(list.size()!=0){
				throw new PlatformException(PlatformErrorCode.CARD_BASE_IS_EXIST);
			}
			boolean flag = checkCardNoScope(cbi);
			if(!flag){
				throw new PlatformException(PlatformErrorCode.CARDNO_INT_SCOPE);
			}
			if(cbi.getPresetRamType().intValue() == 0){
				cbi.setPlatformType(null);
			}
			cardBaseInfoDao.saveOrUpdate(cbi);
			saveCardbaseSDForISD(cbi);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}

	private void saveCardbaseSDForISD(CardBaseInfo cbi) {
		SecurityDomain sd = securityDomainManager.getIsd();
		CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cbi);
		if(null == cbsd){
			cbsd =  new CardBaseSecurityDomain();
			cbsd.setCardBaseInfo(cbi);
			cbsd.setSecurityDomain(sd);
			cbsd.setPreset(CardBaseSecurityDomain.PRESET);
			cbsd.setPresetKeyVersion(cbi.getCardKeyVersion());
			cbsd.setPresetMode(CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED);
			cardBaseSecurityDomainManager.saveOrUpdate(cbsd);
		}
	}

	private boolean checkCardNoScopeForModify(CardBaseInfo cbi,CardBaseInfo oldcbi) {
		String startNo = cbi.getStartNo();
		String endNo = cbi.getEndNo();
		if(startNo.compareToIgnoreCase(endNo)>0){
			throw new  PlatformException(PlatformErrorCode.START_NO_GT_END_NO);
		}
		List<CardBaseInfo> testFileList = cardBaseInfoDao.findInScope(cbi);
		if(testFileList.size() == 1){
			if(testFileList.get(0).getId() == oldcbi.getId()){
				return true;
			}else{
				return false;
			}
		} else if(testFileList.size() == 0){
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean checkCardNoScope(CardBaseInfo cbi) {
		String startNo = cbi.getStartNo();
		String endNo = cbi.getEndNo();
		if(startNo.compareToIgnoreCase(endNo)>0){
			throw new  PlatformException(PlatformErrorCode.START_NO_GT_END_NO);
		}
		List<CardBaseInfo> testFileList = cardBaseInfoDao.findInScope(cbi);
		if(CollectionUtils.isNotEmpty(testFileList)){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void modifyCardBaseInfo(String oldId,CardBaseInfo cbi) {
		try {
			if(cbi.getStartNo().length() == 19){
				cbi.setStartNo(cbi.getStartNo() + "0");
			}
			if(cbi.getEndNo().length() == 19){
				cbi.setEndNo(cbi.getEndNo() + "F");
			}
			CardBaseInfo oldcbi = cardBaseInfoDao.load(Long.valueOf(oldId));
			if(!oldcbi.getBatchNo().equals(cbi.getBatchNo())){
				List<CardBaseInfo> list = cardBaseInfoDao.findByProperty("batchNo", cbi.getBatchNo());
				if(list.size()!=0){
					throw new PlatformException(PlatformErrorCode.CARD_BASE_IS_EXIST_MODIFY);
				}
			}
			boolean scopeFlag = false;
			if(oldcbi.getStartNo().equals(cbi.getStartNo()) && oldcbi.getEndNo().equals(cbi.getEndNo())){
				scopeFlag = true;
			}
			if(!checkPublishCard(cbi) && !scopeFlag){
				boolean flag = checkCardNoScopeForModify(cbi,oldcbi);
				if(!flag){
					throw new PlatformException(PlatformErrorCode.CARDNO_INT_SCOPE);
				}
			}
			boolean cardKeyVersioFlag  = false;
			if(cbi.getCardKeyVersion().intValue() == oldcbi.getCardKeyVersion().intValue()){
				cardKeyVersioFlag = true;
			}
			if(!checkPublishCard(cbi) && !cardKeyVersioFlag){
				modifyCardBaseSDKeyVersionForISD(cbi.getCardKeyVersion(),oldcbi);
			}
			if(cbi.getPresetRamType().intValue() == 0){
				cbi.setPlatformType(null);
			}
			BeanUtils.copyProperties(cbi, oldcbi);
			cardBaseInfoDao.saveOrUpdate(oldcbi);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void modifyCardBaseSDKeyVersionForISD(Integer cardKeyVersion, CardBaseInfo oldcbi) {
		SecurityDomain sd = securityDomainManager.getIsd();
		CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, oldcbi);
		if(null != cbsd){
			cbsd.setPresetKeyVersion(cardKeyVersion);
			cardBaseSecurityDomainManager.saveOrUpdate(cbsd);
		}
	}

	@Override
	public void checkRemove(Long cbiId) {
		try {
			CardBaseInfo cbi = cardBaseInfoDao.load(cbiId);
			List<CardInfo> cardInfoList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
			if(cardInfoList.size()>0){
				throw  new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARD);
			}
			cardBaseInfoDao.remove(cbiId);
			removeCardBaseSDForISD(cbi);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void removeCardBaseSDForISD(CardBaseInfo cbi) {
		SecurityDomain sd = securityDomainManager.getIsd();
		CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cbi);
		if(null != cbsd){
			cardBaseSecurityDomainManager.remove(cbsd);
		}
	}

	@Override
	public CardBaseInfo getCardBaseInfoByCardNo(String cardNo) {
		try {
			return cardBaseInfoDao.getCardBaseInfoByCardNo(cardNo);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean checkPublishCard(CardBaseInfo cardBaseInfo) {
		try {
			List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo",cardBaseInfo);
			if (checkList.size() > 0) {
				return true;
			}else{
				return false;
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
	public List<Map<String, Object>> findTestedCardBase(Page<ApplicationVersionTestReport> page, String appVerId) {
		try {
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			page = applicationVersionTestReportManager.findByAppverAndTestpass(page,Long.valueOf(appVerId));
			List<ApplicationVersionTestReport> pageReusltList = page.getResult();
			for(ApplicationVersionTestReport avtr : pageReusltList){
				CardBaseInfo cardBase = avtr.getCardBaseInfo();
				mapList.add(cardBase.toMap(null, null));
			}
			return mapList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}}
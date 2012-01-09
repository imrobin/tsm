package com.justinmobile.tsm.application.manager.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.JoinType;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.dao.SecurityDomainApplyDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.Privilege;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.cms2ac.dao.KeyProfileApplyDao;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.domain.KeyProfileApply;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;
import com.justinmobile.tsm.cms2ac.manager.KeyProfileManager;
import com.justinmobile.tsm.system.dao.RequistionDao;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionFactory;

@Service("securityDomainManager")
public class SecurityDomainManagerImpl extends EntityManagerImpl<SecurityDomain, SecurityDomainDao> implements SecurityDomainManager {

	private static final Logger logger = LoggerFactory.getLogger(SecurityDomainManagerImpl.class);

	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private SecurityDomainDao securityDomainDao;

	@Autowired
	private SecurityDomainApplyDao securityDomainApplyDao;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Autowired
	private RequistionDao requistionDao;

	@Autowired
	private AppletDao appletDao;

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Autowired
	private CardInfoManager cardInfoManager;

	@Autowired
	private LoadFileDao loadFileDao;

	@Autowired
	private KeyProfileManager keyProfileManager;
	
	@Autowired
	private HsmkeyConfigManager hsmkeyConfigManager;
	
	@Autowired
	private KeyProfileApplyDao keyProfileApplyDao;
	
	@Override
	public SecurityDomain getIsd() {
		return securityDomainDao.getIsd();
	}

	@Override
	public boolean validateSecurityDomainAid(String aid, String originalAid) {
		final String property = "aid";
		if (StringUtils.isBlank(originalAid)) {
			originalAid = null;
		} else {
			originalAid = originalAid.toUpperCase();
		}
		if (StringUtils.isBlank(aid)) {
			aid = null;
		} else {
			aid = aid.toUpperCase();
		}

		// true可用，false不可用
		boolean formal = securityDomainDao.isPropertyUnique(property, aid, originalAid);
		boolean temp = securityDomainApplyDao.isPropertyUniqueForAidByStatus(aid, originalAid);
		boolean appAid = applicationDao.isPropertyUnique(property, aid, originalAid);
		boolean appletAid = appletDao.isPropertyUnique(property, aid, originalAid);
		boolean fileAid = loadFileDao.isPropertyUnique(property, aid, originalAid);

		boolean bln = (formal && temp && appAid && appletAid && fileAid);
		logger.debug("\n\nformal:" + formal + "|temp:" + temp + "|appAid:" + appAid + "|appletAid:" + appletAid + "|fileAid:" + fileAid
				+ "|result:" + bln);
		return bln;
	}

	@Override
	public void applySecurityDomain(SecurityDomain sd, Requistion requistion) {
		try {
			securityDomainDao.saveOrUpdate(sd);
			requistionDao.saveOrUpdate(requistion);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean handlePublishedApply(SecurityDomainApply apply, boolean result, Map<String, Object> params) throws PlatformException {

		try {
			if (result) {
				apply.setStatus(SecurityDomain.STATUS_PUBLISHED);

				SecurityDomain sd = new SecurityDomain();
				BeanUtils.copyProperties(apply, sd, new String[] { "id" });
				sd.setHasLock(SecurityDomain.UNLOCK);
				securityDomainDao.saveOrUpdate(sd);

				Requistion requistion = apply.getRequistion();
				requistion.setOriginalId(sd.getId());
				requistionDao.saveOrUpdate(requistion);
				
				List<KeyProfileApply> list = this.keyProfileApplyDao.findByProperty("securityDomainApply", apply);
				if(list != null && !list.isEmpty()) {
					List<HsmkeyConfig> hsmkeyConfigs = null;
					String hsmkeyConfigString = null;
					for(KeyProfileApply keyProfileApply : list) {
						int key = keyProfileApply.getIndex();
						KeyProfile keyProfile = new KeyProfile();
						BeanUtils.copyProperties(keyProfileApply, keyProfile, new String[]{"id"});
						keyProfile.setSecurityDomain(sd);
						
						switch (key) {
						case KeyProfile.INDEX_DEK:
							hsmkeyConfigString = apply.getHsmkeyConfigDEK();
							keyProfile.setValue(apply.getKeyProfileDEK());
							if(StringUtils.isNotBlank(hsmkeyConfigString)) {
								hsmkeyConfigs = getHsmkeyConfigs(hsmkeyConfigString);
							}
							break;
						case KeyProfile.INDEX_ENC:
							hsmkeyConfigString = apply.getHsmkeyConfigENC();
							keyProfile.setValue(apply.getKeyProfileENC());
							if(StringUtils.isNotBlank(hsmkeyConfigString)) {
								hsmkeyConfigs = getHsmkeyConfigs(hsmkeyConfigString);
							}
							break;
						case KeyProfile.INDEX_MAC:
							hsmkeyConfigString = apply.getHsmkeyConfigMAC();
							keyProfile.setValue(apply.getKeyProfileMAC());
							if(StringUtils.isNotBlank(hsmkeyConfigString)) {
								hsmkeyConfigs = getHsmkeyConfigs(hsmkeyConfigString);
							}
							break;
						default:
							break;
						}
						keyProfile.setHsmKeyConfigs(hsmkeyConfigs);
						this.keyProfileManager.saveOrUpdate(keyProfile);
					}
				} 
				
			}
			securityDomainApplyDao.saveOrUpdate(apply);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return false;
	}

	private List<HsmkeyConfig> getHsmkeyConfigs(String hsmkeyConfigString) {
		List<HsmkeyConfig> list = new ArrayList<HsmkeyConfig>();
		String[] ids = hsmkeyConfigString.split(",");
		for(String id : ids) {
			HsmkeyConfig e = this.hsmkeyConfigManager.load(Long.valueOf(id));
			list.add(e);
		}
		return list;
	}
	
	@Override
	public boolean apply(SecurityDomain sd) {
		boolean bln = false;
		try {
			SecurityDomainApply apply = new SecurityDomainApply();
			apply.setApplyType(SecurityDomainApply.APPLY_TYPE_NEW);
			apply.setApplyDate(Calendar.getInstance());
			sd.setLoadModule(getIsd().getLoadModule());
			BeanUtils.copyProperties(sd, apply);
			securityDomainDao.saveOrUpdate(sd);

			// 创建SD发布申请历史记录
			Requistion recordApply = RequistionFactory.getPublishForSD();
			recordApply.setOriginalId(sd.getId());
			apply.setRequistion(recordApply);
			securityDomainApplyDao.saveOrUpdate(apply);

			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return bln;
	}

	@Override
	public boolean applySecurityDomain(SecurityDomainApply apply) throws PlatformException {
		boolean bln = false;
		try {
			//验证URL
			if(Privilege.parse(apply.getPrivilege()).isToken()) {
				applicationManager.validateBuissinessUrl(apply.getBusinessPlatformUrl(), apply.getServiceName());
			}
			
			apply.setApplyType(SecurityDomainApply.APPLY_TYPE_NEW);
			apply.setApplyDate(Calendar.getInstance());

			// 创建SD发布申请历史记录
			Requistion requistion = RequistionFactory.getPublishForSD();
			apply.setRequistion(requistion);

			requistionDao.saveOrUpdate(requistion);

			securityDomainApplyDao.saveOrUpdate(apply);

			//组装KeyProfile
			String keyProfileString = apply.getKeyProfileENC();
			KeyProfileApply enc = new KeyProfileApply(apply, KeyProfile.INDEX_ENC, keyProfileString);
			enc = getKeyProfileApply(apply.getHsmkeyConfigENC(), enc);
			this.keyProfileApplyDao.saveOrUpdate(enc);
			
			keyProfileString = apply.getKeyProfileMAC();
			KeyProfileApply mac = new KeyProfileApply(apply, KeyProfile.INDEX_MAC, keyProfileString);
			mac = getKeyProfileApply(apply.getHsmkeyConfigMAC(), mac);
			this.keyProfileApplyDao.saveOrUpdate(mac);
			
			keyProfileString = apply.getKeyProfileDEK();
			KeyProfileApply dek = new KeyProfileApply(apply, KeyProfile.INDEX_DEK, keyProfileString);
			dek = getKeyProfileApply(apply.getHsmkeyConfigDEK(), dek);
			this.keyProfileApplyDao.saveOrUpdate(dek);
			
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return bln;
	}

	private KeyProfileApply getKeyProfileApply(String keyProfileString, KeyProfileApply keyProfile) {
		List<HsmkeyConfig> hsmKeyConfigs = new ArrayList<HsmkeyConfig>();
		if(StringUtils.isNotBlank(keyProfileString)) {
			hsmKeyConfigs.clear();
			String[] ids = keyProfileString.split(",");
			for(String id : ids) {
				HsmkeyConfig e = hsmkeyConfigManager.load(Long.valueOf(id));
				hsmKeyConfigs.add(e);
			}
			keyProfile.setHsmKeyConfigs(hsmKeyConfigs);
		}
		return keyProfile;
	}
	
	/**
	 * 归档申请处理：<br/>
	 * 通过：状态置为已归档 被拒：SD状态不变
	 */
	@Override
	public boolean handleArchivedApply(SecurityDomainApply apply, boolean result) throws PlatformException {
		boolean bln = false;

		try {
			SecurityDomain sd = securityDomainDao.load(apply.getRequistion().getOriginalId());
			if (result) {
				sd.setStatus(SecurityDomain.STATUS_ARCHIVED);
				sd.setHasLock(SecurityDomain.UNLOCK);
				apply.setStatus(SecurityDomain.STATUS_ARCHIVED);
				// 1、如果该应用在card_base_application有预置关联关系，且该卡批次没有发卡，归档成功后，还要删除card_base_application对应记录
				// 2、如果该应用在card_base_application有预置关联关系，且该卡批次已经发卡，不允许归档
				// 3、如果该应用在card_base_application没有预置关联关系，归档成功后，不动card_base_application
				List<CardBaseSecurityDomain> cardBaseSecurityDomains = cardBaseSecurityDomainManager.findBySecurityDomain(sd);
				List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
				for (CardBaseSecurityDomain cardBaseSecurityDomain : cardBaseSecurityDomains) {
					if (cardBaseSecurityDomain.getPreset() != null
							&& cardBaseSecurityDomain.getPreset().intValue() == CardBaseSecurityDomain.PRESET) {
						filters.clear();
						filters.add(new PropertyFilter("cardBaseInfo", JoinType.L, "id", MatchType.EQ, PropertyType.L,
								cardBaseSecurityDomain.getCardBaseInfo() == null ? "-1" : cardBaseSecurityDomain.getCardBaseInfo().getId()
										+ ""));
						List<CardInfo> cardInfos = cardInfoManager.find(filters);
						if (cardInfos.size() == 0) {
							cardBaseSecurityDomainManager.remove(cardBaseSecurityDomain.getId());
						} else {
							return false;
						}
					}
				}
			} else {
				apply.setApplyResult(Requistion.RESULT_REJECT_CH);
			}
			sd.setHasLock(SecurityDomain.UNLOCK);
			securityDomainDao.saveOrUpdate(sd);
			securityDomainApplyDao.saveOrUpdate(apply);

			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return bln;
	}

	@Override
	public boolean handleModifyApply(SecurityDomainApply apply, boolean result) throws PlatformException {
		boolean bln = false;

		try {
			Requistion requistion = apply.getRequistion();
			SecurityDomain originSd = securityDomainDao.load(requistion.getOriginalId());
			if (result) {
				originSd.setSdName(apply.getSdName());
				originSd.setScp02SecurityLevel(apply.getScp02SecurityLevel());
				originSd.setInstallParams(apply.getInstallParams());
				originSd.setBusinessPlatformUrl(apply.getBusinessPlatformUrl());
				originSd.setServiceName(apply.getServiceName());
			} else {
				apply.setApplyResult(Requistion.RESULT_REJECT_CH);
			}
			originSd.setHasLock(SecurityDomain.UNLOCK);
			securityDomainDao.saveOrUpdate(originSd);
			securityDomainApplyDao.saveOrUpdate(apply);
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return bln;
	}

	/**
	 * 撤销只针对申请状态为未审核的，包括审核未通过的SD发布申请
	 */
	@Override
	public void cancelApply(Long id) throws PlatformException {

		try {


			Requistion requistion = requistionDao.load(id);
			
			SecurityDomain sd = securityDomainDao.load(requistion.getOriginalId());
			requistionDao.remove(requistion);
			securityDomainApplyDao.remove(requistion.getId());

			sd.setHasLock(SecurityDomain.UNLOCK);
			securityDomainDao.saveOrUpdate(sd);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public void deleteApply(Long id) throws PlatformException {
		try {

			keyProfileApplyDao.removeAll(id);
			securityDomainApplyDao.remove(id);
			requistionDao.remove(id);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	// 提交归档申请
	@Override
	public void archiveApply(Long id, String reason) throws PlatformException {
		try {
			SecurityDomain sd = securityDomainDao.load(id);
			sd.setHasLock(SecurityDomain.LOCK);
			SecurityDomainApply apply = new SecurityDomainApply();
			BeanUtils.copyProperties(sd, apply, new String[] { "id" });
			
			List<KeyProfile> list = sd.getKeyProfiles();
			if(list != null && !list.isEmpty()) {
				for(KeyProfile kp : list) {
					Integer index = kp.getIndex();
					if(index.equals(KeyProfile.INDEX_DEK)) {
						apply.setKeyProfileDEK(kp.getValue());
					} else if(index.equals(KeyProfile.INDEX_ENC)) {
						apply.setKeyProfileENC(kp.getValue());
					} else if(index.equals(KeyProfile.INDEX_MAC)) {
						apply.setKeyProfileMAC(kp.getValue());
					}
				}
			}
			
			apply.setApplyDate(Calendar.getInstance());
			apply.setApplyType(SecurityDomainApply.APPLY_TYPE_ARCHIVE);

			Requistion requistion = RequistionFactory.getRequistion(Requistion.TYPE_SD_ARCHIVE);
			requistion.setOriginalId(id);
			requistion.setReason(reason);

			requistionDao.saveOrUpdate(requistion);

			apply.setRequistion(requistion);
			securityDomainApplyDao.saveOrUpdate(apply);

			securityDomainDao.saveOrUpdate(sd);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean validateApplicationOfSercurityDomainStatus(Long sdId) throws PlatformException {
		boolean bln = false;

		try {

			String hql = "from Application a where a.sd.id = ? and a.status <> ?";
			List<Application> applications = applicationDao.find(hql, sdId, Application.STATUS_ARCHIVED);
			boolean isApplications = true;
			if (applications != null && !applications.isEmpty()) {
				isApplications = false;
			}
			hql = "select alf.applicationVersion.application from LoadFile as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where lf.sd.id = ? and alf.applicationVersion.application.status <> ?";
			@SuppressWarnings("rawtypes")
			List loadFiles = loadFileDao.find(hql, sdId, Application.STATUS_ARCHIVED);
			boolean isLoadFiles = true;
			if (loadFiles != null && !loadFiles.isEmpty()) {
				isLoadFiles = false;
			}

			bln = isApplications && isLoadFiles;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return bln;
	}

	@Override
	public boolean modifyApply(SecurityDomain form, String reason) throws PlatformException {
		boolean bln = false;

		try {

			//URL 验证
			//委托管理模式下，才验证业务平台URL
			if(Privilege.parse(form.getPrivilege()).isToken()) {
				applicationManager.validateBuissinessUrl(form.getBusinessPlatformUrl(), form.getServiceName());
			}
			
			SecurityDomain originSd = securityDomainDao.load(form.getId());
			originSd.setHasLock(SecurityDomain.LOCK);

			SecurityDomainApply apply = new SecurityDomainApply();
			BeanUtils.copyProperties(originSd, apply, new String[] { "id" });
			//安全域发布状态下可修改的字段
			apply.setSdName(form.getSdName());
			apply.setInstallParams(form.getInstallParams());
			apply.setScp02SecurityLevel(form.getScp02SecurityLevel());
			apply.setBusinessPlatformUrl(form.getBusinessPlatformUrl());
			apply.setServiceName(form.getServiceName());

			apply.setApplyDate(Calendar.getInstance());
			apply.setApplyType(SecurityDomainApply.APPLY_TYPE_MODIFY);

			Requistion requistion = RequistionFactory.getRequistion(Requistion.TYPE_SD_MODIFY);
			requistion.setOriginalId(originSd.getId());
			requistion.setReason(reason);
			requistionDao.saveOrUpdate(requistion);

			apply.setRequistion(requistion);
			securityDomainApplyDao.saveOrUpdate(apply);

			securityDomainDao.saveOrUpdate(originSd);
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return bln;
	}

	@Override
	public boolean modifyApply(SecurityDomainApply apply) throws PlatformException {
		boolean bln = false;

		try {
			SecurityDomainApply original = securityDomainApplyDao.load(apply.getId());
			original.setSdName(apply.getSdName());
			original.setPrivilege(apply.getPrivilege());
			original.setModel(apply.getModel());
			original.setInstallParams(apply.getInstallParams());
			original.setDeleteRule(apply.getDeleteRule());
			original.setNoneVolatileSpace(apply.getNoneVolatileSpace());
			original.setVolatileSpace(apply.getVolatileSpace());
			original.setScp02SecurityLevel(apply.getScp02SecurityLevel());
			original.setKeyProfileDEK(apply.getKeyProfileDEK());
			original.setKeyProfileENC(apply.getKeyProfileENC());
			original.setKeyProfileMAC(apply.getKeyProfileMAC());
			original.setCurrentKeyVersion(apply.getCurrentKeyVersion());
			original.setBusinessPlatformUrl(apply.getBusinessPlatformUrl());
			original.setServiceName(apply.getServiceName());
			original.setAid(apply.getAid());
			original.setSp(apply.getSp());
			
			List<KeyProfileApply> list = this.keyProfileApplyDao.findByProperty("securityDomainApply", original);
			if(list != null && !list.isEmpty()) {
				for(KeyProfileApply e : list) {
					int key = e.getIndex();
					switch (key) {
					case KeyProfile.INDEX_DEK:
						e.setValue(apply.getKeyProfileDEK());
						break;
					case KeyProfile.INDEX_ENC:
						e.setValue(apply.getKeyProfileENC());
						break;
					case KeyProfile.INDEX_MAC:
						e.setValue(apply.getKeyProfileMAC());
						break;
					default:
						break;
					}
					this.keyProfileApplyDao.saveOrUpdate(e);
				}
			}
			
			Requistion requistion = original.getRequistion();
			requistion.setStatus(Requistion.STATUS_INIT);
			requistion.setSubmitDate(Calendar.getInstance());
			requistion.setReviewDate(null);
			requistion.setResult(null);
			requistion.setOpinion(null);
			securityDomainApplyDao.saveOrUpdate(original);
			requistionDao.saveOrUpdate(requistion);

			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return bln;
	}

	@Override
	public SecurityDomain getByAid(String sdAid) {
		try {
			return securityDomainDao.findUniqueByProperty("aid", sdAid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<Requistion> findPageForSD(Page<Requistion> page) throws PlatformException {

		try {
			page = requistionDao.findPageForSD(page);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return page;
	}

	@Override
	public Requistion getRequistionForSecurityDomain(Long sdId) throws PlatformException {
		Requistion requistion = null;

		try {

			requistion = requistionDao.findRequistionByOriginalIdAndType(sdId, Requistion.TYPE_SD_ARCHIVE, Requistion.TYPE_SD_MODIFY);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		return requistion;
	}

	@Override
	public Page<SecurityDomain> findUnLinkPage(Page<SecurityDomain> page, String cardBaseId) {
		try {
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardBaseId));
			String hql = "from SecurityDomain as sd where sd.status = ? and sd not in (select cbd.securityDomain from CardBaseSecurityDomain as cbd where cbd.cardBaseInfo = ?)";
			return securityDomainDao.findPage(page, hql, SecurityDomain.STATUS_PUBLISHED, cbi);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<SecurityDomain> findPageBy(Page<SecurityDomain> page, Map<String, Object> queryParams) throws PlatformException {
		return securityDomainDao.findPageByStatus(page, queryParams);
	}
	
	@Override
	public Page<Map<String, Object>> findPage(Page<Map<String, Object>> page, Map<String, Object> queryParams) throws PlatformException {
		try {
			page = securityDomainDao.findPage(page, queryParams);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return page;
	}

	@Override
	public boolean isAidUnique(String aid) {
		try {
			aid = aid.toUpperCase();
			return securityDomainDao.isPropertyUnique("aid", aid, null) && securityDomainApplyDao.isPropertyUniqueForAidByStatus(aid, null);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<SecurityDomain> getByName(String sdName) {
		try {
			return securityDomainDao.findByProperty("sdName", sdName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<SecurityDomain> getByLikeName(String sdName) {
		try {
			return securityDomainDao.getByLikeName(sdName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	
	
	@Override
	public void updateHsmkeyConfigBySecurityDomain(SecurityDomain sd, Map<String, String> map) throws PlatformException {
		List<KeyProfile> list = sd.getKeyProfiles();
		if(list != null && !list.isEmpty()) {
			String dekIds = map.get("dekIds");
			String encIds = map.get("encIds");
			String macIds = map.get("macIds");
			
			String keyProfileDEK = map.get("keyProfileDEK");
			String keyProfileENC = map.get("keyProfileENC");
			String keyProfileMAC = map.get("keyProfileMAC");
			for(KeyProfile keyProfile : list) {
				int key = keyProfile.getIndex();
				switch (key) {
				case KeyProfile.INDEX_DEK:
					if(StringUtils.isNotBlank(dekIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(dekIds));
					}
					keyProfile.setValue(keyProfileDEK);
					break;
				case KeyProfile.INDEX_ENC:
					if(StringUtils.isNotBlank(encIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(encIds));
					}
					keyProfile.setValue(keyProfileENC);
					break;
				case KeyProfile.INDEX_MAC:
					if(StringUtils.isNotBlank(macIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(macIds));
					}
					keyProfile.setValue(keyProfileMAC);
					break;
				default:
					break;
				}
				
				this.keyProfileManager.saveOrUpdate(keyProfile);
			}
		}
	}

	@Override
	public void updateHsmkeyConfigBySecurityDomain(SecurityDomainApply apply, String encIds, String macIds, String dekIds) throws PlatformException {
		List<KeyProfileApply> list = this.keyProfileApplyDao.findByProperty("securityDomainApply", apply);
		if(list != null && !list.isEmpty()) {
			String keyProfileDEK = apply.getKeyProfileDEK();
			String keyProfileENC = apply.getKeyProfileENC();
			String keyProfileMAC = apply.getKeyProfileMAC();
			for(KeyProfileApply keyProfile : list) {
				int key = keyProfile.getIndex();
				switch (key) {
				case KeyProfile.INDEX_DEK:
					if(StringUtils.isNotBlank(dekIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(dekIds));
					}
					keyProfile.setValue(keyProfileDEK);
					break;
				case KeyProfile.INDEX_ENC:
					if(StringUtils.isNotBlank(encIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(encIds));
					}
					keyProfile.setValue(keyProfileENC);
					break;
				case KeyProfile.INDEX_MAC:
					if(StringUtils.isNotBlank(macIds)) {
						keyProfile.setHsmKeyConfigs(getHsmkeyConfigs(macIds));
					}
					keyProfile.setValue(keyProfileMAC);
					break;
				default:
					break;
				}
				
				this.keyProfileApplyDao.saveOrUpdate(keyProfile);
			}
		}
		this.securityDomainApplyDao.saveOrUpdate(apply);
	}
	
	
}
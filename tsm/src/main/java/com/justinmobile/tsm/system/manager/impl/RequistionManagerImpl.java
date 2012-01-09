package com.justinmobile.tsm.system.manager.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
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
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.dao.ApplicationKeyProfileDao;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseLoadFileDao;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;
import com.justinmobile.tsm.system.dao.RequistionDao;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Service("requistionManager")
public class RequistionManagerImpl extends EntityManagerImpl<Requistion, RequistionDao> implements RequistionManager {

	@Autowired
	private RequistionDao requistionDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;
	@Autowired
	private LoadFileDao loadFileDao;
	@Autowired
	private SecurityDomainManager securityDomainManager;
	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;
	@Autowired
	private CardBaseApplicationDao cardBaseApplicationDao;
	@Autowired
	private CardBaseLoadFileDao cardBaseLoadFileDao;
	@Autowired
	private CardInfoManager cardInfoManager;
	@Autowired
	private SysUserManager sysUserManager;
	@Autowired
	private HsmkeyConfigManager hsmkeyConfigManager;
	@Autowired
	private ApplicationKeyProfileDao applicationKeyProfileDao;

	@Override
	public Page<Requistion> findPageByParam(Page<Requistion> page, Map<String, String> paramMap) {
		SysUser currentUser = sysUserManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
		return requistionDao.findPageByParam(page, paramMap,currentUser);
	}

	@Override
	public Requistion getRequistionByTypeAndId(Integer type, Long originalId) {
		List<Requistion> list = requistionDao.find("from Requistion t where t.type = ? and t.originalId = ? order by t.id desc", type,
				originalId);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int getCountByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId) {
		try {
			return requistionDao.getCountByTypeAndStatusAndOrignaId(type, status, originalId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Requistion> getByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId) {
		try {
			return requistionDao.getByTypeAndStatusAndOrignaId(type, status, originalId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public JsonMessage updatePublish(String status, int typeOriginal, String sdIdsStr, String opinion, Requistion ac, String typeTk, String typeKek
			, String hsmkeyConfigTK, String hsmkeyConfigKEK) {
		JsonMessage message = new JsonMessage();
		ac.setOpinion(opinion);
		Boolean isAllArchived = true; // 是否全部归档
		if (status.equals(Requistion.STATUS_PASS + "")) { // 3 ,审核通过
			ac.setResult(Requistion.RESULT_PASS);
			ApplicationVersion appVersion = applicationVersionManager.load(ac.getOriginalId());
			if (typeOriginal == Requistion.TYPE_APP_UPLOAD) {// 11-- 应用发布申请12
																// --应用归档申请13
																// --应用上传申请14
																// --应用信息修改申请21
																// --安全域发布申请22
																// --安全域归档申请31
																// --SP注册申请
				if (!appVersion.getStatus().equals(ApplicationVersion.STATUS_UPLOADED)) {
					message.setSuccess(Boolean.FALSE);
					message.setMessage("该版本不是已上传状态");
					return message;
				}
				appVersion.setStatus(ApplicationVersion.STATUS_AUDITED);// 审核通过，多应用平台保存审核结果，并修改应用状态为“发布已审核”
				if (appVersion.getApplication().getStatus() == Application.STATUS_TO_BE_AUDITED) {
					appVersion.getApplication().setStatus(Application.STATUS_AUDITED);
				}
				String[] sdIds = sdIdsStr.split(",");// 更新loadfile第三方安全域sdid
				for (int i = 0; !sdIdsStr.equals("") && i < sdIds.length; i++) {
					String loadFileId = sdIds[i].split(";")[0];
					String sdId = sdIds[i].split(";")[1];
					LoadFile loadFile = loadFileDao.load(Long.parseLong(loadFileId));
					if (loadFile.getSd() == null) {
						SecurityDomain sd = securityDomainManager.load(Long.parseLong(sdId));
						loadFile.setSd(sd);
						loadFileDao.saveOrUpdate(loadFile);
					}
				}
				
				// 加入密钥
				if (StringUtils.isNotEmpty(typeTk)){
//					HsmkeyConfig hc = new HsmkeyConfig();
//					hc.setIndex(Integer.parseInt(typeTk));
//					if (tkVersion != null && !tkVersion.equals("")){
//						hc.setVersion(Integer.parseInt(tkVersion));
//					}
//					if (!StringUtils.isEmpty(tkVendor)){
//						hc.setVendor(tkVendor);
//					}
//					hsmkeyConfigProfileDao.saveOrUpdate(hc);
					ApplicationKeyProfile akp = new ApplicationKeyProfile();
					//akp.setKeyIndex(Integer.parseInt(typeTk));
					akp.setKeyType(ApplicationKeyProfile.TYPE_TK);
					akp.setKeyValue(typeTk);
					akp.setApplication(appVersion.getApplication());
					akp.setHsmKeyConfigs(this.getHsmkeyConfigs(hsmkeyConfigTK));
				//	akp.getHsmKeyConfigs().add(hc);
					applicationKeyProfileDao.saveOrUpdate(akp);
				}
				if (StringUtils.isNotEmpty(typeKek)){
					ApplicationKeyProfile akp = new ApplicationKeyProfile();
					akp.setKeyValue(typeKek);
					akp.setKeyType(ApplicationKeyProfile.TYPE_KEK);
					akp.setApplication(appVersion.getApplication());
					akp.setHsmKeyConfigs(this.getHsmkeyConfigs(hsmkeyConfigKEK));
					applicationKeyProfileDao.saveOrUpdate(akp);
				}
				//end
			} else if (typeOriginal == Requistion.TYPE_APP_ARCHIVE) {
				if (!appVersion.getStatus().equals(ApplicationVersion.STATUS_PULISHED)) {
					message.setSuccess(Boolean.FALSE);
					message.setMessage("该版本不是已发布状态");
					return message;
				}
				// 1、如果该应用在card_base_application有预置关联关系，且该卡批次没有发卡，归档成功后，还要删除card_base_application对应记录
				// 2、如果该应用在card_base_application有预置关联关系，且该卡批次已经发卡，不允许归档
				// 3、如果该应用在card_base_application没有预置关联关系，归档成功后，不动card_base_application
				List<CardBaseApplication> cardBaseApplications = cardBaseApplicationManager.findByApplicationVersion(appVersion);
				List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
				for (CardBaseApplication cardBaseApplication : cardBaseApplications) {
					if (cardBaseApplication.getPreset()) {
						filters.clear();
						filters.add(new PropertyFilter("cardBaseInfo", JoinType.L, "id", MatchType.EQ, PropertyType.L, cardBaseApplication
								.getCardBase() == null ? "-1" : cardBaseApplication.getCardBase().getId() + ""));
						List<CardInfo> cardInfos = cardInfoManager.find(filters);
						if (cardInfos.size() == 0) {
							cardBaseApplicationDao.remove(cardBaseApplication.getId());
							//4.归档后删除关联文件  
							Set<ApplicationLoadFile> applicationLoadFiles = cardBaseApplication.getApplicationVersion().getApplicationLoadFiles();
							for (ApplicationLoadFile applicationLoadFile : applicationLoadFiles) {
								LoadFileVersion loadFileVersion = applicationLoadFile.getLoadFileVersion();
								List<CardBaseLoadFile> cardBaseLoadFiles = cardBaseLoadFileDao.findByProperty("loadFileVersion", loadFileVersion);
								for (CardBaseLoadFile cardBaseLoadFile : cardBaseLoadFiles) {
									if (cardBaseLoadFile.getCardBaseInfo().getId() == cardBaseApplication.getCardBase().getId()){
										cardBaseLoadFileDao.remove(cardBaseLoadFile);
									}
								}
							}
						} else {
							throw new PlatformException(PlatformErrorCode.APPLICATION_VERSION_IS_PRESET);
						}
					}
				}
				appVersion.setStatus(ApplicationVersion.STATUS_ARCHIVE);// 审核通过，多应用平台保存审核结果，并修改应用状态为“归档”
				appVersion.setArchiveDate(Calendar.getInstance());
				List<ApplicationVersion> appVersions = appVersion.getApplication().getVersions();
				for (ApplicationVersion av : appVersions) {
					if (av.getId() != appVersion.getId() && !av.getStatus().equals(ApplicationVersion.STATUS_ARCHIVE)) {
						isAllArchived = false;
					}
				}
				if (isAllArchived) { // 全部归档 ，应用归档
					if (!appVersion.getApplication().getStatus().equals(Application.STATUS_PUBLISHED)) {
						message.setSuccess(Boolean.FALSE);
						message.setMessage("该应用不是已发布状态");
						return message;
					}
					appVersion.getApplication().setStatus(Application.STATUS_ARCHIVED);
					appVersion.getApplication().setArchivedDate(Calendar.getInstance());
				}
			}
			applicationVersionManager.saveOrUpdate(appVersion);
		} else {
			ac.setResult(Requistion.RESULT_REJECT);
			 //发布审核不通过操作后，application_version的状态仍然为已测试，
				//将导致后续流程无法执行（因无法再修改/重新提交审核）
				//所以，在发布审核不通过后，还要修改application_version.status=1（已上传）
			/*if (typeOriginal == Requistion.TYPE_APP_PUBLISH) {
				ApplicationVersion appVersion = applicationVersionManager.load(ac.getOriginalId());
				appVersion.setStatus(ApplicationVersion.STATUS_UPLOADED);
				applicationVersionManager.saveOrUpdate(appVersion);
			}*/
		}
		ac.setStatus(Integer.parseInt(status));
		ac.setReviewDate(Calendar.getInstance());
		return message;
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
}
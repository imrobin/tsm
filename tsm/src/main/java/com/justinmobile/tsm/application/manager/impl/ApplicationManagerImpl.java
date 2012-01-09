package com.justinmobile.tsm.application.manager.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.ApplicationImageDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationImage;
import com.justinmobile.tsm.application.domain.ApplicationType;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationTypeManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.RecommendApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.system.domain.MobileSection;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.MobileSectionManager;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Service("applicationManager")
public class ApplicationManagerImpl extends EntityManagerImpl<Application, ApplicationDao> implements ApplicationManager {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationManagerImpl.class);

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private SpBaseInfoManager spManager;

	@Autowired
	private SecurityDomainManager sdManager;

	@Autowired
	private ApplicationTypeManager applicationTypeManager;

	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;

	@Autowired
	private LoadFileManager loadFileManager;

	@Autowired
	private AppletManager appletManager;

	@Autowired
	private RequistionManager requistionManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	private MobileSectionManager mobileSectionManager;

	@Autowired
	private SysUserManager userManager;
	
	@Autowired
	private RecommendApplicationManager recommendApplicationManager;
	
	@Autowired
	private ApplicationImageDao applicationImageDao;
	

	public ApplicationDao getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(ApplicationDao applicationDao) {
		this.applicationDao = applicationDao;
	}

	public SpBaseInfoManager getSpManager() {
		return spManager;
	}

	public void setSpManager(SpBaseInfoManager spManager) {
		this.spManager = spManager;
	}

	public SecurityDomainManager getSdManager() {
		return sdManager;
	}

	public void setSdManager(SecurityDomainManager sdManager) {
		this.sdManager = sdManager;
	}

	/** 创建一个新应用 */
	@Override
	public void createNewApplication(String username, Application application, Map<String, String> params) {
		try {
			// 根据登录名获取SP
			SpBaseInfo sp = spManager.getSpByNameOrMobileOrEmail(username);
			// 验证SP状态
			application.setSp(sp);

			// 验证AID
			application.validateAid();

			// 验证业务平台URL
			validateBuissinessUrl(application);

			// 处理类型
			long applicationTypeId = Long.parseLong(params.get("applicationTypeId"));
			ApplicationType applicationType = applicationTypeManager.load(applicationTypeId);
			application.setChildType(applicationType);

			// 创建评分统计对象
			application.creatNewStatistics();

			application.createNewVersion(params.get("versionNo"));

			// 将PC图标临时文件转为byte[]
			String pcIconTempFileAbsPath = params.get("pcIconTempFileAbsPath");
			if (StringUtils.isBlank(pcIconTempFileAbsPath)) {
				application.setPcIcon(null);
			} else {
				application.setPcIcon(ConvertUtils.file2ByteArray(pcIconTempFileAbsPath));
			}
			// 将应用截图存入application_image
			String[] applicationImgTempFileAbsPaths = params.get("applicationImgTempFileAbsPath").split(",");
			Set<ApplicationImage> appImgSet = Sets.newHashSet();
			for (String applicationImgTempFileAbsPath : applicationImgTempFileAbsPaths){
				ApplicationImage applicationImage = new ApplicationImage();
				if (!StringUtils.isBlank(applicationImgTempFileAbsPath)) {
					applicationImage.setApplication(application);
					applicationImage.setApplicationImage(ConvertUtils.file2ByteArray(applicationImgTempFileAbsPath));
					appImgSet.add(applicationImage);
				}
			}
			application.setApplicationImages(appImgSet);

			// 将Mobile图标临时文件转为byte[]
			String mobileIconTempFileAbsPath = params.get("mobileIconTempFileAbsPath");
			if (StringUtils.isBlank(mobileIconTempFileAbsPath)) {
				application.setMoblieIcon(null);
			} else {
				application.setMoblieIcon(ConvertUtils.file2ByteArray(mobileIconTempFileAbsPath));
			}

			// 根据安全域模式获取安全域
			SecurityDomain sd = null;
			sd = selectSd(application, params, sd);
			application.setSd(sd);

			applicationDao.saveOrUpdate(application);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void validateBuissinessUrl(String businessPlatformUrl, String serviceName) {
		logger.debug("\nbusinessPlatformUrl:" + businessPlatformUrl + "\nserviceName:" + serviceName);
		try {
			URL wsdlUrl = null;
			wsdlUrl = new URL(businessPlatformUrl);

			javax.xml.ws.Service.create(wsdlUrl, new QName(NameSpace.CM, serviceName));
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.APPLICAION_URL_SERVICE_ERROR, e);
		}

	}

	private void validateBuissinessUrl(Application application) {
		validateBuissinessUrl(application.getBusinessPlatformUrl(), application.getServiceName());
	}

	private SecurityDomain selectSd(Application application, Map<String, String> params, SecurityDomain sd) {
		if (SecurityDomain.MODEL_ISD == application.getSdModel()) {// 如果是主安全域，自动关联
			sd = sdManager.getIsd();
		} else if (SecurityDomain.MODEL_APPLICATION_SELECTABLE.contains(application.getSdModel())) {// 如果是需要指定具体安全域的安全域模式，查找具体安全域
			sd = sdManager.load(Long.parseLong(params.get("sdId")));
		}
		return sd;
	}

	@Override
	public void changeAppStatus(Long appId, int status) {
		Application app = applicationDao.load(appId);
		int appStauts = app.getStatus();
		boolean canChange = checkStatusChange(appStauts, status);// 检查当前状态是否能够改变到目标状态
		if (canChange) {
			app.setStatus(status);
			applicationDao.saveOrUpdate(app);
		}
	}

	private boolean checkStatusChange(int appStauts, int status) {
		if (appStauts == status) {
			return true;
		}
		return false;
	}

	@Override
	public byte[] getPcImgByAppId(Long appId) {
		try {
			Application app = applicationDao.load(appId);
			return app.getPcIcon();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void archiveApp(Long appId) {
		try {
			Application app = applicationDao.load(appId);
			app.setArchivedDate(Calendar.getInstance());
			app.setStatus(Application.STATUS_ARCHIVED);

			List<ApplicationVersion> vers = app.getVersions();
			for (ApplicationVersion ver : vers) {
				ver.setStatus(ApplicationVersion.STATUS_ARCHIVE);
				ver.setArchiveDate(Calendar.getInstance());
				applicationVersionManager.saveOrUpdate(ver);
			}

			applicationDao.saveOrUpdate(app);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<Application> advanceSearch(Page<Application> page, Map<String, String> paramMap) {
		try {
			return applicationDao.advanceSearch(page, paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isEditable(String username, Application application) {
		SpBaseInfo requestSp = spManager.getSpByNameOrMobileOrEmail(username);
		SpBaseInfo ownerSp = application.getSp();
		if ((null != requestSp) && // 安全框架已经保证了用户已登录、帐号有效且角色为SP，这个判断是为了方便在测试时不登录可以访问此功能，后期应删除
				!requestSp.equals(ownerSp)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Application getByAid(String aid) throws PlatformException {
		try {
			return applicationDao.findUniqueByProperty("aid", aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void defChange(String appId, String deleteRule, String personalType) {
		try {
			Application app = applicationDao.load(Long.valueOf(appId));
			int deleteRuleI = Integer.valueOf(deleteRule);
			if (deleteRuleI == Application.DELETE_RULE_CAN_NOT || deleteRuleI == Application.DELETE_RULE_DELETE_ALL
					|| deleteRuleI == Application.DELETE_RULE_DELETE_DATA_ONLY) {
				app.setDeleteRule(deleteRuleI);
			}
			int personalTypeI = Integer.valueOf(personalType);
			if (personalTypeI == Application.TYPE_PERSONALIZE_APP_TO_SD || personalTypeI == Application.TYPE_PERSONALIZE_PASSTHROUGH
					|| personalTypeI == Application.TYPE_PERSONALIZE_SD_TO_APP) {
				app.setPersonalType(personalTypeI);
			}
			applicationDao.saveOrUpdate(app);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isSupport(CardInfo card, Application application) {
		try {
			List<CardBaseApplication> cardBaseApplications = cardBaseApplicationManager
					.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(card.getCardBaseInfo(), application);
			if (0 != cardBaseApplications.size()) {
				return true;
			} else {
				return false;
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
	public void modifyApplicationBaseInfo(String username, Application application, Map<String, String> params) {
		if (!isEditable(username, application)) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_AID_DISCARD);
		}

		// 验证业务平台URL
		validateBuissinessUrl(application);

		ApplicationType applicationType = applicationTypeManager.load(Long.parseLong(params.get("applicationTypeId")));
		application.setChildType(applicationType);

		// 将PC图标临时文件转为byte[]
		String pcIconTempFileAbsPath = params.get("pcIconTempFileAbsPath");
		if (StringUtils.isNotBlank(pcIconTempFileAbsPath)) {
			application.setPcIcon(ConvertUtils.file2ByteArray(pcIconTempFileAbsPath));
		}

		// 将Mobile图标临时文件转为byte[]
		String mobileIconTempFileAbsPath = params.get("mobileIconTempFileAbsPath");
		if (StringUtils.isNotBlank(mobileIconTempFileAbsPath)) {
			application.setMoblieIcon(ConvertUtils.file2ByteArray(mobileIconTempFileAbsPath));
		}
		if (Application.STATUS_INIT == application.getStatus()) {// 如果应用状态不是“初始化”，忽略所属安全域的修改
			// 修改安全域
			SecurityDomain sd = null;
			sd = selectSd(application, params, sd);
			application.setSd(sd);
		}
		// 将应用截图存入application_image
		if (StringUtils.isNotBlank(params.get("applicationImgTempFileAbsPath"))) {
			String[] applicationImgTempFileAbsPaths = params.get("applicationImgTempFileAbsPath").split(",");
			Set<ApplicationImage> appImgSet = application.getApplicationImages();
			for (Iterator<ApplicationImage> it=appImgSet.iterator();it.hasNext();){
				applicationImageDao.remove(it.next());
			}
			appImgSet.clear();
			for (String applicationImgTempFileAbsPath : applicationImgTempFileAbsPaths){
				ApplicationImage applicationImage = new ApplicationImage();
				if (!StringUtils.isBlank(applicationImgTempFileAbsPath)) {
					applicationImage.setApplication(application);
					applicationImage.setApplicationImage(ConvertUtils.file2ByteArray(applicationImgTempFileAbsPath));
					appImgSet.add(applicationImage);
				}
			}
			application.setApplicationImages(appImgSet);
		}
		applicationDao.saveOrUpdate(application);
	}

	@Override
	public Page<Application> recommendAppList(Page<Application> page) {
		try {
			SysUser currentUser = userManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
			return applicationDao.recommendAppList(page, currentUser);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<Application> getDownloadableApps(Page<Application> page, String cardNo, Map<String, ?> filters) throws PlatformException {
		try {
			return applicationDao.getDownloadableApps(page, cardNo, filters);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void validateAid(String aid) {
		try {
			aid = aid.toUpperCase();
			if (!applicationDao.isPropertyUnique("aid", aid, null)) {// 应用AID不能与其他应用的AID重复
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_REDULICATE, "其他应用");
			}

			if (!loadFileManager.isPropertyUnique("aid", aid, null)) {// 应用AID不能与文件的AID重复
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_REDULICATE, "加载文件");
			}

			if (!appletManager.isPropertyUnique("aid", aid, null)) {// 应用AID不能与实例的AID重复
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_REDULICATE, "实例");
			}

			if (!sdManager.isAidUnique(aid)) {// 应用AID不能与安全域的AID重复
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_REDULICATE, "安全");
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
	public void remove(Application application, String username) {
		try {
			if (!isEditable(username, application)) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_AID_DISCARD);
			}

			if (Application.STATUS_INIT != application.getStatus()) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_NOT_INIT);
			}

			Set<ApplicationVersion> applicationVersions = new HashSet<ApplicationVersion>();
			for (ApplicationVersion version : application.getVersions()) {
				applicationVersions.add(version);
			}

			for (ApplicationVersion version : applicationVersions) {
				List<Requistion> publishRequistions = requistionManager.getByTypeAndStatusAndOrignaId(Requistion.TYPE_APP_PUBLISH,
						Requistion.STATUS_INIT, version.getId());
				if (0 != publishRequistions.size()) {
					for (Requistion requistion : publishRequistions) {
						requistionManager.remove(requistion);
					}
				}

				List<Requistion> modifyRequistions = requistionManager.getByTypeAndStatusAndOrignaId(Requistion.TYPE_APP_MODIFY,
						Requistion.STATUS_INIT, version.getId());
				if (0 != modifyRequistions.size()) {
					for (Requistion requistion : modifyRequistions) {
						requistionManager.remove(requistion);
					}
				}

				List<Requistion> archiveRequistions = requistionManager.getByTypeAndStatusAndOrignaId(Requistion.TYPE_APP_ARCHIVE,
						Requistion.STATUS_INIT, version.getId());
				if (0 != archiveRequistions.size()) {
					for (Requistion requistion : archiveRequistions) {
						requistionManager.remove(requistion);
					}
				}
				// 删除关联批次
				List<CardBaseApplication> cardBaseApplications = cardBaseApplicationManager.findByApplicationVersion(version);
				if (CollectionUtils.isNotEmpty(cardBaseApplications)) {
					for (CardBaseApplication cardBaseApplication : cardBaseApplications) {
						cardBaseApplicationManager.remove(cardBaseApplication);
					}
				}

				// 删除应用版本
				applicationVersionManager.remove(version);
			}

			recommendApplicationManager.removeByApplication(application);
			applicationDao.remove(application);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<Application> findByAppType(Page<Application> page, Long parentId) {
		try {
			return applicationDao.findByAppType(page, parentId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public String getLocationMobileStatus(String cardNo, String appLocation) {
		CustomerCardInfo cci = customerCardInfoManager.getByCardNo(cardNo);
		if (cci == null) { // cci为空表示状态不正常，跳过归属地，直接交给核心流程
			return "";
		}
		String mobileNo = cci.getMobileNo();
		String paragraph = mobileNo.substring(0, 7);
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("paragraph", MatchType.EQ, PropertyType.S, paragraph));
		List<MobileSection> mobileSections = mobileSectionManager.find(filters);
		if (mobileSections.size() == 0) {
			return "notInMobileSection";
		}
		if (appLocation.equals(Application.LOCATION_TOTAL_NETWORK)) {
			return "";
		} else {
			for (MobileSection mobileSection : mobileSections) {
				if (!mobileSection.getProvince().equals(appLocation)) {
					return mobileSection.getProvince();
				}
			}
		}
		return "";
	}

	@Override
	public List<Map<String, Object>> getShowTypeApp() {
		List<Map<String, Object>> resutList = new ArrayList<Map<String, Object>>();
		try {
			List<ApplicationType> typeList = applicationTypeManager.getShowIndexTypeListOrderById();
			for (ApplicationType at : typeList) {
				Map<String,Object> appTypeMap = new HashMap<String,Object>();
				appTypeMap.put("typeId", at.getId());
				appTypeMap.put("typeName", at.getName());
				List<Application> appList = applicationDao.getApplistByTypeIncludeChildTypeOrderByDownloadCount(at);
				List<Map<String,Object>> appMapList = new ArrayList<Map<String, Object>>();
				for(Application app : appList) {
					if(app.getStatus().intValue() == Application.STATUS_PUBLISHED && (app.getSp().getInBlack().intValue() == SpBaseInfo.NOT_INBLACK && app.getSp().getStatus().intValue() == SpBaseInfo.STATUS_AVALIABLE)){
						Map<String,Object> appMap = new HashMap<String,Object>();
						appMap.put("appId", app.getId());
						appMap.put("appName", app.getName());
						appMapList.add(appMap);
					}
				}
				appTypeMap.put("appList", appMapList);
				resutList.add(appTypeMap);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return resutList;
	}

}
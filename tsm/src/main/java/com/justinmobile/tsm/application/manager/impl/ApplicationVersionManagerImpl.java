package com.justinmobile.tsm.application.manager.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
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
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.SpecialMobileDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SpecialMobile;
import com.justinmobile.tsm.application.domain.TestFile;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionTestReportManager;
import com.justinmobile.tsm.application.manager.TestFileManager;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseLoadFileDao;
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
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Service("applicationVersionManager")
public class ApplicationVersionManagerImpl extends EntityManagerImpl<ApplicationVersion, ApplicationVersionDao> implements
		ApplicationVersionManager {

	@Autowired
	private ApplicationVersionDao applicationVersionDao;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	private RequistionManager requistionManager;

	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;

	@Autowired
	private CardBaseApplicationDao cardBaseApplicationDao;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Autowired
	private SpecialMobileDao specialMobileDao;

	@Autowired
	private CardInfoDao cardInfoDao;

	@Autowired
	private TestFileManager testFileManager;

	@Autowired
	private CardBaseLoadFileManager cardBaseLoadFileManager;
	@Autowired
	private CardBaseLoadFileDao cardBaseLoadFileDao;

	@Autowired
	private ApplicationVersionTestReportManager applicationVersionTestReportManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	@Autowired
	private CardInfoManager cardInfoManager;
	
	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Override
	public Page<ApplicationVersion> getByAppId(Application app, Page<ApplicationVersion> page, Integer status) {
		Page<ApplicationVersion> avPage = null;
		try {
			String hql = "from " + ApplicationVersion.class.getName() + " as av where av.application = ? and av.stauts = ?";
			avPage = applicationVersionDao.findPage(page, hql, app, status);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return avPage;
	}

	/*
	 * (non Javadoc) <p>Title: changeAppVerStatus</p> <p>Description: </p>
	 * 
	 * @param verIds
	 * 
	 * @param statusTested
	 * 
	 * @see com.justinmobile.tsm.application.manager.ApplicationVersionManager#
	 * changeAppVerStatus(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void changeAppVerStatus(String verId, Integer status) {
		try {
			Long id = Long.valueOf(verId);
			ApplicationVersion av = applicationVersionDao.load(id);
			av.setStatus(status);
			applicationVersionDao.saveOrUpdate(av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	/*
	 * (non Javadoc) <p>Title: publish</p> <p>Description: </p>
	 * 
	 * @param versionIds
	 * 
	 * @see
	 * com.justinmobile.tsm.application.manager.ApplicationVersionManager#publish
	 * (java.lang.String)
	 */
	@Override
	public void publish(String versionIds, String mobiles, String cardBaseInfoId) {
		try {
			String[] verIdsArray = versionIds.split(",");
			for (int i = 0; i < verIdsArray.length; i++) {
				Long id = Long.valueOf(verIdsArray[0]);
				ApplicationVersion av = applicationVersionDao.load(id);
				// 当application.publish_date为空时，当application.status≠4（已审核），没有提示"该应用不是已审核状态"
				if (av.getApplication().getPublishDate() == null
						&& av.getApplication().getStatus().intValue() != Application.STATUS_AUDITED) {
					throw new PlatformException(PlatformErrorCode.APPLICATION_NOT_AUDIT);
				}
				av.setStatus(ApplicationVersion.STATUS_PULISHED);
				av.setPublishDate(Calendar.getInstance());
				// 版本对应手机号
				if (mobiles != null) {
					String _mobiles[] = mobiles.split(",");
					SpecialMobile speicalMobile = null;
					for (String mobile : _mobiles) {
						speicalMobile = specialMobileDao.findUniqueByProperty("mobileNo", mobile);
						if (speicalMobile == null) {
							speicalMobile = new SpecialMobile();
							speicalMobile.setMobileNo(mobile);
						}
						speicalMobile.getApplicationVersions().add(av);
						specialMobileDao.saveOrUpdate(speicalMobile);
						av.getSpeicalMobiles().add(speicalMobile);
						applicationVersionDao.saveOrUpdate(av);
					}
				}
				String cardBaseInfoIds[] = cardBaseInfoId.split(",");
				// 加入终端信息
				for (String _cardBaseInfoId : cardBaseInfoIds) {
					String _cardBaseInfoIds[] = _cardBaseInfoId.split(":");
					CardBaseInfo cbi = cardBaseInfoManager.load(Long.parseLong(_cardBaseInfoIds[0]));
					int presetMode = Integer.valueOf(_cardBaseInfoIds[1]);
					CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, av);
					if (null == cba) {
						cba = new CardBaseApplication();
						cba.setCardBase(cbi);
						cba.setApplicationVersion(av);
						cba.setPresetMode(presetMode);
						if (cba.isPreset()) {
							List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cbi);
							if (checkList.size() > 0) {
								throw new PlatformException(PlatformErrorCode.CANT_OPT_BY_CARDNAME, cbi.getName(), "应用");
							}
							Application application = av.getApplication();
							CardBaseApplication checkCba = cardBaseApplicationDao.getByApplicationAndCardbaseAndPresetMode(application, cbi);
							if(null != checkCba){
								throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_APPVER_FOR_PUBLISH, cbi.getName());
							}
							SecurityDomain sd = application.getSd();
							CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cbi);
							if(null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET && cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED){
								doLinkCardBaseLoadFileForAppver(cba);
							} else {
								throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST_FOR_PUBLISH_APP,cbi.getName());
							}
						}
						cardBaseApplicationDao.saveOrUpdate(cba);
					}
					Application app = av.getApplication();
					app.setStatus(Application.STATUS_PUBLISHED);
					if (null == app.getPublishDate()) {
						app.setPublishDate(Calendar.getInstance());
					}
					app.setLastestVersion(av.getVersionNo());
					app.setArchivedDate(null);
					applicationManager.saveOrUpdate(app);
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
				CardBaseLoadFile checkCBLF = cardBaseLoadFileManager.getByLoadfileAndCardbase(alf.getLoadFileVersion().getLoadFile(), cba.getCardBase());
				if(null != checkCBLF){
					throw new PlatformException(PlatformErrorCode.CARD_BASE_JUST_ONE_LOADFILEVERSION_FOR_LOADFILE_FOR_PUBLISH,checkCBLF.getCardBaseInfo().getName(),checkCBLF.getLoadFileVersion().getLoadFile().getName());
				}
				SecurityDomain sd = alf.getLoadFileVersion().getLoadFile().getSd();
				CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, cba.getCardBase());
				if(null != cbsd && cbsd.getPreset().intValue() == CardBaseSecurityDomain.PRESET){
					cblf = new CardBaseLoadFile();
					cblf.setCardBaseInfo(cba.getCardBase());
					cblf.setLoadFileVersion(alf.getLoadFileVersion());
					cardBaseLoadFileManager.saveOrUpdate(cblf);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_BASE_SD_NOT_EXIST_FOR_PUBLISH_FILE,alf.getLoadFileVersion().getLoadFile().getName(),cba.getCardBase().getName());
				}
			}
		}
	}

	@Override
	public void completeCreateApplicationVersion(long applicationVersionId) {
		try {
			ApplicationVersion applicationVersion = applicationVersionDao.load(applicationVersionId);

			if (applicationVersion.getApplicationLoadFiles().size() == 0) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_ATLEAST_ONE_LOADFILE);
			}

			if (applicationVersion.getApplets().size() == 0) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_ATLEAST_ONE_APPLET);
			}

			int aidSameBetweenAppletAndApplicationcount = 0;
			for (Applet applet : applicationVersion.getApplets()) {
				if (applet.getAid().equals(applicationVersion.getApplication().getAid())) {
					aidSameBetweenAppletAndApplicationcount++;
				}
			}
			if (1 != aidSameBetweenAppletAndApplicationcount) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_APPLET_AID_UNMACTH);
			}

			// 计算卡空间
			applicationVersion.calcSpaceInfo();

			// 下载顺序偏序化
			List<ApplicationLoadFile> downloadOrder = applicationLoadFileManager.getAllByDownloadOrder(applicationVersion);
			for (int i = 0; i < downloadOrder.size(); i++) {
				ApplicationLoadFile applicationLoadFile = downloadOrder.get(i);
				applicationLoadFile.setDownloadOrder(i + 1);
				applicationLoadFileManager.saveOrUpdate(applicationLoadFile);
			}

			// 删除顺序偏序化
			List<ApplicationLoadFile> deleteOrder = applicationLoadFileManager.getAllByDeleteOrder(applicationVersion);
			for (int i = 0; i < deleteOrder.size(); i++) {
				ApplicationLoadFile applicationLoadFile = deleteOrder.get(i);
				applicationLoadFile.setDeleteOrder(i + 1);
				applicationLoadFileManager.saveOrUpdate(applicationLoadFile);
			}

			// 如果应用状态是“初始化”，说明该版本是第一个提交审核的版本，修改应用状态为“待审核”
			Application application = applicationVersion.getApplication();
			if (Application.STATUS_INIT == application.getStatus().intValue()) {
				application.setStatus(Application.STATUS_TO_BE_AUDITED);
				applicationManager.saveOrUpdate(application);
			}

			applicationVersion.setStatus(ApplicationVersion.STATUS_UPLOADED);
			createRequistion(applicationVersion);
			applicationVersionDao.saveOrUpdate(applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isEditable(String username, ApplicationVersion applicationVersion) {
		return applicationManager.isEditable(username, applicationVersion.getApplication());
	}

	@Override
	public void archive(String[] verIdsArray, String reason) {
		try {
			for (int i = 0; i < verIdsArray.length; i++) {
				Boolean isAllArchived = true; // 是否全部归档
				Long id = Long.valueOf(verIdsArray[i]);
				ApplicationVersion appVersion = applicationVersionDao.load(id);
				List<CardBaseApplication> cbaList = cardBaseApplicationManager.findByApplicationVersion(appVersion);
				for (CardBaseApplication cba : cbaList) {
					if (cba.getPreset()) {
						List<CardInfo> checkList = cardInfoDao.findByProperty("cardBaseInfo", cba.getCardBase());
						if (checkList.size() > 0) {
							throw new PlatformException(PlatformErrorCode.APP_IS_PRESET);
						}
					}
				}
				//更改为归档不需要流程,RequistionManagerImpl.updatePublish代码	
				if (!appVersion.getStatus().equals(ApplicationVersion.STATUS_PULISHED)) {
					throw new PlatformException(PlatformErrorCode.APPLICATION_VERSION_NOT_PUBLISH);
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
						throw new PlatformException(PlatformErrorCode.INVALID_APP_STATUS);
					}
					appVersion.getApplication().setStatus(Application.STATUS_ARCHIVED);
					appVersion.getApplication().setArchivedDate(Calendar.getInstance());
				}
				this.saveOrUpdate(appVersion);
			//end
/*
 * int count = requistionManager.getCountByTypeAndStatusAndOrignaId(Requistion.TYPE_APP_ARCHIVE, Requistion.STATUS_INIT, id);
				if (count != 0) {
					throw new PlatformException(PlatformErrorCode.APP_IS_ARCHIVED);
				}
				Requistion req = new Requistion();
				req.setOriginalId(id);
				req.setType(Requistion.TYPE_APP_ARCHIVE);
				req.setStatus(Requistion.STATUS_INIT);
				req.setSubmitDate(Calendar.getInstance());
				req.setReason(reason);
				requistionManager.saveOrUpdate(req);*/
				
				/*
				 * ApplicationVersion av = applicationVersionDao.load(id);
				 * av.setStatus(ApplicationVersion.STATUS_ARCHIVE);
				 * av.setArchiveDate(Calendar.getInstance());
				 * applicationVersionDao.saveOrUpdate(av);
				 * 
				 * if (i == verIdsArray.length - 1) {//
				 * 如果是最后一个,并对所有版本判断,无已发布版本的时候,将应用归档 boolean flag = false;
				 * Application app = av.getApplication();
				 * List<ApplicationVersion> versions = app.getVersions(); for
				 * (ApplicationVersion aav : versions) { if (aav.getStatus() ==
				 * ApplicationVersion.STATUS_PULISHED) { flag = true; } } if
				 * (flag) { app.setArchivedDate(Calendar.getInstance());
				 * app.setStatus(Application.STATUS_ARCHIVED);
				 * applicationManager.saveOrUpdate(app); } }
				 */
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
	public ApplicationVersion getAidAndVersionNo(String aid, String versionNo) {
		return applicationVersionDao.getAidAndVersionNo(aid, versionNo);
	}

	@Override
	public ApplicationVersion getLastestAppVersionSupportCard(CardInfo card, Application app) {
		List<CardBaseApplication> list = cardBaseApplicationManager.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(
				card.getCardBaseInfo(), app);
		if (0 == list.size()) {
			return null;
		} else {
			return list.get(0).getApplicationVersion();
		}
	}

	@Override
	public ApplicationVersion getLastestAppVersionSupportCard(CardInfo card, Application app, String mobileNo) {
		List<CardBaseApplication> list = cardBaseApplicationManager.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(
				card.getCardBaseInfo(), app);
		if (0 == list.size()) {
			return null;
		} else {
			for (CardBaseApplication cardBaseApplication : list) {
				ApplicationVersion applicationVersion = cardBaseApplication.getApplicationVersion();
				if (applicationVersion.isMobileNoLimite(mobileNo)) {
					return applicationVersion;
				}
			}
			return null;
		}
	}

	@Override
	public ApplicationVersion getByAidAndVersionNo(String aid, String versionNo) {
		return applicationVersionDao.getAidAndVersionNo(aid, versionNo);
	}

	@Override
	public void finishAppVersion(String versionIds) {
		try {
			String[] verIdsArray = versionIds.split(",");
			for (int i = 0; i < verIdsArray.length; i++) {
				Long id = Long.valueOf(verIdsArray[0]);
				ApplicationVersion av = applicationVersionDao.load(id);
				List<CardBaseApplication> cbaList = cardBaseApplicationDao.findByProperty("applicationVersion", av);
				if (cbaList.size() < 1) {
					throw new PlatformException(PlatformErrorCode.MUST_lINK_FOR_DEFINE);
				}
				av.setStatus(ApplicationVersion.STATUS_UPLOADED);
				applicationVersionDao.saveOrUpdate(av);
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
	public Page<ApplicationVersion> findUnLinkPage(Page<ApplicationVersion> page, String cardBaseId) {
		try {
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardBaseId));
			String hql = "from ApplicationVersion as av where av.status = ? and av not in (select cba.applicationVersion from CardBaseApplication as cba where cba.cardBase = ?)";
			return applicationVersionDao.findPage(page, hql, ApplicationVersion.STATUS_PULISHED, cbi);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void createVersion(ApplicationVersion appVer) {
		try {
			applicationVersionDao.saveOrUpdate(appVer);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(ApplicationVersion applicationVersion, String username) {
		try {
			if (!isEditable(username, applicationVersion)) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_SP_DISCARD);
			}

			if (ApplicationVersion.STATUS_INIT.intValue() != applicationVersion.getStatus().intValue()) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_VERSION_NOT_INIT);
			}

			applicationVersionDao.remove(applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isSupportByCard(CardInfo card, ApplicationVersion applicationVersion) {
		try {
			boolean isSupport = false;

			List<CardBaseApplication> cardBaseApplications = cardBaseApplicationManager
					.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(card.getCardBaseInfo(),
							applicationVersion.getApplication());

			for (CardBaseApplication cardBaseApplication : cardBaseApplications) {
				if (applicationVersion.equals(cardBaseApplication.getApplicationVersion())) {
					isSupport = true;
				}
			}

			return isSupport;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public long hasArchiveRequest(Long appVerId) {
		return applicationVersionDao.hasArchiveRequest(appVerId);
	}

	@Override
	public void finishTest(Long appverId) {
		try {
			ApplicationVersion av = applicationVersionDao.load(appverId);
			checkTestReportExist(av);
			av.setStatus(ApplicationVersion.STATUS_TESTED);
			applicationVersionDao.saveOrUpdate(av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	private void createRequistion(ApplicationVersion av) {
		int count = requistionManager.getCountByTypeAndStatusAndOrignaId(Requistion.TYPE_APP_UPLOAD, Requistion.STATUS_INIT, av.getId());
		if (count == 0) {
			Requistion req = new Requistion();
			req.setType(Requistion.TYPE_APP_UPLOAD);
			req.setOriginalId(av.getId());
			req.setReason(Requistion.REASON_DEFAULT_APP_UPlOAD_APPLY);
			req.setSubmitDate(Calendar.getInstance());
			req.setStatus(Requistion.STATUS_INIT);
			requistionManager.saveOrUpdate(req);
		}
	}

	@Override
	public Page<ApplicationVersion> findPageBySp(Page<ApplicationVersion> page, Map<String, Object> queryParams) throws PlatformException {
		try {

			page = this.applicationVersionDao.findPageByMultParams(page, queryParams);

			List<ApplicationVersion> list = page.getResult();
			if (!list.isEmpty()) {
				List<ApplicationVersion> newList = new ArrayList<ApplicationVersion>(list.size());
				Page<CustomerCardInfo> p = new Page<CustomerCardInfo>(Integer.MAX_VALUE);
				for (ApplicationVersion e : list) {

					int downloadUserAmount = customerCardInfoManager.getCustomerCardInfoPageByApp(p, e).getTotalCount();
					if (downloadUserAmount == -1)
						downloadUserAmount = 0;
					e.setDownloadUserAmount(downloadUserAmount);

					int undownloadUserAmount = 0;
					if (e.getApplication().getSd().isSpaceExtendable()) {
						// card_info
						undownloadUserAmount = applicationVersionDao.getUndownloadUserAmountByApplicationVersionWithCardInfo(e);
					} else {
						// card_security_domain
						undownloadUserAmount = applicationVersionDao.getUndownloadUserAmountByApplicationVersionWithCardSecurityDomain(e);
					}
					e.setUndownloadUserAmount(undownloadUserAmount);

					newList.add(e);
				}

				page.setResult(newList);
			}

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return page;
	}

	private void checkTestFileExist(ApplicationVersion av) {
		List<TestFile> tfList = testFileManager.findByAppver(av);
		if (CollectionUtils.isEmpty(tfList)) {
			throw new PlatformException(PlatformErrorCode.NO_CONTACT_TEST_FILE);
		}
	}
	
	private void checkTestReportExist(ApplicationVersion av) {
		List<ApplicationVersionTestReport> avfList = applicationVersionTestReportManager.findByAppver(av);
		if (CollectionUtils.isEmpty(avfList)) {
			throw new PlatformException(PlatformErrorCode.NO_CONTACT_TEST_REPORT);
		}
	}
	
	@Override
	public void finishTest(ApplicationVersionTestReport testReport, Long appverId, String subType) {
		try {
			ApplicationVersion av = applicationVersionDao.load(appverId);
			if (subType.equalsIgnoreCase("OFFL")) {
				checkTestFileExist(av);
			}
			av.setStatus(ApplicationVersion.STATUS_TESTED);
			applicationVersionDao.saveOrUpdate(av);
			testReport.setAppVer(av);
			applicationVersionTestReportManager.saveOrUpdate(testReport);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<ApplicationVersion> getDownTestFileAppver(Page<ApplicationVersion> page, String appName) {
		try {
			return applicationVersionDao.getDownTestFileAppver(page, appName);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(ApplicationVersion entity) {
		Set<ApplicationLoadFile> applicationLoadFiles = new HashSet<ApplicationLoadFile>();

		for (ApplicationLoadFile applicationLoadFile : entity.getApplicationLoadFiles()) {
			applicationLoadFiles.add(applicationLoadFile);
		}

		// 删除应用时清理所有应用版本对加载文件版本的引入关系
		for (ApplicationLoadFile applicationLoadFile : applicationLoadFiles) {
			applicationLoadFileManager.remove(applicationLoadFile);
		}

		entity.getApplication().removeVersion(entity);
		super.remove(entity);
	}

	@Override
	public void saveReport(ApplicationVersionTestReport testReport, Long appverId, Long cardBaseId) {
		try {
			ApplicationVersion av = applicationVersionDao.load(appverId);
			CardBaseInfo cbi = cardBaseInfoManager.load(cardBaseId);
			checkExist(av,cbi);
			testReport.setCardBaseInfo(cbi);
			testReport.setAppVer(av);
			applicationVersionTestReportManager.saveOrUpdate(testReport);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void checkExist(ApplicationVersion av, CardBaseInfo cbi) {
		List<ApplicationVersionTestReport> resultList = applicationVersionTestReportManager.findByAppVerAndCardBase(av,cbi);
		if(resultList.size() > 0){
			throw new PlatformException(PlatformErrorCode.CARD_BASE_IS_REPORT);
		}
	}

	@Override
	public List<ApplicationVersion> getByAppIdWithPublish(Application app) {
		try {
			return applicationVersionDao.getByAppIdWithPublish(app);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
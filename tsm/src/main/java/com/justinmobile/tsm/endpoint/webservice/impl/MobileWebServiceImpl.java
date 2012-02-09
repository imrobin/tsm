package com.justinmobile.tsm.endpoint.webservice.impl;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.core.utils.encode.EncodeUtils;
import com.justinmobile.core.utils.hibernate.OpenSession;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationComment;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SpecialMobile;
import com.justinmobile.tsm.application.manager.ApplicationClientInfoManager;
import com.justinmobile.tsm.application.manager.ApplicationCommentManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.RecommendApplicationManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.card.manager.CardClientManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.cms2ac.engine.TransactionHelper;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.endpoint.webservice.CommandID;
import com.justinmobile.tsm.endpoint.webservice.ListOrder;
import com.justinmobile.tsm.endpoint.webservice.MobileWebService;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppCommentList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppInfoList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppOperate;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.BasicResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ClientInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ClientInfoList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.GetInformation;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoginOrRegisterRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.MemoryInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.PageRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqAppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqGetApplicationInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqSdList;

import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResListComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResLoginOrRegister;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResSdList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.SDInfoList;
import com.justinmobile.tsm.endpoint.webservice.log.LogMethod;
import com.justinmobile.tsm.endpoint.webservice.log.LogMethod.Type;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@WebService(serviceName = "MobileWebService", targetNamespace = NameSpace.CM, portName = "MobileWebServiceHttpPort")
@Service("mobileService")
public class MobileWebServiceImpl implements MobileWebService {
	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SysUserManager userManager;
	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private ApplicationManager applicationManager;
	@Autowired
	private RecommendApplicationManager recommendApplicationManager;

	@Autowired
	private ApplicationCommentManager commentManager;

	@Autowired
	private LocalTransactionManager localTransactionManager;

	@Autowired
	private TransactionHelper transactionHelper;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	private ApplicationClientInfoManager applicationClientInfoManager;

	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Autowired
	private CardInfoManager cardInfoManager;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Autowired
	private CardClientManager cardClientManager;

	@Autowired
	private ApplicationVersionManager appVerManager;

	@Override
	@OpenSession
	public ResApplicationList listApplication(ReqApplicationList req) {
		ResApplicationList res = new ResApplicationList();
		Status status = Status.getClientStauts();
		res.setStatus(status);
		res.setSessionID(req.getSessionID());
		res.setCommandID(req.getCommandID());
		Integer isDownloaded = req.getIsDownloaded();
		try {
			if (req.getQueryCondition() != null
					&& req.getQueryCondition().equals("commendApp")) {
				listRecommendApp(req, res);
				// isDownload=0是未下载，1是已下载，2是所有应用
			} else if (isDownloaded.intValue() == 2) {
				listApps(req, res);
			} else {
				if (isDownloaded.intValue() == 0
						|| (req.getQueryCondition() != null && req
								.getQueryCondition().equals("topDownload"))) {
					listNotCardApps(req, res);
				} else {
					listCardApps(req, res);
				}
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}

		return res;
	}

	private void listCardApps(ReqApplicationList req, ResApplicationList res) {
		// 获取分页参数
		Page<CardApplication> page = buildPage(req);
		// 是否有用有更新
		CustomerCardInfo cci = customerCardInfoManager.getByCardNo(req
				.getCardNo());
		CardInfo cardInfo = cci.getCard();
		List<CardBaseApplication> cardBaseApplications = cardInfo
				.getCardBaseInfo().getCardBaseApplications();
		// 获取过滤条件
		List<PropertyFilter> filters = buildFilter(req);
		// 加入卡号
		filters.add(new PropertyFilter("ALIAS_cardInfoI_EQS_cardNo", req
				.getCardNo()));
		filters.add(new PropertyFilter("INI_status", new Integer[] {
				CardApplication.STATUS_AVAILABLE,
				CardApplication.STATUS_PERSONALIZED,
				CardApplication.STATUS_LOCKED }));

		page = cardApplicationManager.findPage(page, filters);

		Map<CardApplication, Integer> apps = Maps.newLinkedHashMap();

		AppInfoList appInfoList = new AppInfoList();
		// 将得到的结果转换成应用列表
		List<CardApplication> result = page.getResult();
		if (CollectionUtils.isNotEmpty(result)) {
			for (CardApplication cardApplication : result) {
				// String cardVersion =
				// cardApplication.getApplicationVersion().getVersionNo();
				Application application = cardApplication
						.getApplicationVersion().getApplication();
				// String appVersion = application.getLastestVersion();
				// 是否有用有更新
				List<ApplicationVersion> versions = application.getVersions();
				int isUpdate = 0x00;
				// 在application.version中存在更高版本，status=3，且card_base_application有该终端对应批次的记录。就显示
				for (ApplicationVersion av : versions) {
					if (av.getStatus().intValue() == ApplicationVersion.STATUS_PULISHED
							&& SpringMVCUtils.compareVersion(av.getVersionNo(),
									cardApplication.getApplicationVersion()
											.getVersionNo())) {
						for (int i = 0; i < cardBaseApplications.size(); i++) {
							CardBaseApplication cba = (CardBaseApplication) cardBaseApplications
									.get(i);
							if (av.getId() == cba.getApplicationVersion()
									.getId()) {
								Set<SpecialMobile> speicalMobiles = av
										.getSpeicalMobiles();
								if (speicalMobiles.size() != 0) {// 不为空表示当前版本有特定手机限制
									for (SpecialMobile sm : speicalMobiles) { // 如果在特定手机里，可以更新
										if (sm.getMobileNo().equals(
												cci.getMobileNo())) {
											isUpdate = 0x01;
											break;
										}
									}
								} else {
									isUpdate = 0x01;
									break;
								}
							}
						}
					}
				}
				apps.put(cardApplication, isUpdate);
			}
		}
		String sysType = StringUtils.substringBefore(
				StringUtils.substringAfter(req.getCommonType(), "-"), "-");
		if (MapUtils.isNotEmpty(apps)) {
			for (Map.Entry<CardApplication, Integer> entry : apps.entrySet()) {
				AppInfo info = new AppInfo();
				info.buildSimple(entry.getKey().getApplicationVersion(),
						sysType, entry.getValue());
				int status = entry.getKey().getStatus();
				if (status == CardApplication.STATUS_LOCKED) {
					status = 2;
				} else {
					status = 1;
				}
				info.setAppStatus(status);
				// this.setClientId(info.getAppAid(), info, req.getCardNo());
				appInfoList.add(info);
			}
		}
		sortList(req, appInfoList.getAppInfo());
		// 设置返回结果
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(page.getTotalPages());
		res.setAppInfoList(appInfoList);
	}

	private void listNotCardApps(ReqApplicationList req, ResApplicationList res) {
		// 获取分页参数
		Page<Application> page = buildPage(req);
		Map<String, ?> filters = null;

		if (req.getQueryCondition() != null
				&& req.getQueryCondition().equals("topDownload")) {
			page.setOrderBy("downloadCount");
			page.setOrder("desc");
			filters = new HashMap<String, Object>();
			page.setPageSize(5);
		} else {
			filters = buildMapFilter(req);
		}
		page = applicationManager.getDownloadableApps(page, req.getCardNo(),
				filters);
		AppInfoList appInfoList = new AppInfoList();
		// 将得到的结果转换成dto
		String sysType = StringUtils.substringBefore(
				StringUtils.substringAfter(req.getCommonType(), "-"), "-");
		appInfoList.addAll(page.getResult(), sysType, null);
		List<AppInfo> appInfos = appInfoList.getAppInfo();
		// for (AppInfo appinfo : appInfos) {
		// this.setClientId(appinfo.getAppAid(), appinfo, req.getCardNo());
		// }
		int nextPage = page.getNextPage();
		if (req.getQueryCondition() != null
				&& req.getQueryCondition().equals("topDownload")) {
			nextPage = 0;
		} else {
			sortList(req, appInfos);
			// 设置返回结果
			if (!page.isHasNext()) {
				nextPage = 0;
			}
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(page.getTotalPages());
		res.setAppInfoList(appInfoList);
	}

	private void listRecommendApp(ReqApplicationList req, ResApplicationList res) {
		// 获取分页参数
		Page<RecommendApplication> page = buildPage(req);

		page = recommendApplicationManager.recommendAppListForMobile(page,
				req.getCardNo());
		AppInfoList appInfoList = new AppInfoList();
		// 将得到的结果转换成dto
		String sysType = StringUtils.substringBefore(
				StringUtils.substringAfter(req.getCommonType(), "-"), "-");
		List<Application> appList = new ArrayList<Application>();
		for (RecommendApplication ra : page.getResult()) {
			appList.add(ra.getApplication());
		}
		appInfoList.addAll(appList, sysType, null);
		List<AppInfo> appInfos = appInfoList.getAppInfo();
		// for (AppInfo appinfo : appInfos) {
		// this.setClientId(appinfo.getAppAid(), appinfo, req.getCardNo());
		// }
		sortList(req, appInfos);
		// 设置返回结果
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(page.getTotalPages());
		res.setAppInfoList(appInfoList);
	}

	@SuppressWarnings("unchecked")
	private void sortList(ReqApplicationList req, List<AppInfo> appInfos) {
		ListOrder order = ListOrder.typeOf(req.getListOrder());
		if (!order.equals(ListOrder.noOrder)) {
			String orderName = StringUtils.substringBefore(order.name(), "_");
			String sort = StringUtils.substringAfter(order.name(), "_");
			try {
				if (order.equals(ListOrder.appName_asc)
						|| order.equals(ListOrder.appName_desc)) {
					// 中文排序
					Collections.sort(appInfos, new BeanComparator(orderName,
							Collator.getInstance(Locale.CHINA)));
				} else {
					Collections.sort(appInfos, new BeanComparator(orderName));
				}
				if ("desc".equals(sort)) {
					Collections.reverse(appInfos);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 排序失败
			}
		}
	}

	private void listApps(ReqApplicationList req, ResApplicationList res) {
		// 获取分页参数
		Page<Application> page = buildPage(req);
		// 获取过滤条件
		List<PropertyFilter> filters = buildFilter(req);
		// 应用的状态为已发布状态
		filters.add(new PropertyFilter("EQI_status", String
				.valueOf(Application.STATUS_PUBLISHED)));
		page = applicationManager.findPage(page, filters);
		AppInfoList appInfoList = new AppInfoList();
		// 将得到的结果转换成dto
		String sysType = StringUtils.substringBefore(
				StringUtils.substringAfter(req.getCommonType(), "-"), "-");
		if (null != req.getQueryCondition()
				&& req.getQueryCondition().startsWith("EQS_aid=")) {
			appInfoList.addAllFullInfo(page.getResult(), sysType, null);
		} else {
			appInfoList.addAll(page.getResult(), sysType, null);
		}
		// 设置返回结果
		List<AppInfo> appInfos = appInfoList.getAppInfo();
		for (AppInfo appinfo : appInfos) {
			this.setClientId(appinfo.getAppAid(), appinfo, req.getCardNo());
		}
		sortList(req, appInfos);
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(page.getTotalPages());
		res.setAppInfoList(appInfoList);
	}

	private List<PropertyFilter> buildFilter(PageRequest req) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		String queryCondition = req.getQueryCondition();
		if (StringUtils.isNotBlank(queryCondition)) {
			String[] querys = StringUtils.split(queryCondition, "&");
			for (String query : querys) {
				if (StringUtils.indexOf(query, "=") == -1) {
					throw new PlatformException(
							PlatformErrorCode.PAGE_FILTER_PARAM_ERROR);
				}
				String condition = StringUtils.substringBefore(query, "=");
				String value = StringUtils.substringAfter(query, "=");
				filters.add(new PropertyFilter(condition, value));
			}
		}
		return filters;
	}

	private Map<String, String> buildMapFilter(PageRequest req) {
		Map<String, String> filters = Maps.newHashMap();
		String queryCondition = req.getQueryCondition();
		if (StringUtils.isNotBlank(queryCondition)) {
			String[] querys = null;
			if (queryCondition.startsWith("like=")) {
				String name = StringUtils.substringAfter(queryCondition,
						"like=");
				querys = new String[] { "LIKES_name"
						+ PropertyFilter.OR_SEPARATOR + "LIKES_sp.name"
						+ PropertyFilter.OR_SEPARATOR + "LIKES_description="
						+ name };// StringUtils.split(queryCondition,
									// "&");
			} else if (queryCondition.startsWith("classify=")) {
				String classify = StringUtils.substringAfter(queryCondition,
						"classify=");
				querys = new String[] { "EQI_childType.classify"
						+ PropertyFilter.OR_SEPARATOR
						+ "EQI_childType.parentType.classify=" + classify };
			}
			for (String query : querys) {
				if (StringUtils.indexOf(query, "=") == -1) {
					throw new PlatformException(
							PlatformErrorCode.PAGE_FILTER_PARAM_ERROR);
				}
				String condition = StringUtils.substringBefore(query, "=");
				String value = StringUtils.substringAfter(query, "=");
				filters.put(condition, value);
			}
		}
		return filters;
	}

	private <X> Page<X> buildPage(PageRequest req) {
		Integer pageSize = req.getPageSize();
		if (pageSize == null || pageSize == 0) {
			pageSize = 10;
		}
		Integer pageNumber = req.getPageNumber();
		if (pageNumber == null || pageNumber == 0) {
			pageNumber = 1;
		}
		Page<X> page = new Page<X>(pageSize);
		page.setPageNo(pageNumber);
		return page;
	}

	@Override
	@OpenSession
	public GetInformation getInfo(ReqGetApplicationInfo reqGetApplicationInfo) {
		GetInformation info = new GetInformation();
		Status status = Status.getClientStauts();
		info.setStatus(status);
		info.setCommandID(reqGetApplicationInfo.getCommandID());
		info.setSessionID(reqGetApplicationInfo.getSessionID());
		String cardNo = reqGetApplicationInfo.getCardNo();
		String appAID = reqGetApplicationInfo.getAppAID();
		try {
			CommandID commandId = CommandID.AppInfo;// 默认获得应用详情
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			try {
				commandId = CommandID.codeOf(reqGetApplicationInfo
						.getCommandID());
			} catch (IllegalArgumentException e) {
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
			}
			switch (commandId) {
			case AppInfo:
				AppInfo appInfo = new AppInfo();
				if (StringUtils.isNotBlank(appAID)) {
					Application app = null;
					Integer isUpdate = 0x00;// 默认不更新
					CardApplication cardApp = cardApplicationManager
							.getByCardNoAid(reqGetApplicationInfo.getCardNo(),
									appAID);
					if (cardApp == null
							|| !CardApplication.STATUS_SHOWABLE
									.contains(cardApp.getStatus())) {
						app = applicationManager.getByAid(appAID);

					} else {
						app = cardApp.getApplicationVersion().getApplication();
						String cardVersion = cardApp.getApplicationVersion()
								.getVersionNo();
						String appVersion = app.getLastestVersion();
						if (SpringMVCUtils.compareVersion(appVersion,
								cardVersion)) {
							isUpdate = 0x01;
						} else {
							isUpdate = 0x00;
						}
					}
					String sysType = StringUtils
							.substringBefore(
									StringUtils.substringAfter(
											reqGetApplicationInfo
													.getCommonType(), "-"), "-");
					appInfo.build(app, sysType, isUpdate);
					info.setAppInfo(appInfo);
				}
				break;
			case CommentView:
				ResListComment comments = listComment(reqGetApplicationInfo);
				info.setAppCommentList(comments.getAppCommentList());
				info.setNextPageNumber(comments.getNextPageNumber());
				info.setTotalPages(comments.getTotalPage());
				break;
			case CardSpaceInfo:

				if (card == null) {
					throw new PlatformException(
							PlatformErrorCode.CARD_NOT_FOUND);
				}
				MemoryInfo memoryInfo = new MemoryInfo();
				memoryInfo.setNonVolatileMemory(card
						.getAvailableNonevolatileSpace());
				memoryInfo.setVolatileMemory(card.getAvailableVolatileSpace());
				memoryInfo.setTotalMemory(card.getAvailableNonevolatileSpace()
						+ card.getAvailableVolatileSpace());
				info.setMemoryInfo(memoryInfo);
				break;
			case AppClientInfo:
				if (card == null) {
					throw new PlatformException(
							PlatformErrorCode.CARD_NOT_FOUND);
				}
				List<ApplicationClientInfo> clients = applicationClientInfoManager
						.getClientByCard(card);
				ClientInfoList clist = new ClientInfoList();
				clist.addAll(clients, appAID);
				info.setClientInfoList(clist);
				break;
			default:
				break;
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		return info;
	}

	@Override
	@LogMethod(Type.APDU)
	@OpenSession
	public ResExecAPDU execAPDU(ReqExecAPDU reqExecAPDU) {

		ResExecAPDU apdu = new ResExecAPDU();
		String sessionId = reqExecAPDU.getSessionID();
		Status status = Status.getClientStauts();
		apdu.setStatus(status);
		try {

			String cardNo = reqExecAPDU.getCardNo();
			String commonType = reqExecAPDU.getCommonType();
			// 取第一个操作
			if (StringUtils.isBlank(sessionId)) {
				List<AppOperate> appAids = reqExecAPDU.getAppList()
						.getAppOperate();
				// 第一次请求时，检查卡片和用户是否存在，是否登录等
				if (!SystemConfigUtils.isTestRuntimeEnvironment()) {
					if (!StringUtils.startsWith(commonType, "ME")
							&& !StringUtils.startsWith(commonType, "APC")) {// 手机不需要登录，所以不验证
						checkCustomer(reqExecAPDU);
					}
				}
				String mobileNo = reqExecAPDU.getMobileNo();
				if (StringUtils.isNotBlank(mobileNo)) {
					CardInfo card = cardInfoManager.getByCardNo(cardNo);
					card.setMobileNo(mobileNo);
				}
				sessionId = transactionHelper.createSession(appAids, cardNo,
						commonType, SpringSecurityUtils.getCurrentUserName());
				LocalTransaction localTransaction = localTransactionManager
						.getBySessionId(sessionId);
				localTransaction.setBeginTime(Calendar.getInstance());
			}

			// 开始处理流程，结果保存在参数apdu中
			execAPDU(reqExecAPDU, apdu, sessionId);

			Task task = localTransactionManager.getBySessionId(sessionId)
					.getTask();
			taskManager.saveOrUpdate(task);
		} catch (PlatformException e) {
			e.printStackTrace();
			String errorCode = e.getErrorCode().getErrorCode();
			status.setStatusCode(errorCode);
			status.setStatusDescription(e.getMessage());
			if (StringUtils.isNotBlank(sessionId)) {// 如果有localTransaction，修改结果
				endFaildTrans(sessionId, e, errorCode);
			}
		}
		apdu.setSeqNum(null);
		return apdu;
	}

	private void execAPDU(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu,
			String sessionId) {
		Status status = Status.getClientStauts();
		apdu.setStatus(status);
		try {
			try {
				apdu = processTrans(reqExecAPDU, apdu, sessionId);
			} catch (PlatformException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
			}
		} catch (PlatformException e) {// 如果当前流程抛出异常，处理异常后执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）
			e.printStackTrace();
			String errorCode = e.getErrorCode().getErrorCode();
			status.setStatusCode(errorCode);
			status.setStatusDescription(e.getMessage());
			if (StringUtils.isNotBlank(sessionId)) {// 如果有localTransaction，修改结果
				LocalTransaction localTransaction = endFaildTrans(sessionId, e,
						errorCode);
				transactionHelper.completDesiredOperation(localTransaction);

				Task task = localTransaction.getTask();
				task.increaseFailTransCount();// 任务的失败流程数+1
				processNextTransactionOrEndTask(reqExecAPDU, apdu, task,
						MocamResult.getLastResult(localTransaction.getAid()),
						transactionHelper.getNextSeqNum());// 执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）
			}
		}
	}

	private LocalTransaction endFaildTrans(String sessionId,
			PlatformException e, String errorCode) {
		LocalTransaction localTransaction = localTransactionManager
				.getBySessionId(sessionId);
		localTransaction.setResult(errorCode);// 设置状态码
		localTransaction.setFailMessage(StringUtils.isBlank(e.getMessage()) ? e
				.getErrorCode().getDefaultMessage() : e.getMessage());
		localTransaction.setExecutionStatusAsCompeleted();// 将当前流程及其子流程的执行状态设为“已执行”
		localTransaction.setEndTime(Calendar.getInstance());
		localTransactionManager.saveOrUpdate(localTransaction);
		return localTransaction;
	}

	private void checkCustomer(ReqExecAPDU reqExecAPDU) {
		String userName = SpringSecurityUtils.getCurrentUserName();
		if (StringUtils.isBlank(userName)) {
			throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		}
		SysUser user = userManager.getUserByName(userName);
		if (user == null) {
			throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		}
		String roleName = user.getSysRole().getRoleName();
		if (SpecialRoleType.CUSTOMER.name().equals(roleName)) {
			Customer c = customerManager.getCustomerByUserName(userName);
			if (c == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			List<CustomerCardInfo> customerCardInfos = c.getCustomerCardInfos();
			if (CollectionUtils.isEmpty(customerCardInfos)) {
				throw new PlatformException(
						PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			}
			boolean hasCard = false;
			for (CustomerCardInfo customerCardInfo : customerCardInfos) {
				String cCardNo = customerCardInfo.getCard().getCardNo();
				if (cCardNo.equals(reqExecAPDU.getCardNo())) {
					hasCard = true;
				}
			}
			if (!hasCard) {
				throw new PlatformException(
						PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			}
		}
	}

	private ResExecAPDU processTrans(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu,
			String sessionId) {
		MocamProcessor processor = null;
		MocamResult result = new MocamResult();

		LocalTransaction localTransaction = localTransactionManager
				.getBySessionId(sessionId);
		processor = transactionHelper.routeProcessor(localTransaction
				.getProcedureName());
		result = processor.process(localTransaction, reqExecAPDU);
		if (localTransaction.isComplete()) {// 当前流程已经完成
			localTransaction.setEndTime(Calendar.getInstance());

			transactionHelper.completDesiredOperation(localTransaction);

			Task task = localTransaction.getTask();
			if (PlatformMessage.SUCCESS.getCode().equals(
					localTransaction.getResult())) {
				task.increaseSuccTransCount();
			} else {
				task.increaseFailTransCount();
			}
			apdu = processNextTransactionOrEndTask(reqExecAPDU, apdu, task,
					result, transactionHelper.getNextSeqNum());// 执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）
		} else {// 当前流程未完成，设置当前流程的执行结果
			buildResApdu(reqExecAPDU, apdu, localTransaction,
					transactionHelper.getNextSeqNum(), result);
		}
		return apdu;
	}

	private void buildResApdu(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu,
			LocalTransaction localTransaction, String nextSeqNum,
			MocamResult result) {
		apdu.addApdus(result.getApdus());

		Operation operation = Operation.valueOf(localTransaction
				.getProcedureName());
		if (StringUtils.isNotBlank(operation.getCommandId())) {
			apdu.setCommandID(operation.getCommandId());
		} else {
			apdu.setCommandID(new Integer(operation.getType()).toString());
		}
		apdu.setCurrentAppAid(localTransaction.getAid());
		apdu.setProgress(result.getProgress());
		apdu.setProgressPercent(result.getProgressPercent());
		apdu.setSessionID(localTransaction.getLocalSessionId());
		apdu.setTimeStamp(CalendarUtils.getFormatNow());
		ApduName apduName = result.getApduName();
		if (apduName != null) {
			apdu.setApduName(apduName.name());
		} else {
			apdu.setApduName(ApduName.Complete.name());
		}

		if (SessionStatus.TERMINATE == localTransaction.getSessionStatus()
				.intValue()) {
			Status status = apdu.getStatus();
			status.setStatusCode(localTransaction.getResult());
			status.setStatusDescription(localTransaction.getFailMessage());
		}
	}

	private ResExecAPDU processNextTransactionOrEndTask(
			ReqExecAPDU reqExecAPDU, ResExecAPDU apdu, Task task,
			MocamResult result, String nextSeqNum) {
		LocalTransaction localTransaction = task.getCurrentTransaction();
		task.increaseCurrentTransIndex();// 任务的当前流程索引+1
		if (task.hasTrancationToExecut()) {// 如果任务还有流程需要执行，执行流程
			LocalTransaction currentTransaction = task.getCurrentTransaction();
			currentTransaction.setBeginTime(Calendar.getInstance());
			String sessionId = currentTransaction.getLocalSessionId();
			execAPDU(reqExecAPDU, apdu, sessionId);
		} else {// 否则结束任务
			task.setFinished(Boolean.TRUE);
			task.setEndTime(Calendar.getInstance());
			buildResApdu(reqExecAPDU, apdu, localTransaction, nextSeqNum,
					result);
		}

		return apdu;
	}

	@Override
	@OpenSession
	public BasicResponse postAppComment(ReqAppComment reqAppComment) {
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCommandID(reqAppComment.getCommandID());
		Status status = Status.getClientStauts();
		basicResponse.setStatus(status);
		try {
			CommandID commandId = CommandID.CommentPost;// 默认为提交评论
			try {
				commandId = CommandID.codeOf(reqAppComment.getCommandID());
			} catch (IllegalArgumentException e) {
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
			}
			switch(commandId){
			case CommentPost:{
			ApplicationComment ac = new ApplicationComment();
			String appAID = reqAppComment.getComment().getAppAID();
			String cardNo = reqAppComment.getCardNo();
			CustomerCardInfo customerCardInfo = customerCardInfoManager
					.getByCardNo(cardNo);
			Integer starGrade = reqAppComment.getComment().getStarGrade();
			if (StringUtils.isBlank(appAID)) {
				throw new PlatformException(
						PlatformErrorCode.APPLICAION_AID_NOT_EXIST);
			}
			if (starGrade == null) {
				starGrade = 0;
			}
			Application app = applicationManager.getByAid(appAID);
			Customer customer = customerCardInfo.getCustomer();
			if (commentManager.getByAppIdAndCustomerId(app.getId(),
					customer.getId()) != null) {
				throw new PlatformException(
						PlatformErrorCode.APPLICATION_COMMENT_REPEAT);
			}
			ac.setApplication(app);
			ac.setCustomer(customer);
			ac.setCommentTime(Calendar.getInstance());
			ac.setContent(EncodeUtils.urlDecode(reqAppComment.getComment()
					.getCommentContent()));
			ac.setGrade(starGrade);
			commentManager.saveOrUpdate(ac);
			break;
			}
			case CommentUpDown:{
				long id = reqAppComment.getComment().getCommentId();
				if(reqAppComment.getComment().getDown()!=null){
					commentManager.downComment(id);
				}
				if(reqAppComment.getComment().getUp()!=null){
					commentManager.upComment(id);
				}
				break;
			}
		}
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		return basicResponse;
	}

	private ResListComment listComment(
			ReqGetApplicationInfo reqGetApplicationInfo) {
		ResListComment comments = new ResListComment();
		Page<ApplicationComment> page = buildPage(reqGetApplicationInfo);
		// 加根据时间倒叙
		page.addOrder("commentTime", "desc");
		List<PropertyFilter> filters = buildFilter(reqGetApplicationInfo);
		filters.add(new PropertyFilter("ALIAS_applicationI_EQS_aid",
				reqGetApplicationInfo.getAppAID()));
		page = commentManager.findPage(page, filters);
		AppCommentList appCommentList = new AppCommentList();
		appCommentList.addAll(page.getResult());
		// 设置返回结果
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		comments.setNextPageNumber(nextPage);
		comments.setTotalPage(page.getTotalPages());
		comments.setAppCommentList(appCommentList);
		return comments;
	}

	@Override
	public LoadClientResponse loadClient(LoadClientRequest loadClientRequest) {
		LoadClientResponse response = new LoadClientResponse();
		Status status = Status.getClientStauts();
		response.setCommandID(loadClientRequest.getCommandID());
		response.setStatus(status);
		response.setSessionID(loadClientRequest.getSessionID());
		// 默认为不更新
		Integer isUpdatable = 0x00;
		try {
			String cardNo = loadClientRequest.getCardNo();
			String aid = loadClientRequest.getAppAId();
			String[] commType = loadClientRequest.getCommonType().split("-");
			String version = commType[2];
			// 目前测试为Android系统
			String sysType = loadClientRequest.getMobileOs();
			String sysRequirment = loadClientRequest.getOsVersion();
			// 出除多余的版本编号;如2.3.3-[Mr.Koo-N835]这种格式
			if (sysRequirment.length() > 3) {
				sysRequirment = sysRequirment.substring(0, 3);
			}
			String fileType = null;
			ApplicationClientInfo applicationClientInfo = null;
			if (ApplicationClientInfo.SYS_TYPE_Android
					.equalsIgnoreCase(sysType)) {
				fileType = ApplicationClientInfo.FILE_TYPE_APK;
				// 忽略掉小版本号
				// sysRequirment =
				// sysRequirment.substring(0,sysRequirment.indexOf(".")+1);
			} else if (ApplicationClientInfo.SYS_TYPE_J2ME
					.equalsIgnoreCase(sysType)) {
				fileType = ApplicationClientInfo.FILE_TYPE_JAD;
			} else {
				fileType = ApplicationClientInfo.FILE_TYPE_JAR;
			}
			if (loadClientRequest.getCommandID().equalsIgnoreCase(
					CommandID.AppClientUpgrade.getCode())) {
				CardApplication cardApplication = cardApplicationManager
						.getByCardNoAid(cardNo, aid);
				ApplicationVersion appVer = cardApplication
						.getApplicationVersion();
				applicationClientInfo = applicationClientInfoManager
						.getByApplicationVersionSysTypeSysRequirementFileType(
								appVer, "os", sysType + sysRequirment, fileType);
				if (applicationClientInfo == null) {
					throw new PlatformException(
							PlatformErrorCode.APPLICAION_CLIENT_NOT_EXSIT);
				}
				ClientInfo ci = new ClientInfo();
				ci.build(aid, applicationClientInfo, isUpdatable);
				response.setClientInfo(ci);
			} else if (loadClientRequest.getCommandID().equals(
					CommandID.UeUprage.getCode())) {
				applicationClientInfo = applicationClientInfoManager
						.getAppManagerByTypeAndReqAndVersion("os", sysType
								+ sysRequirment, version);
				if (applicationClientInfo == null) {
					throw new PlatformException(
							PlatformErrorCode.APPLICAION_CLIENT_NOT_EXSIT);
				}
				ClientInfo ci = new ClientInfo();
				ci.build(aid, applicationClientInfo, isUpdatable);
				response.setClientInfo(ci);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		return response;
	}

	@Override
	@OpenSession
	public ResLoginOrRegister loginOrRegiseter(LoginOrRegisterRequest request) {
		ResExecAPDU resAPDU = new ResExecAPDU();
		ResLoginOrRegister res = new ResLoginOrRegister();
		Status status = Status.getClientStauts();
		res.setStatus(status);
		String sessionId = null;
		try {
			CommandID commandId = CommandID.UserLogin;// 默认为注册
			String cardNo = request.getCardNo();
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			CustomerCardInfo customerCard = customerCardInfoManager
					.getByCardNo(cardNo);
			try {
				commandId = CommandID.codeOf(request.getCommandID());
			} catch (IllegalArgumentException e) {
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
			}
			switch (commandId) {
			case UserRegist: {
				// 注册时必须验证随机码
				if (!card.getChallengeNo().equals(request.getChallengeNo())) {// 如果随机数不匹配，抛出异常
					throw new PlatformException(
							PlatformErrorCode.MISMATCH_CHALLENGE_NO);
				}
				card.setRegisterable(CardInfo.REGISTERABLE_READY);

				if (null == customerCard) {// 如终端未绑定，开始注册新用户
					cardInfoManager.saveOrUpdate(card);
					processTrans(request, resAPDU, res, cardNo);
				} else {// 如果该终端已经绑定
					if (customerCard.getMobileNo().equals(card.getMobileNo())) {// 如果绑定手机号与上行短信手机号一致，发起修改IMSI操作
						request.setCommandID(CommandID.ChangeToken.getCode());
						processTrans(request, resAPDU, res, cardNo);
					} else {// 如果绑定手机号与上行短信手机号不一致，抛出异常
						throw new PlatformException(
								PlatformErrorCode.CARD_IS_EXIST);
					}
				}
				break;
			}
			case UserLogin: {
				String sysType = StringUtils.substringBefore(StringUtils
						.substringAfter(request.getCommonType(), "-"), "-");
				ClientInfoList clist = request.getClientInfoList();
				if (null == customerCard) {// 如果当前终端未绑定，则抛出异常
					throw new PlatformException(
							PlatformErrorCode.CARD_NO_UNEXIST);
				} else {
					processTrans(request, resAPDU, res, cardNo);
				}
				res.setMobileNo(customerCard.getMobileNo());
				res.setAppInfoList(upAppInfo(card, sysType));
				res.setClientInfoList(upClientInfo(card, sysType));
				break;
			}
			case ChangeToken: {

				// 客户端在发送CommandID.ChangeToken之前一定会发送上行短信，因此先验证上行短信
				PlatformMessage message;
				if (null == card
						|| !card.getChallengeNo().equals(
								request.getChallengeNo())) {
					message = PlatformMessage.MOBILE_MISMATCH_CHALLENGE_NO;
				} else {// 收到上行短信，根据绑定关系通知客户端下一步行为
					 if(null != customerCard){
				    	if (customerCard.isInBlackList()) {// 如果在黑名单
							message = PlatformMessage.MOBILE_IN_BLACK_LIST;
						}else if(card.getRegisterable().intValue() == CardInfo.REGISTERABLE_CHANGE_SIM.intValue())// 如果不再黑名单
						{// 如果绑定手机号与上行短信手机号一致，执行修改IMSI操作
						message = PlatformMessage.MOBILE_NOTIFY_IMSI;
					    } else if(card.getRegisterable().intValue() == CardInfo.REGISTERABLE_LOGIN.intValue()) //发起登陆
					    {
							message = PlatformMessage.MOBILE_LOGIN;
						}else{
							//提示已经绑定
							message = PlatformMessage.MOBILE_REGISTERED;
						}
					}else {// 如果绑定手机号与上行短信手机号不一致
						message = PlatformMessage.MOBILE_REGISTER;
				    }
				    }
				status.setStatusCode(message.getCode());
				status.setStatusDescription(message.getDefaultMessage());
				break;
			}
			case UserCancel: {
				boolean result = customerCardInfoManager
						.checkCancelTermCardApp(customerCard.getId());
				if (result) {
					throw new PlatformException(
							PlatformErrorCode.CARD_NOT_CANCEL_FOR_APP);
				} else {
					customerCardInfoManager.finashCancel(customerCard.getId());
				}
				break;
			}
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			String errorCode = e.getErrorCode().getErrorCode();
			status.setStatusCode(errorCode);
			status.setStatusDescription(e.getMessage());
			if (StringUtils.isNotBlank(sessionId)) {// 如果有localTransaction，修改结果
				endFaildTrans(sessionId, e, errorCode);
			}
		}
		res.setCommandID(request.getCommandID());
		if (request.getCommandID().equals(CommandID.ChangeToken.getCode())) {
			res.setUrl(SystemConfigUtils.getServiceUrl()
					+ "aboutus.jsp?v=termsOfServiceLink");
		} else if (request.getCommandID()
				.equals(CommandID.UserRegist.getCode())) {
			res.setMocamVersion(applicationClientInfoManager
					.getMocamMaxVersion());
		}
		return res;
	}

	private void processTrans(LoginOrRegisterRequest request,
			ResExecAPDU resAPDU, ResLoginOrRegister res, String cardNo) {
		String sessionId;
		List<AppOperate> appAids = new ArrayList<AppOperate>();
		AppOperate operate = new AppOperate();
		operate.setOperation(new Integer(CommandID.codeOf(
				request.getCommandID()).getCode()));
		appAids.add(operate);
		sessionId = transactionHelper.createSession(appAids, cardNo,
				request.getCommonType(),
				SpringSecurityUtils.getCurrentUserName());
		LocalTransaction localTransaction = localTransactionManager
				.getBySessionId(sessionId);
		localTransaction.setBeginTime(Calendar.getInstance());
		// 开始处理流程，结果保存在参数apdu中
		execAPDU(null, resAPDU, sessionId);
		BeanUtils.copyProperties(resAPDU, res);
		Task task = localTransactionManager.getBySessionId(sessionId).getTask();
		taskManager.saveOrUpdate(task);
	}

	@Override
	public ResSdList listSd(ReqSdList reqSdList) {
		ResSdList res = new ResSdList();
		Status status = Status.getClientStauts();
		res.setStatus(status);
		res.setCommandID(reqSdList.getCommandID());
		try {
			if (StringUtils.isBlank(reqSdList.getCardNo())) {
				throw new PlatformException(PlatformErrorCode.CARD_NOT_FOUND);
			} else {
				Boolean isInstall = reqSdList.getIsInstall();
				if (isInstall == null || !isInstall) {
					listUnCreatedCardSds(reqSdList, res);
				} else {
					listCardSds(reqSdList, res);
				}
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}

		return res;
	}

	private void listCardSds(ReqSdList req, ResSdList res) {
		// 获取分页参数
		Page<CardSecurityDomain> page = buildPage(req);
		// 获取过滤条件
		List<PropertyFilter> filters = buildFilter(req);
		// 应用的状态为已发布状态
		filters.add(new PropertyFilter("EQI_status", String
				.valueOf(CardSecurityDomain.STATUS_PERSO)));
		filters.add(new PropertyFilter("ALIAS_cardL_EQS_cardNo", req
				.getCardNo()));
		page = cardSecurityDomainManager.findPage(page, filters);
		SDInfoList sdInfoList = new SDInfoList();
		List<PropertyFilter> appFilters = new ArrayList<PropertyFilter>();
		appFilters.add(new PropertyFilter("EQI_status", String
				.valueOf(CardApplication.STATUS_AVAILABLE)));
		appFilters.add(new PropertyFilter("ALIAS_cardInfoL_EQS_cardNo", req
				.getCardNo()));
		List<CardApplication> cardApplications = cardApplicationManager
				.find(appFilters);
		// 将得到的结果转换成dto
		sdInfoList.addAll(page.getResult(), cardApplications);
		// 设置返回结果
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(page.getTotalPages());
		res.setSdInfoList(sdInfoList);
	}

	private void listUnCreatedCardSds(ReqSdList req, ResSdList res) {

		// 获取分页参数
		Page<CardSecurityDomain> page = buildPage(req);
		// 获取CardBaseInfo
		CardBaseInfo cbi = cardBaseInfoManager.getCardBaseInfoByCardNo(req
				.getCardNo());
		// 通过CardNo获取该CardBaseSecurityDomain
		List<CardBaseSecurityDomain> listCbsd = cardBaseSecurityDomainManager
				.getByCardBase(cbi);
		List<SecurityDomain> allSd = new ArrayList<SecurityDomain>();
		for (CardBaseSecurityDomain cbsd : listCbsd) {
			allSd.add(cbsd.getSecurityDomain());
		}
		// 获取CardSecurityDomain下的安全域
		List<SecurityDomain> createdSd = cardSecurityDomainManager
				.getSdByCardNo(req.getCardNo());
		SDInfoList sdInfoList = new SDInfoList();
		if (createdSd != null) {
			@SuppressWarnings("unchecked")
			List<SecurityDomain> unCreatedSd = (List<SecurityDomain>) CollectionUtils
					.subtract(allSd, createdSd);
			// 将得到的结果转换成dto
			sdInfoList.addAll(unCreatedSd);
		} else {
			sdInfoList.addAll(allSd);
		}
		// 设置返回结果
		int nextPage = page.getNextPage();
		if (!page.isHasNext()) {
			nextPage = 0;
		}
		res.setNextPageNumber(nextPage);
		res.setTotalPage(sdInfoList.getSdInfo().size() / page.getPageSize() + 1);
		res.setSdInfoList(sdInfoList);
	}

	private void setClientId(String aid, AppInfo info, String cardNo) {
		if (cardNo != null) {
			CustomerCardInfo cci = customerCardInfoManager.getByCardNo(cardNo);
			CardApplication ca = cardApplicationManager
					.getAvailbleOrLockedByCardNoAid(cci.getCard().getCardNo(),
							aid);
			if (ca == null) {

			} else {
				boolean hasSysRequirment = customerCardInfoManager
						.hasSysRequirment(cci, ca);
				if (!hasSysRequirment) {
					// mappedApplication.put("clientStatusStr",
					// PlatformErrorCode.NOT_DOWN_CLINET.getDefaultMessage());
				} else {
					MobileType mt = cci.getMobileType();
					Set<ApplicationClientInfo> acs = ca.getApplicationVersion()
							.getClients();
					ApplicationClientInfo androidTemp = null;
					for (Iterator<ApplicationClientInfo> it = acs.iterator(); it
							.hasNext();) {
						ApplicationClientInfo ac = (ApplicationClientInfo) it
								.next();
						if (mt.getOriginalOsKey().equals(ac.getSysRequirment())) {
							// 应用详情-下载客户端：当同一手机型号对应了多个版本的Android客户端时
							// ，应该下载当前手机型号对应的最高版本的客户端，以版本号来判断，而不是上传时间。
							if (androidTemp == null
									|| SpringMVCUtils.compareVersion(
											ac.getVersion(),
											androidTemp.getVersion())) {
								info.setClientID(String.valueOf(ac.getId()));
								androidTemp = ac;
							}
						}
					}
				}
			}
		}
	}

	private AppInfoList upAppInfo(CardInfo card, String sysType) {
		List<CardApplication> cardAppList = cardApplicationManager
				.getByCardAndStatus(card, CardApplication.STATUS_AVAILABLE);
		List<Application> updateAppList = new ArrayList<Application>();
		AppInfoList appList = new AppInfoList();
		Integer isUpdatable = 0x01;

		for (CardApplication cardApp : cardAppList) {
			ApplicationVersion installedVersion = cardApp
					.getApplicationVersion();
			ApplicationVersion lastedVersion = appVerManager
					.getLastestAppVersionSupportCard(card, cardApp
							.getApplicationVersion().getApplication());
			// 如果最新版本大于已安装版本则该应用需要更新
			if (SpringMVCUtils.compareVersion(lastedVersion.getVersionNo(),
					installedVersion.getVersionNo())) {
				updateAppList.add(lastedVersion.getApplication());
			}
		}
		appList.addAll(updateAppList, sysType, isUpdatable);
		return appList;
	}

	private ClientInfoList upClientInfo(CardInfo card, String sysType) {
		Integer isUpdatable = 0x01;
		;
		ClientInfoList ciList = new ClientInfoList();
		// 获取卡上安装的所有应用
		List<CardApplication> cardAppList = cardApplicationManager
				.getByCardAndStatus(card, CardApplication.STATUS_AVAILABLE);
		for (CardApplication ca : cardAppList) {
			Application app = ca.getApplicationVersion().getApplication();
			// 根据应用和卡片获取CardClient
			CardClient cc = cardClientManager
					.getByCardAndApplicationAndSysType(card, app, sysType);
			if (null != cc) {
				ApplicationClientInfo aci = cc.getClient();
				// 获取该卡上可以安装应用的最大版本
				List<ApplicationClientInfo> maxAciList = applicationClientInfoManager
						.getByAidAndCardNo(app.getAid(), card.getCardNo());
				inner: for (ApplicationClientInfo ac : maxAciList) {
					if (SpringMVCUtils.compareVersion(ac.getVersion(),
							aci.getVersion())) {
						ClientInfo ci = new ClientInfo();
						ci.build(app.getAid(), ac, isUpdatable);
						ciList.addClientInfo(ci);
						break inner;
					}
				}
			}
		}
		return ciList;
	}
}

package com.justinmobile.tsm.application.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationComment;
import com.justinmobile.tsm.application.domain.ApplicationImage;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.GradeStatistics;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.application.manager.ApplicationCommentManager;
import com.justinmobile.tsm.application.manager.ApplicationImageManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.RecommendApplicationManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;
import com.justinmobile.tsm.commons.web.CommonsController;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.history.domain.SubscribeHistory;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Controller("applicationController")
@RequestMapping("/application/")
public class ApplicationController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private ApplicationCommentManager applicationCommentManager;

	@Autowired
	private RecommendApplicationManager recommendApplicationManager;

	@Autowired
	private SubscribeHistoryManager subscribeHistoryManager;

	@Autowired
	private CommonsController commonsController;

	@Autowired
	private SysUserManager sysUserManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private CardInfoManager cardInfoManager;

	@Autowired
	private ApplicationImageManager applicationImageManager;

	@RequestMapping
	public @ResponseBody JsonResult select(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			List<Application> list = applicationManager.getAll();
			String excludeField = null;
			String includeCascadeField = null;
			Page<Application> page = SpringMVCUtils.getPage(request);
			page.setTotalCount(list.size());
			page.setPageSize(page.getTotalCount());
			page.setResult(list);
			result.setPage(page, excludeField, includeCascadeField);
			
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
			log.error(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
			log.error(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping
	public @ResponseBody
	JsonMessage createApplication(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			log.debug("\n" + "createApplication" + "\n");
			if (log.isDebugEnabled()) {
				@SuppressWarnings("unchecked")
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
				}

			}

			Application application = new Application();
			BindingResult result = SpringMVCUtils.bindObject(request, application);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				application.setPersoCmdTransferSecureAlgorithm(SecureAlgorithm.valueOf(Integer.parseInt(
						request.getParameter("persoCmdTransferSA"), 16)));
				application.setPersoCmdSensitiveDataSecureAlgorithm(SecureAlgorithm.valueOf(Integer.parseInt(
						request.getParameter("persoCmdSensitiveDataSA"), 16)));

				Map<String, String> params = new HashMap<String, String>();
				params.put("versionNo", request.getParameter("versionNo"));
				params.put("sdId", request.getParameter("sdId"));
				params.put("pcIconTempFileAbsPath", request.getParameter("pcIconTempFileAbsPath"));
				params.put("mobileIconTempFileAbsPath", request.getParameter("mobileIconTempFileAbsPath"));
				params.put("applicationImgTempFileAbsPath", request.getParameter("applicationImgTempFileAbsPath"));
				params.put("applicationTypeId", request.getParameter("applicationTypeId"));

				applicationManager.createNewApplication(username, application, params);

				Long applicationVersionId = application.getVersions().get(0).getId();
				if (log.isDebugEnabled()) {
					log.debug("\napplicationVersionId: " + applicationVersionId + "\n");
				}
				message.setMessage(applicationVersionId);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage modifyApplicationBaseInfo(HttpServletRequest request, @RequestParam Long applicationId) {
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			log.debug("\n" + "createApplication" + "\n");
			if (log.isDebugEnabled()) {
				@SuppressWarnings("unchecked")
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
				}

			}

			Application application = applicationManager.load(applicationId);
			BindingResult result = SpringMVCUtils.bindObject(request, application);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				application.setPersoCmdTransferSecureAlgorithm(SecureAlgorithm.valueOf(Integer.parseInt(
						request.getParameter("persoCmdTransferSA"), 16)));
				application.setPersoCmdSensitiveDataSecureAlgorithm(SecureAlgorithm.valueOf(Integer.parseInt(
						request.getParameter("persoCmdSensitiveDataSA"), 16)));

				Map<String, String> params = new HashMap<String, String>();
				params.put("sdId", request.getParameter("sdId"));
				params.put("pcIconTempFileAbsPath", request.getParameter("pcIconTempFileAbsPath"));
				params.put("mobileIconTempFileAbsPath", request.getParameter("mobileIconTempFileAbsPath"));
				params.put("applicationTypeId", request.getParameter("applicationTypeId"));
				params.put("applicationImgTempFileAbsPath", request.getParameter("applicationImgTempFileAbsPath"));

				applicationManager.modifyApplicationBaseInfo(username, application, params);

				if (log.isDebugEnabled()) {
					log.debug("\napplicationVersionId: " + applicationId + "\n");
				}
				message.setMessage(applicationId);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage modifyDeleteRuleAndPersonalType(HttpServletRequest request, @RequestParam Long applicationId) {
		JsonMessage message = new JsonMessage();
		try {
			Application application = applicationManager.load(applicationId);
			// BindingResult result = SpringMVCUtils.bindObject(request,
			// application);
			Integer deleteRule = ServletRequestUtils.getIntParameter(request, "deleteRule");
			Integer personalType = ServletRequestUtils.getIntParameter(request, "personalType");
			application.setDeleteRule(deleteRule);
			application.setPersonalType(personalType);
			applicationManager.saveOrUpdate(application);
			message.setMessage(applicationId);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getImgIdByAppId(HttpServletRequest request, @RequestParam Long applicationId) {
		JsonMessage message = new JsonMessage();
		try {
			Application application = applicationManager.load(applicationId);
			Set<ApplicationImage> imgSet = application.getApplicationImages();
			StringBuffer sb = new StringBuffer("");
			for (Iterator<ApplicationImage> imgIter = imgSet.iterator(); imgIter.hasNext();) {
				ApplicationImage aImg = imgIter.next();
				sb.append(aImg.getId() + ",");
			}
			if (sb.toString().equals("")) {
				message.setMessage("");
			} else {
				message.setMessage(sb.toString().substring(0, sb.toString().length() - 1));
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	/**
	 * 默认列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Application> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Long cardAppId = ServletRequestUtils.getLongParameter(request, "cardAppId"); // 有cardAppId表示来自用户查看以前某个版本的信息
			page = applicationManager.findPage(page, filters);
			List<Application> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = applicationResult(applicationList, cardAppId);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult findByAppType(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Application> page = SpringMVCUtils.getPage(request);
			// List<PropertyFilter> filters =
			// SpringMVCUtils.getParameters(request);
			Long parentId = ServletRequestUtils.getLongParameter(request, "type");
			if (parentId == -1) {
				ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages");
				String text = bundle.getString("indexNavigateCategoryIds");
				result.setMessage(text);
				return result;
			}
			page = applicationManager.findByAppType(page, parentId);
			List<Application> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = applicationResult(applicationList, null);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 应用推荐列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult recommendAppList(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Application> page = SpringMVCUtils.getPage(request);
			// List<PropertyFilter> filters =
			// SpringMVCUtils.getParameters(request);
			page.setPageSize(999);
			page = applicationManager.recommendAppList(page);
			List<Application> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = applicationResult(applicationList, null);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
			JsonResult result2 = this.recommendApplication(request);
			result.setMessage(result2.getMessage());
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 用户评论加载
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult loadComment(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationComment> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			String backMessage = "";
			page = applicationCommentManager.findPage(page, filters);
			List<ApplicationComment> applicationList = page.getResult();
			List<Map<String, Object>> applicationComments = applicationCommentResult(applicationList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(applicationComments);
			result.setPage(pageMap);
			if (StringUtils.isEmpty(SpringSecurityUtils.getCurrentUserName())) { // 登录状态等统一放在加载评论时判断
				backMessage = "notlogin";
			} else {
				SysUser sysUser = sysUserManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
				if (sysUser.getSysRole() == null
						|| (sysUser.getSysRole() != null && !sysUser.getSysRole().getRoleName().equals(SpecialRoleType.CUSTOMER.toString()))) {
					backMessage = "notcustomer";
				}
			}
			result.setMessage(backMessage);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 判断当前用户是否订购应用
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage hasSubscribed(HttpServletRequest request, @RequestParam Long appId) {
		JsonMessage message = new JsonMessage();
		boolean hasSubscribed = false;
		try {
			Page<SubscribeHistory> pageSub = SpringMVCUtils.getPage(request);
			hasSubscribed = this.subscribeHistoryManager.hasSubscribed(pageSub, appId, false);
			message.setMessage(hasSubscribed);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * 最近用户下载
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult recentlyDownLoad(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Long appId = ServletRequestUtils.getLongParameter(request, "id", 0);
			boolean isRecently = ServletRequestUtils.getBooleanParameter(request, "isRecently", false);
			Page<SubscribeHistory> page = SpringMVCUtils.getPage(request);
			page = subscribeHistoryManager.recentlyDownLoad(page, appId, isRecently);
			List<SubscribeHistory> applicationList = page.getResult();
			List<Map<String, Object>> applicationComments = subscribeHistoryResult(applicationList, isRecently);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(applicationComments);
			result.setPage(pageMap);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 推荐应用下载
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult recommendApplication(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<RecommendApplication> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			boolean local = ServletRequestUtils.getBooleanParameter(request, "local", false); // 是否本地应用
			page = recommendApplicationManager.findRecommendApplication(page, filters, local);
			List<RecommendApplication> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = recommendApplicationResult(applicationList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
			List<RecommendApplication> recommendApplicationList = page.getResult();
			StringBuffer orderNos = new StringBuffer("");
			for (RecommendApplication ra : recommendApplicationList) {
				orderNos.append(ra.getOrderNo() + ":" + ra.getApplication().getLocation() + ",");
			}
			String orderNoStr = "";
			if (!orderNos.toString().equals("")) {
				orderNoStr = orderNos.toString().substring(0, orderNos.toString().length() - 1);
			}
			result.setMessage(orderNoStr);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 评论saveorUpdate
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage updateComment(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationComment ac = new ApplicationComment();
			BindingResult result = SpringMVCUtils.bindObject(request, ac);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				ac.getApplication().setId(ServletRequestUtils.getLongParameter(request, "applicationId"));
				ac.setId(ServletRequestUtils.getLongParameter(request, "id", -1) == -1 ? null : ServletRequestUtils.getLongParameter(
						request, "id"));
				applicationCommentManager.saveOrUpdate(ac);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * 是否登录
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage isLogin(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			if (SpringSecurityUtils.getCurrentUserName() != null && !SpringSecurityUtils.getCurrentUserName().equals("")) {
				message.setSuccess(true);
			} else {
				message.setSuccess(false);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * 推荐应用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage saveRecommend(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			RecommendApplication ra = new RecommendApplication();
			BindingResult result = SpringMVCUtils.bindObject(request, ra);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				recommendApplicationManager.saveOrUpdate(ra);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * 删除推荐应用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage removeRecommend(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			recommendApplicationManager.remove(id);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	private List<Map<String, Object>> subscribeHistoryResult(List<SubscribeHistory> subscribeHistoryList, boolean isRecently) {
		List<Map<String, Object>> subscribeHistorys = new ArrayList<Map<String, Object>>(subscribeHistoryList.size());
		if (isRecently) {
			Map<String, String> nameMap = new HashMap<String, String>();
			for (int i = 0; i < subscribeHistoryList.size(); i++) {
				Map<String, Object> mappedSubscribeHistory = subscribeHistoryList.get(i).toMap(null, null);
				Customer customer = subscribeHistoryList.get(i).getCustomerCardInfo().getCustomer();
				mappedSubscribeHistory.put("userName",
						customer.getNickName() == null ? customer.getSysUser().getUserName() : customer.getNickName());
				mappedSubscribeHistory.put("mobile", customer.getSysUser().getMobile());
				mappedSubscribeHistory.put("appId", subscribeHistoryList.get(i).getApplicationVersion().getApplication().getId());
				mappedSubscribeHistory.put("appName", subscribeHistoryList.get(i).getApplicationVersion().getApplication().getName());
				mappedSubscribeHistory.put("subscribeTime",
						DateFormatUtils.format((Calendar) subscribeHistoryList.get(i).getSubscribeDate(), "yyyy-MM-dd HH:mm"));
				mappedSubscribeHistory.put("customerId", subscribeHistoryList.get(i).getCustomerCardInfo().getCustomer().getId());
				mappedSubscribeHistory.put("hasIcon", subscribeHistoryList.get(i).getCustomerCardInfo().getCustomer().getPcIcon() != null);
				if (nameMap.get(mappedSubscribeHistory.get("userName").toString()) == null) {
					nameMap.put(mappedSubscribeHistory.get("userName").toString(), mappedSubscribeHistory.get("userName").toString());
				} else {
					continue;
				}
				subscribeHistorys.add(mappedSubscribeHistory);
				if (subscribeHistorys.size() > 6) {
					return subscribeHistorys;
				}
			}
			return subscribeHistorys;
		} else {
			for (SubscribeHistory ac : subscribeHistoryList) {
				Map<String, Object> mappedSubscribeHistory = ac.toMap(null, null);
				Customer customer = ac.getCustomerCardInfo().getCustomer();
				mappedSubscribeHistory.put("userName",
						customer.getNickName() == null ? customer.getSysUser().getUserName() : customer.getNickName());
				mappedSubscribeHistory.put("mobile", customer.getSysUser().getMobile());
				mappedSubscribeHistory.put("appId", ac.getApplicationVersion().getApplication().getId());
				mappedSubscribeHistory.put("appName", ac.getApplicationVersion().getApplication().getName());
				mappedSubscribeHistory.put("subscribeTime", DateFormatUtils.format((Calendar) ac.getSubscribeDate(), "yyyy-MM-dd HH:mm"));
				mappedSubscribeHistory.put("iconUrl", ac.getCustomerCardInfo().getCustomer().getIconUrl());
				subscribeHistorys.add(mappedSubscribeHistory);
			}
			return subscribeHistorys;
		}
	}

	private List<Map<String, Object>> applicationCommentResult(List<ApplicationComment> applicationComments) {
		List<Map<String, Object>> mappedComments = new ArrayList<Map<String, Object>>(applicationComments.size());
		for (ApplicationComment ac : applicationComments) {
			Map<String, Object> mappedApplicationComment = ac.toMap(null, null);
			// System.out.println(SpringSecurityUtils.getCurrentUserName());
			if (SpringSecurityUtils.getCurrentUserName().equals("")) {
				mappedApplicationComment.put("isCurrentUser", null);
			} else if (ac.getCustomer().getSysUser().getUserName().equals(SpringSecurityUtils.getCurrentUserName())) {
				mappedApplicationComment.put("isCurrentUser", "true");
			} else {
				mappedApplicationComment.put("isCurrentUser", "false");
			}
			if (ac.getCustomer() != null) {
				mappedApplicationComment.put("user_userName", ac.getCustomer().getNickName() == null ? ac.getCustomer().getSysUser()
						.getUserName() : ac.getCustomer().getNickName());
				mappedApplicationComment.put("customerId", ac.getCustomer().getId());
				if (ac.getCustomer().getPcIcon() != null) {
					mappedApplicationComment.put("hasIcon", true);
				} else {
					mappedApplicationComment.put("hasIcon", false);
				}
			} else {
				mappedApplicationComment.put("user_userName", "用户不存在");
				mappedApplicationComment.put("hasIcon", false);
			}
			mappedComments.add(mappedApplicationComment);
		}
		return mappedComments;
	}

	private List<Map<String, Object>> applicationResult(List<Application> applications, Long cardAppId) {
		List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>(applications.size());
		ApplicationVersion av = null;
		for (Application application : applications) {
			Map<String, Object> mappedApplication = application.toMap(null, "sd.sdName childType.name");
			GradeStatistics gs = application.getStatistics();
			mappedApplication.put("avgCount", application.getStarNumber() == null ? 0 : application.getStarNumber());
			mappedApplication.put("avgCountInTen", gs == null ? "暂无评价" : gs.getAvgNumberInTen());
			SpBaseInfo sp = application.getSp();
			mappedApplication.put("spName", sp == null ? "" : sp.getName());
			if (application.getLastestVersion() != null) {
				if (cardAppId == null) {
					av = applicationVersionManager.getAidAndVersionNo(application.getAid(), application.getLastestVersion());
					if (av != null) {
						String formatValue = "";
						if (av.getPublishDate() != null) {
							formatValue = DateFormatUtils.format((Calendar) av.getPublishDate(), "yyyy-MM-dd HH:mm:ss");
						}
						mappedApplication.put("updateDate", formatValue);
					}
					// 应用有无可用版本
					boolean hasAvailableVer = false;
					List<ApplicationVersion> versions = application.getVersions();
					for (ApplicationVersion _av : versions) {
						if (_av.getStatus().equals(ApplicationVersion.STATUS_PULISHED)) {
							hasAvailableVer = true;
							break;
						}
					}
					mappedApplication.put("hasAvailableVer", hasAvailableVer);
					//
					mappedApplication.put("versionNo", application.getLastestVersion());
					mappedApplication.put("lastestVersionNo", application.getLastestVersion());
					Space space = application.getLastestSpace();
					mappedApplication.put("spaceNvm", space.spaceToString(space.getNvm()));
					mappedApplication.put("spaceRam", space.spaceToString(space.getRam()));
					mappedApplication.put("type", "normal");

					// 号段判断
					// String username = SpringSecurityUtils.get
					// end
				} else {
					CardApplication ca = cardApplicationManager.load(cardAppId);
					mappedApplication.put("versionNo", ca.getApplicationVersion().getVersionNo());
					mappedApplication.put("lastestVersionNo", application.getLastestVersion());
					av = applicationVersionManager.load(ca.getApplicationVersion().getId());
					if (av != null) {
						String formatValue = "";
						if (av.getPublishDate() != null) {
							formatValue = DateFormatUtils.format((Calendar) av.getPublishDate(), "yyyy-MM-dd HH:mm:ss");
						}
						mappedApplication.put("publishDate", formatValue);
					}
					Space space = new Space();
					mappedApplication.put("spaceNvm", space.spaceToString(ca.getUsedNonVolatileSpace()));
					mappedApplication.put("spaceRam", space.spaceToString(ca.getUsedVolatileSpace()));
					mappedApplication.put("type", "oldversion");
				}
			}

			if (application.getPcIcon() != null) {
				mappedApplication.put("hasIcon", true);
			} else {
				mappedApplication.put("hasIcon", false);
			}
			mappedApplications.add(mappedApplication);
		}
		return mappedApplications;
	}

	private List<Map<String, Object>> recommendApplicationResult(List<RecommendApplication> applications) {
		List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>(applications.size());
		for (RecommendApplication recommendApplication : applications) {
			Map<String, Object> mappedApplication = recommendApplication.toMap(null,
					"application.name application.starNumber application.id application.downloadCount application.location");
			if (recommendApplication.getApplication().getPcIcon() != null) {
				mappedApplication.put("hasIcon", true);
			} else {
				mappedApplication.put("hasIcon", false);
			}
			mappedApplications.add(mappedApplication);
		}
		return mappedApplications;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage defChange(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String appId = request.getParameter("appId");
			String deleteRule = request.getParameter("deleteRule");
			String personalType = request.getParameter("personalType");
			applicationManager.defChange(appId, deleteRule, personalType);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getLocationMobileStatus(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String cardNo = ServletRequestUtils.getStringParameter(request, "cardNo", "");
			String appLocation = ServletRequestUtils.getStringParameter(request, "appLocation", "");
			String status = applicationManager.getLocationMobileStatus(cardNo, appLocation);
			message.setMessage(status);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage changeAppStatus(@RequestParam Long appId, @RequestParam Integer appStatus) {
		JsonMessage message = new JsonMessage();
		try {
			applicationManager.changeAppStatus(appId, appStatus);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * @Title: getAppPcImg
	 * @Description: 获取应用的图片
	 * @param response
	 * @param appId
	 */
	@RequestMapping
	public void getAppPcImg(HttpServletResponse response, @RequestParam Long appId) {
		try {
			byte[] image = applicationManager.getPcImgByAppId(appId);
			if (image != null) {
				SpringMVCUtils.writeImage(image, response);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@RequestMapping
	public void getAppMobileImg(HttpServletResponse response, @RequestParam Long appId) {
		try {
			Application application = applicationManager.load(appId);
			byte[] image = application.getMoblieIcon();
			if (image != null) {
				SpringMVCUtils.writeImage(image, response);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// 应用截图
	@RequestMapping
	public void getAppImg(HttpServletResponse response, @RequestParam Long appImgId) {
		try {
			ApplicationImage applicationImage = applicationImageManager.loadById(appImgId);
			byte[] image = applicationImage.getApplicationImage();
			if (image != null) {
				SpringMVCUtils.writeImage(image, response);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@RequestMapping
	public void getAppMobileImgByAid(HttpServletResponse response, @RequestParam String aId) {
		try {
			Application application = applicationManager.getByAid(aId);
			byte[] image = application.getMoblieIcon();
			if (image != null) {
				SpringMVCUtils.writeImage(image, response);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @Title: getAppById
	 * @Description:
	 * @param response
	 * @param appId
	 * @return 获取指定的应用的信息
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getAppById(HttpServletResponse response, @RequestParam Long appId) {
		JsonMessage message = new JsonMessage();
		try {
			Application app = applicationManager.load(appId);
			List<ApplicationVersion> appVers = app.getVersions();
			List<String> appVerList = new ArrayList<String>();
			for (ApplicationVersion appVer : appVers) {
				if (appVer.getStatus().equals(ApplicationVersion.STATUS_PULISHED)) {
					appVerList.add(appVer.getVersionNo());
				}
			}
			Map<String, Object> vMap = app.toMap(null, "sp.name");
			vMap.put("vers", appVerList);
			message.setMessage(vMap);
		} catch (PlatformException e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getAppGooVer(HttpServletResponse response, @RequestParam Long appId, @RequestParam String cardNo) {
		JsonMessage message = new JsonMessage();
		try {
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			Application app = applicationManager.load(appId);
			ApplicationVersion appver = applicationVersionManager.getLastestAppVersionSupportCard(card, app);
			message.setMessage(appver.getVersionNo());
		} catch (PlatformException e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	/**
	 * 高级搜索
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult advanceSearch(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		Page<Application> page = SpringMVCUtils.getPage(request);
		try {
			String name = ServletRequestUtils.getStringParameter(request, "name");
			String childIds = ServletRequestUtils.getStringParameter(request, "childs");
			String father = ServletRequestUtils.getStringParameter(request, "father");
			String spId = ServletRequestUtils.getStringParameter(request, "sp");
			String star = ServletRequestUtils.getStringParameter(request, "star");
			Map<String, String> paramMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(name)) {
				paramMap.put("name", name.trim());
			}
			paramMap.put("childIds", childIds);
			paramMap.put("spId", spId);
			paramMap.put("star", star);
			paramMap.put("father", father);
			page = applicationManager.advanceSearch(page, paramMap);
			List<Application> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = applicationResult(applicationList, null);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 是否已评论
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage isCommented(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Long appId = ServletRequestUtils.getLongParameter(request, "id");
			boolean isCommented = applicationCommentManager.isCommented(appId);
			message.setMessage(isCommented);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public void uploadPcIcon(HttpServletRequest request, HttpServletResponse response, @RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = commonsController.upload(request, file);

		commonsController.convertToJson(response, result);
	}

	@RequestMapping
	public void uploadAppliationImage(HttpServletRequest request, HttpServletResponse response, @RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = commonsController.upload(request, file);

		commonsController.convertToJson(response, result);
	}

	@RequestMapping
	public void uploadMobileIcon(HttpServletRequest request, HttpServletResponse response, @RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = commonsController.upload(request, file);
		try {
			@SuppressWarnings("unchecked")
			String absPath = ((Map<String, String>) result.getMessage()).get("tempFileAbsPath");

			BufferedImage image = ImageIO.read(new File(absPath));
			int width = image.getWidth();
			int height = image.getHeight();
			if (50 != width || 50 != height) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_MOBILE_ICON_OVERSIZE);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		commonsController.convertToJson(response, result);
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getByCriteria(HttpServletRequest request) {
		log.debug("\n" + "LoadFileVersion.getByCriteria" + "\n");
		if (log.isDebugEnabled()) {
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
		}

		JsonResult result = new JsonResult();

		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<Application> page = SpringMVCUtils.getPage(request);

			page = applicationManager.findPage(page, filters);

			result.setPage(page, "", "childType.id sd.id");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage exportConstant() {
		JsonMessage message = new JsonMessage();

		try {
			Map<String, Integer> constant = new HashMap<String, Integer>();

			constant.put("STATUS_INIT", Application.STATUS_INIT);
			constant.put("STATUS_AUDITED", Application.STATUS_AUDITED);
			constant.put("STATUS_PUBLISHED", Application.STATUS_PUBLISHED);
			constant.put("STATUS_DISABLE", Application.STATUS_DISABLE);
			constant.put("STATUS_ARCHIVED", Application.STATUS_ARCHIVED);

			message.setMessage(constant);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage validateAid(@RequestParam("aid") String aid) {
		JsonMessage message = new JsonMessage();
		try {
			applicationManager.validateAid(aid.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage remove(Long applicationId) {
		JsonMessage message = new JsonMessage();
		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			Application application = applicationManager.load(applicationId);

			applicationManager.remove(application, username);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getApplication(@RequestParam Long appId) {
		JsonMessage message = new JsonMessage();
		try {
			Application application = applicationManager.load(appId);
			message.setMessage(application.toMap(null, "sp.name sp.id childType.name"));
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getShowTypeApp() {
		JsonMessage message = new JsonMessage();
		try {
			List<Map<String, Object>> resultList = applicationManager.getShowTypeApp();
			message.setMessage(resultList);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

}

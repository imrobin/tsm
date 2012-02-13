/**
 * CustomerCardController.java
 * 
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.customer.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.fee.manager.FeeStatManager;

/**
 * @ClassName: CustomerCardController
 * @Description: 与终端相关的CONTROLLER
 * @author liqiang.wang1
 * @date 2011-4-22 上午09:34:32
 * 
 */
@Controller("customerCardController")
@RequestMapping("/customerCard/")
public class CustomerCardController {

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private CardInfoManager cardManager;

	@Autowired
	private FeeStatManager feeStatManager;

	/**
	 * @Title: getCustomerCardByCustomer
	 * @Description: 列出当前用户指定终端
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Application> page = SpringMVCUtils.getPage(request);
			Integer status = ServletRequestUtils.getIntParameter(request, "status");
			String aid = ServletRequestUtils.getStringParameter(request, "aid");
			String userName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isNotBlank(userName)) {
				List<CustomerCardInfo> customerCardList = customerCardInfoManager.getCustomerCardByCustomerName(userName, status);
				List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>();
				cciListToMap(aid, customerCardList, mappedApplications);
				Page<Map<String, Object>> pageMap = page.getMappedPage();
				pageMap.setResult(mappedApplications);
				result.setPage(pageMap);
				result.setTotalCount(customerCardList.size());
			}
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
	 * @param aid
	 * @param customerCardList
	 * @param mappedApplications
	 */
	private void cciListToMap(String aid, List<CustomerCardInfo> customerCardList, List<Map<String, Object>> mappedApplications) {
		for (CustomerCardInfo cci : customerCardList) {
			Map<String, Object> mappedApplication = cci.toMap(null, "mobileType.type mobileType.id mobileType.brandChs");
			CardApplication ca = cardApplicationManager.getAvailbleOrLockedByCardNoAid(cci.getCard().getCardNo(), aid);
			mappedApplication.put("clientStatusStr", "");
			mappedApplication.put("userName", cci.getCustomer().getSysUser().getUserName());
			mappedApplication.put("cardNo", cci.getCard().getCardNo());
			if (ca == null) {
				mappedApplication.put("clientStatusStr", PlatformErrorCode.NOT_DOWN_APP.getDefaultMessage());
			} else {
				boolean hasSysRequirment = customerCardInfoManager.hasSysRequirment(cci, ca);
				if (!hasSysRequirment) {
					mappedApplication.put("clientStatusStr", PlatformErrorCode.NOT_DOWN_CLINET.getDefaultMessage());
				} else {
					mappedApplication.put("clientAndroidUrl", "");
					mappedApplication.put("clientJ2MEUrl", "");
					MobileType mt = cci.getMobileType();
					Set<ApplicationClientInfo> acs = ca.getApplicationVersion().getClients();
					ApplicationClientInfo androidTemp = null;
					ApplicationClientInfo j2meacTemp = null;
					for (Iterator<ApplicationClientInfo> it = acs.iterator(); it.hasNext();) {
						ApplicationClientInfo ac = (ApplicationClientInfo) it.next();
						if (mt.getOriginalOsKey().equals(ac.getSysRequirment())) {
							// 应用详情-下载客户端：当同一手机型号对应了多个版本的Android客户端时
							// ，应该下载当前手机型号对应的最高版本的客户端，以版本号来判断，而不是上传时间。
							if (androidTemp == null || SpringMVCUtils.compareVersion(ac.getVersion(), androidTemp.getVersion())) {
								mappedApplication.put("clientAndroidUrl", ac.getFileUrl());
								androidTemp = ac;
							}
						} else if (mt.getJ2meKey().equals(ac.getSysRequirment())) {
							if ((j2meacTemp == null || SpringMVCUtils.compareVersion(ac.getVersion(), j2meacTemp.getVersion()))
									&& ac.getFileUrl().endsWith(".jad")) {
								mappedApplication.put("clientJ2MEUrl", ac.getFileUrl());
								j2meacTemp = ac;
							}
						}
					}
				}
			}
			mappedApplications.add(mappedApplication);
		}
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult listLost(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = customerCardInfoManager.findPage(page, filters);
			// result.setPage(page, null,
			// "customer.sysUser.userName mobileType.type mobileType.id mobileType.brandChs");
			List<CustomerCardInfo> cciList = page.getResult();
			List<Map<String, Object>> mappedCustomerCardInfos = new ArrayList<Map<String, Object>>();
			for (CustomerCardInfo cci : cciList) {
				Map<String, Object> mappedCustomerCardInfo = cci.toMap(null, "mobileType.type mobileType.id mobileType.brandChs");
				if (cci.getCustomer().getSysUser() != null) {
					mappedCustomerCardInfo.put("userName", cci.getCustomer().getSysUser().getUserName());
				} else {
					mappedCustomerCardInfo.put("userName", "");
				}
				mappedCustomerCardInfos.add(mappedCustomerCardInfo);
			}
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedCustomerCardInfos);
			result.setPage(pageMap);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = customerCardInfoManager.findPage(page, filters);
			List<CustomerCardInfo> cciList = page.getResult();
			List<Map<String, Object>> mappedCustomerCardInfos = new ArrayList<Map<String, Object>>();
			for (CustomerCardInfo cci : cciList) {
				Map<String, Object> mappedCustomerCardInfo = cci.toMap(null, "mobileType.type mobileType.id mobileType.brandChs");
				if (cci.getCustomer().getSysUser() != null) {
					mappedCustomerCardInfo.put("userName", cci.getCustomer().getSysUser().getUserName());
				} else {
					mappedCustomerCardInfo.put("userName", "");
				}
				mappedCustomerCardInfos.add(mappedCustomerCardInfo);
			}
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedCustomerCardInfos);
			result.setPage(pageMap);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult cancelLost(HttpServletRequest request, @RequestParam Long ccid) {
		JsonResult result = new JsonResult();
		try {
			customerCardInfoManager.cancelLost(ccid);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * @Title: getCusstomerCard
	 * @Description:获得指定的终端信息
	 * @param request
	 * @param ccId
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getCusstomerCard(HttpServletRequest request, @RequestParam Long ccId) {
		JsonResult result = new JsonResult();
		try {
			CustomerCardInfo customerCard = customerCardInfoManager.load(ccId);
			List<CustomerCardInfo> resustList = new ArrayList<CustomerCardInfo>();
			resustList.add(customerCard);
			result.setResult(resustList, null, "mobileType.brandChs mobileType.type mobileType.id card.cardNo card.status customer.active");
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
	 * @Title: getCardList
	 * @Description: 获得用户指定状态的所有终端
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getCanChange(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			String userName = SpringSecurityUtils.getCurrentUser().getUsername();
			List<CustomerCardInfo> customerCardList = customerCardInfoManager.getCustomerCardCanChange(userName);
			result.setResult(customerCardList, null, "mobileType.brandChs");
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
	 * @Title: getCardList
	 * @Description: 获取能够恢复应用的终端(即被挂失和被注销的)
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getCanRevert(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			String userName = SpringSecurityUtils.getCurrentUser().getUsername();
			List<CustomerCardInfo> customerCardList = customerCardInfoManager.getCanRevertByCustomerName(userName);
			result.setResult(customerCardList, null, "mobileType.brandChs");
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
	 * @Title: customerCardLost
	 * @Description: 挂失终端
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage customerCardLost(HttpServletRequest req, @RequestParam Long ccId) {
		JsonMessage message = new JsonMessage();
		try {
			customerCardInfoManager.doCustomerCardInfoLost(ccId);
			// TODO 循环发送每个应用的业务平台说明此终端已经挂失应在事务外面做
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
	 * @Title: customerCardRecover
	 * @Description: 注销终端
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage createDelAppListForCancelTerm(HttpServletRequest req, @RequestParam Long ccId) {
		JsonMessage message = new JsonMessage();
		try {
			List<Map<String, Object>> list = customerCardInfoManager.createDelAppListForCancelTerm(ccId);
			message.setMessage(list);
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
	 * @Title: customerCardRecover
	 * @Description: 注销终端
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage finishCancel(HttpServletRequest req, @RequestParam Long ccId) {
		JsonMessage message = new JsonMessage();
		try {
			customerCardInfoManager.finashCancel(ccId);
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
	JsonMessage getCustomerInfoByCardNo(HttpServletRequest req, @RequestParam String cardNo) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String, Object> resultMap = customerCardInfoManager.getCardMessageByCardNo(cardNo);
			message.setMessage(resultMap);
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
	 * @Title: listCardApp
	 * @Description: 显示卡片上终端详情
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult listCardApp(HttpServletRequest req, @RequestParam Long ccId) {
		JsonResult result = new JsonResult();
		try {
			List<Map<String, Object>> appList = customerCardInfoManager.getAppMaplistByCustomerCard(ccId);
			result.setResult(appList);
			result.setTotalCount(appList.size());
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
	 * @Title: listCardSD
	 * @Description: 显示卡终端的安全域列表
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult listCardSD(HttpServletRequest req, @RequestParam Long ccId) {
		JsonResult result = new JsonResult();
		try {
			List<Map<String, Object>> sdList = customerCardInfoManager.getSDMaplistByCustomerCard(ccId);
			result.setResult(sdList);
			result.setTotalCount(sdList.size());
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
	 * @Title: listCardApp
	 * @Description: 显示卡片上应用详情
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult listRevertApps(HttpServletRequest req, @RequestParam Long ccId) {
		JsonResult result = new JsonResult();
		try {
			List<Application> appList = customerCardInfoManager.listRevertApps(ccId);
			result.setResult(appList, null, "sp.name");
			result.setTotalCount(appList.size());
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
	 * @Title: listCardApp
	 * @Description: 显示卡片上指定条件的应用
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult searchApps(HttpServletRequest request, @RequestParam String moibleNo) {
		JsonResult result = new JsonResult();
		try {
			if (StringUtils.isNotBlank(moibleNo)) {
				Page<CardApplication> page = SpringMVCUtils.getPage(request);
				List<Map<String, Object>> resultList = customerCardInfoManager.getCardApplicationsByMobileNo(moibleNo, page);
				result.setResult(resultList);
			} else {
				return result;
			}
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
	 * @Title: listCardApp
	 * @Description: 显示卡片上指定条件的应用
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult searchCustomerCardInfos(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		String moibleNo = request.getParameter("search_LIKES_mobileNo");
		try {
			if (StringUtils.isNotBlank(moibleNo)) {
				Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
				List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
				page = customerCardInfoManager.findPage(page, filters);
				List<CustomerCardInfo> cciList = page.getResult();
				List<Map<String, Object>> mappedCustomerCardInfos = new ArrayList<Map<String, Object>>();
				for (CustomerCardInfo cci : cciList) {
					Map<String, Object> mappedCustomerCardInfo = cci.toMap(null, "mobileType.type mobileType.id mobileType.brandChs");
					if (cci.getCustomer().getSysUser() != null) {
						mappedCustomerCardInfo.put("userName", cci.getCustomer().getSysUser().getUserName());
					} else {
						mappedCustomerCardInfo.put("userName", "");
					}
					mappedCustomerCardInfos.add(mappedCustomerCardInfo);
				}
				Page<Map<String, Object>> pageMap = page.getMappedPage();
				pageMap.setResult(mappedCustomerCardInfos);
				result.setPage(pageMap);
			} else {
				return result;
			}
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
	 * @Title: getCardSzie
	 * @Description: 计算当前卡片上的应用空间与占用比例
	 * @param request
	 * @param ccId
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getCardSzie(HttpServletRequest request, @RequestParam Long ccId) {
		JsonResult result = new JsonResult();
		try {
			Map<String, Object> infoMap = customerCardInfoManager.calCardSize(ccId);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			resultList.add(infoMap);
			result.setResult(resultList);
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
	 * @Title: bindCard
	 * @Description: 手机绑定
	 * @param req
	 * @param ccId
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage bindCard(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			Customer customer = customerManager.getCustomerByUserName(SpringSecurityUtils.getCurrentUserName());
			paramMap.put("userName", SpringSecurityUtils.getCurrentUserName());
			paramMap.put("mobileNo", customer.getSysUser().getMobile());
			paramMap.put("cardNo", request.getParameter("cardNo"));
			paramMap.put("mobileTypeId", request.getParameter("mobileTypeId"));
			paramMap.put("mobileName", request.getParameter("phoneName"));
			// 获得建立好的关联关系
			CustomerCardInfo customerCardInfo = customerCardInfoManager.bindCard(paramMap, false);
			Long customerCardId = customerCardInfo.getId();
			// 发送激活码流程
			customerCardInfoManager.sendActive(customerCardId, "1");
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("customerCardId", customerCardId);
			messageMap.put("mobileNo", paramMap.get("mobileNo"));
			messageMap.put("userName", paramMap.get("userName"));
			message.setMessage(messageMap);
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
	 * @Title: 激活绑定
	 * @Description:
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage activeCard(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			String avtiveCode = request.getParameter("avtiveCode");
			String ccId = request.getParameter("ccId");
			CustomerCardInfo cci = customerCardInfoManager.load(Long.valueOf(ccId));
			boolean success = customerCardInfoManager.activeCard(userName, avtiveCode, ccId);
			if (!success) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("请检查您的激活码");
			} else {
				feeStatManager.genPerStatRecord(cci.getMobileNo(), cci.getCard().getCardNo());
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
	 * @Title: 改变终端激活
	 * @Description:
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage changeActive(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			String avtiveCode = request.getParameter("avtiveCode");
			String ccId = request.getParameter("ccId");
			String oldId = request.getParameter("oldId");
			if (StringUtils.isBlank(oldId)) {
				CustomerCardInfo oldInfo = (CustomerCardInfo) request.getAttribute("old");
				oldId = String.valueOf(oldInfo.getId());
			}
			Map<String, Object> resultMap = customerCardInfoManager.changeActive(userName, avtiveCode, ccId, oldId);
			if (!(Boolean) resultMap.get("active")) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("请检查您的激活码");
			}
			resultMap.put("odlId", oldId);
			resultMap.put("actveId", ccId);
			message.setMessage(resultMap);
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
	 * @Title: 再次发送激活码
	 * @Description: 再次发送激活码
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage reSendActive(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String ccId = request.getParameter("ccId");
			String type = request.getParameter("type");
			customerCardInfoManager.sendActive(Long.valueOf(ccId), type);
			message.setMessage(ccId);
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
	 * @Title: 再次发送激活码
	 * @Description: 再次发送激活码
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage checkSend(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String newMobileNo = request.getParameter("mobileNo");
			String activeCode = customerCardInfoManager.checkSend(newMobileNo);
			message.setMessage(activeCode);
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
	 * @Title: bindCard
	 * @Description: 手机绑定
	 * @param req
	 * @param ccId
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage changeBind(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("userName", SpringSecurityUtils.getCurrentUserName());
			paramMap.put("cardNo", request.getParameter("cardNo"));
			paramMap.put("mobileTypeId", request.getParameter("mobileTypeId"));
			paramMap.put("mobileName", request.getParameter("phoneName"));
			paramMap.put("oldCardId", request.getParameter("oldCardId"));
			// 进行绑定更换
			Long customerCardId = customerCardInfoManager.bindCardWithMobileType(paramMap, true);
			// 发送激活码流程
			customerCardInfoManager.sendActive(customerCardId, "1");
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("customerCardId", customerCardId);
			messageMap.put("mobileNo", paramMap.get("mobileNo"));
			messageMap.put("userName", paramMap.get("userName"));
			messageMap.put("oldCardId", paramMap.get("oldCardId"));
			message.setMessage(messageMap);
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
	 * @param request
	 * @return 恢复应用发送相关信息
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage revertApp(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String oldId = request.getParameter("oldId");
			CustomerCardInfo oldCci = customerCardInfoManager.load(Long.valueOf(oldId));
			CustomerCardInfo cci = getCustomerCardByRequest(request);
			if (null != cci) {
				if (cci.getCard().getStatus().equals(CardInfo.STATUS_DISABLE)) {
					message.setSuccess(Boolean.FALSE);
					message.setMessage(PlatformErrorCode.CARD_IS_DISABLE.getDefaultMessage());
				} else {
					if (cci.getCustomer().getSysUser().getUserName().equals(SpringSecurityUtils.getCurrentUserName())) {
						if (oldCci.getId().equals(cci.getId())) {
							message.setSuccess(Boolean.FALSE);
							message.setMessage("您选择的终端和当前终端相同,无需恢复");
						} else {
							if (oldCci.getStatus() == CustomerCardInfo.STATUS_CANCEL) {
								message.setSuccess(Boolean.FALSE);
								message.setMessage("您恢复应用的终端已被注销,不能进行应用恢复恢复功能");
							}
							List<CardApplication> canRvertApp = customerCardInfoManager.getRevertApp(oldCci, cci);
							List<String> aidList = new ArrayList<String>();
							for (CardApplication ca : canRvertApp) {
								aidList.add(ca.getApplicationVersion().getApplication().getAid());
							}
							Map<String, Object> resultMap = new HashMap<String, Object>();
							resultMap.put("aidList", aidList);
							resultMap.put("customerCardId", cci.getId());
							message.setMessage(resultMap);
						}
					} else {
						message.setSuccess(Boolean.FALSE);
						message.setMessage("此终端不属于用户");
					}
				}
			} else {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("用户未绑定该终端或该终端已被加入黑名单");
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

	private CustomerCardInfo getCustomerCardByRequest(HttpServletRequest request) {
		String cardNo = request.getParameter("cardNo");
		return customerCardInfoManager.getByCardNo(cardNo);
	}

	/**
	 * @Title: 检查恢复任务完成情况
	 * @Description: 假设恢复应用会返回数据给后台以参数形式.后期确定后会修改
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage checkRevertFinish(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			String ccid = request.getParameter("ccid");
			CustomerCardInfo cci = customerCardInfoManager.checkAndFinishRevert(userName, ccid);
			if (null == cci) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("还有应用未恢复完,请继续尝试");
			} else {
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				jsonMap.put("ccid", cci.getId());
				message.setMessage(jsonMap);
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
	JsonMessage checkChangeFinish(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String ccid = request.getParameter("ccid");
			String oldId = request.getParameter("oldId");
			customerCardInfoManager.checkChangeFinish(ccid, oldId);
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
	JsonMessage tipRevert(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String sessionId = request.getParameter("sessionId");
			Map<String, Object> resultmap = customerCardInfoManager.tipRevert(sessionId);
			message.setMessage(resultmap);
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
	JsonMessage changeMobileNo(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String newmobileNo = request.getParameter("mobileNo");
			String ccId = request.getParameter("ccId");
			customerCardInfoManager.changeMobileNo(ccId, newmobileNo);
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
	JsonMessage getCustomerInfoByCCI(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String ccId = request.getParameter("ccId");
			CustomerCardInfo cci = customerCardInfoManager.load(Long.valueOf(ccId));
			Customer customer = cci.getCustomer();
			Map<String, Object> map = customer.toMap(null, "sysUser.userName sysUser.realName sysUser.province");
			message.setMessage(map);
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
	JsonMessage getCardInfoByCCI(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String ccId = request.getParameter("ccId");
			CustomerCardInfo cci = customerCardInfoManager.load(Long.valueOf(ccId));
			CardInfo card = cci.getCard();
			Map<String, Object> map = card.toMap(null, "cardBaseInfo.name");
			Map<String, Object> sizeMap = customerCardInfoManager.calCardSize(Long.valueOf(ccId));
			map.putAll(sizeMap);
			List<Map<String, Object>> appList = customerCardInfoManager.getCardAppinfoListByCci(Long.valueOf(ccId));
			List<Map<String, Object>> sdList = customerCardInfoManager.getCardSDListByCci(Long.valueOf(ccId));
			resultMap.put("cardInfo", map);
			resultMap.put("cardAppInfo", appList);
			resultMap.put("cardSdInfo", sdList);
			message.setMessage(resultMap);
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
	JsonResult getByApplicationAidAndCurrentCustomerThatEmigrated(@RequestParam String aid) {
		JsonResult result = new JsonResult();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isNotBlank(userName)) {

				Application application = applicationManager.getByAid(aid);
				Customer customer = customerManager.getCustomerByUserName(userName);
				List<CustomerCardInfo> customerCardList = customerCardInfoManager.getByCustomerThatEmigratedApplication(application,
						customer);

				List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>();
				cciListToMap(aid, customerCardList, mappedApplications);
				result.setResult(mappedApplications);
				result.setTotalCount(customerCardList.size());
			}
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
	JsonResult getByApplicationAidAndCurrentCardNoThatEmigrated(@RequestParam String aid, @RequestParam String cardNo) {
		JsonResult result = new JsonResult();
		try {
			Application application = applicationManager.getByAid(aid);
			CardInfo card = cardManager.getByCardNo(cardNo);
			List<CustomerCardInfo> customerCardList = customerCardInfoManager.getByCardThatEmigratedApplication(application, card);

			List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>();
			cciListToMap(aid, customerCardList, mappedApplications);
			result.setResult(mappedApplications);
			result.setTotalCount(customerCardList.size());
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
	JsonMessage checkCancelTermCardApp(HttpServletRequest request, @RequestParam Long ccId) {
		JsonMessage message = new JsonMessage();
		try {
			boolean flag = customerCardInfoManager.checkCancelTermCardApp(ccId);
			message.setSuccess(flag);
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
	JsonMessage checkMobileNoLocation(HttpServletRequest request, @RequestParam String cardNo, @RequestParam Long appId) {
		JsonMessage message = new JsonMessage();
		Map<String, Object> forceMap = new HashMap<String, Object>();
		try {
			boolean flag = customerCardInfoManager.checkMobileNoLocation(cardNo, appId, forceMap);
			if (!flag) {
				message.setSuccess(false);
				forceMap.put("force", false);
				message.setMessage(forceMap);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			forceMap.put("force", true);
			forceMap.put("msg", e.getMessage());
			message.setMessage(forceMap);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			forceMap.put("force", true);
			forceMap.put("msg", e.getMessage());
			message.setMessage(forceMap);
		}
		return message;
	}

	/**
	 * @Title: listCardApp
	 * @Description: 显示卡片上指定条件的应用
	 * @param req
	 * @param resp
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult searchAppsForAdmin(HttpServletRequest request, @RequestParam String moibleNo) {
		JsonResult result = new JsonResult();
		try {
			if (StringUtils.isNotBlank(moibleNo)) {
				Page<CardApplication> page = SpringMVCUtils.getPage(request);
				List<Map<String, Object>> resultList = customerCardInfoManager.getCardApplicationsByMobileNoForAdmin(moibleNo, page);
				result.setResult(resultList);
			} else {
				return result;
			}
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
	JsonMessage adminbindcard(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			String customerId = request.getParameter("customerId");
			Customer customer = customerManager.load(Long.valueOf(customerId));
			paramMap.put("userName", customer.getSysUser().getUserName());
			paramMap.put("mobileNo", customer.getSysUser().getMobile());
			paramMap.put("cardNo", request.getParameter("cardNo"));
			paramMap.put("mobileTypeId", request.getParameter("mobileTypeId"));
			paramMap.put("mobileName", request.getParameter("phoneName"));
			// 获得建立好的关联关系
			Long customerCardId = customerCardInfoManager.bindCardWithMobileType(paramMap, false);
			CustomerCardInfo cci = customerCardInfoManager.load(customerCardId);
			feeStatManager.genPerStatRecord(cci.getMobileNo(), cci.getCard().getCardNo());
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
	JsonMessage getAllCardAppListByUser(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			List<Map<String, Object>> resultList = customerCardInfoManager.getAllAppListByUserName(userName);
			message.setMessage(resultList);
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
	JsonMessage getAllAppListByUser(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			List<Map<String, Object>> resultList = customerCardInfoManager.getAllAppListByUserName(userName);
			message.setMessage(resultList);
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
	JsonMessage getCardApplicationByUserAndAppId(HttpServletRequest request, @RequestParam Long appId) {
		JsonMessage message = new JsonMessage();
		try {
			String userName = SpringSecurityUtils.getCurrentUserName();
			List<Map<String, Object>> resultList = customerCardInfoManager.getCardApplicationByUserAndAppId(userName, appId);
			message.setMessage(resultList);
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
	 * 根据手机号获取非注销状态的终端列表
	 * 
	 * @param request
	 * @param mobileNo
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult findCustoemrCardInfoByMobileNo(HttpServletRequest request, @RequestParam String moibleNo) {
		JsonResult result = new JsonResult();
		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			page = customerCardInfoManager.getByMobileNoAllAndPage(page, moibleNo);
			result.setPage(page,null,"mobileType.brandChs mobileType.type");
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
}

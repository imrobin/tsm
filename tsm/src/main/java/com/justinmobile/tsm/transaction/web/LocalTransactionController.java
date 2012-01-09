package com.justinmobile.tsm.transaction.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.icu.text.SimpleDateFormat;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Controller("localTransactionController")
@RequestMapping("/localtransaction/")
public class LocalTransactionController {

	@Autowired
	private LocalTransactionManager localTransactionManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private DesiredOperationManager desiredOperationManager;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<LocalTransaction> page = SpringMVCUtils.getPage(request);
			page.addOrder("beginTime", "desc");
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			filters.add(new PropertyFilter("ALIAS_superTransactionL_NULLI_id", "0"));
			filters.add(new PropertyFilter("ALIAS_taskI_NOTNULLL_id", "0"));
			page = localTransactionManager.findPage(page, filters);
			// page =
			// localTransactionManager.findPage(page,ServletRequestUtils.getStringParameter(request,
			// "search_LIKES_mobileNo"),
			// ServletRequestUtils.getLongParameter(request,
			// "search_EQL_id",-1));
			List<LocalTransaction> requistionList = page.getResult();
			List<Map<String, Object>> mappedLocalTransactions = localTransactionResult(requistionList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedLocalTransactions);
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
	JsonMessage get(@RequestParam("id") Long id) {
		JsonMessage message = new JsonMessage();
		try {
			LocalTransaction lt = localTransactionManager.load(id);
			Map<String, Object> map = lt.toMap(null, null);
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
	JsonResult findDesiredOperationByUser(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<DesiredOperation> page = SpringMVCUtils.getPage(request);
			Map<String, String> paramMap = new HashMap<String, String>();
			String executionStatus = ServletRequestUtils.getStringParameter(request, "executionStatus");
			String resultStr = ServletRequestUtils.getStringParameter(request, "result");
			paramMap.put("result", resultStr);
			paramMap.put("executionStatus", executionStatus);
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			paramMap.put("currentUserName", currentUserName);
			page = desiredOperationManager.findPageByParam(page, paramMap);
			List<DesiredOperation> requistionList = page.getResult();
			List<Map<String, Object>> mappedDesiredOperation = desiredOperationResult(requistionList, executionStatus);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedDesiredOperation);
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
	JsonResult findDesiredOperationByCustomer(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<DesiredOperation> page = SpringMVCUtils.getPage(request);
			String executionStatus = ServletRequestUtils.getStringParameter(request, "executionStatus");
			String cardNo = request.getParameter("cardNo");
			String userName = SpringSecurityUtils.getCurrentUserName();
			if(StringUtils.isBlank(userName)){
				CustomerCardInfo customerCardInfo = customerCardInfoManager.getByCardNo(cardNo);
				SysUser user = customerCardInfo.getCustomer().getSysUser();
				userName = user.getUserName();
			}
			page = desiredOperationManager.findPageByCustomerParam(page, userName, executionStatus);
			List<DesiredOperation> requistionList = page.getResult();
			List<Map<String, Object>> mappedDesiredOperation = desiredOperationResult(requistionList, executionStatus);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedDesiredOperation);
			result.setTotalPage(page.getTotalPages());
			result.setTotalCount(page.getTotalCount());
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

	private List<Map<String, Object>> desiredOperationResult(List<DesiredOperation> requistionList, String executionStatus) {
		List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>(requistionList.size());
		List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
		List<Application> apps = new ArrayList<Application>();
		List<SecurityDomain> sds = new ArrayList<SecurityDomain>();
		Task task = null;
		for (DesiredOperation desiredOperation : requistionList) {
			if (desiredOperation.getPreProcess().intValue() == DesiredOperation.PREPROCESS_TURE
					&& desiredOperation.getIsExcuted().intValue() == DesiredOperation.NOT_EXCUTED) {
				continue;
			}
			if (desiredOperation.getTaskId() != null) {
				task = taskManager.load(desiredOperation.getTaskId());
			} else {
				task = new Task();
			}
			CustomerCardInfo customerCardInfo = null;
			if (null != desiredOperation.getCustomerCardId() && 0 != desiredOperation.getCustomerCardId()) {
				customerCardInfo = customerCardInfoManager.load(desiredOperation.getCustomerCardId());
			}
			propertyFilters.clear();
			apps.clear();
			sds.clear();
			Map<String, Object> mappedDesiredOperation = desiredOperation.toMap(null, null);
			mappedDesiredOperation.put("cardNo", "");
			Long cciId;
			if (null != (cciId = desiredOperation.getCustomerCardId())) {
				CustomerCardInfo cci = customerCardInfoManager.load(cciId);
				mappedDesiredOperation.put("cardNo", cci.getCard().getCardNo());
			}
			mappedDesiredOperation.put("idStr", String.valueOf(desiredOperation.getId()));
			mappedDesiredOperation.put("beginTime", CalendarUtils.parsefomatCalendar(task.getBeginTime(), CalendarUtils.LONG_FORMAT_LINE));
			if (null != customerCardInfo) {
				mappedDesiredOperation.put("cciName", customerCardInfo.getName());
			} else {
				mappedDesiredOperation.put("cciName", "");
			}
			mappedDesiredOperation.put("endTime", CalendarUtils.parsefomatCalendar(task.getEndTime(), CalendarUtils.LONG_FORMAT_LINE));
			String result = desiredOperation.getResult();
			if (StringUtils.isBlank(result)) {
				LocalTransaction localTransaction = localTransactionManager.getBySessionId(desiredOperation.getSessionId());
				if (localTransaction != null) {
					mappedDesiredOperation.put("failMessage", localTransaction.getFailMessage());
				}
			} else {
				mappedDesiredOperation.put("failMessage", result);
			}
			// if (executionStatus != null && Integer.parseInt(executionStatus)
			// == DesiredOperation.NOT_FINISH_EXCUTED){
			// List<LocalTransaction> localTransactions =
			// task.getLocalTransactions();
			// for (LocalTransaction lt : localTransactions){
			// if(!StringUtils.isEmpty(lt.getFailMessage())){
			// mappedDesiredOperation.put("failMessage", lt.getFailMessage());
			// break;
			// }
			// }
			// }
			propertyFilters.add(new PropertyFilter("aid", MatchType.EQ, PropertyType.S, desiredOperation.getAid()));
			if (desiredOperation.getProcedureName().equals(Operation.CREATE_SD.toString())
					|| desiredOperation.getProcedureName().equals(Operation.DELETE_SD.toString())
					|| desiredOperation.getProcedureName().equals(Operation.LOCK_SD.toString())
					|| desiredOperation.getProcedureName().equals(Operation.UNLOCK_SD.toString())
					|| desiredOperation.getProcedureName().equals(Operation.SYNC_CARD_SD.toString())
					|| desiredOperation.getProcedureName().equals(Operation.UPDATE_KEY.toString())) {
				sds = securityDomainManager.find(propertyFilters);
				if (sds != null && sds.size() != 0) {
					mappedDesiredOperation.put("appName", sds.get(0).getSdName());
					mappedDesiredOperation.put("hasIcon", "sd");
				}
			} else if (desiredOperation.getProcedureName().equals("LOCK_CARD")) {
				CustomerCardInfo cci = customerCardInfoManager.load(desiredOperation.getCustomerCardId());
				mappedDesiredOperation.put("appName", "");
				if (null != cci) {
					mappedDesiredOperation.put("cciIconId", cci.getMobileType().getId());
					mappedDesiredOperation.put("hasIcon", "LOCK_CARD");
				}
			} else if (desiredOperation.getProcedureName().equals("UNLOCK_CARD")) {
				CustomerCardInfo cci = customerCardInfoManager.load(desiredOperation.getCustomerCardId());
				mappedDesiredOperation.put("appName", "");
				if (null != cci) {
					mappedDesiredOperation.put("cciIconId", cci.getMobileType().getId());
					mappedDesiredOperation.put("hasIcon", "UNLOCK_CARD");
				}
			} else {
				apps = applicationManager.find(propertyFilters);
				if (apps != null && apps.size() != 0) {
					mappedDesiredOperation.put("appName", apps.get(0).getName());
					mappedDesiredOperation.put("application_id", apps.get(0).getId());
					mappedDesiredOperation.put("hasIcon", apps.get(0).getPcIcon() != null);
					boolean hasClient = false;
					List<ApplicationVersion> versions = apps.get(0).getVersions();
					for (ApplicationVersion av :  versions){
						if (av.getClients().size() != 0){
							hasClient = true;
							break;
						}
					}
					mappedDesiredOperation.put("hasClient", hasClient);
				}
			}
			mappedApplications.add(mappedDesiredOperation);
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (Integer.valueOf(executionStatus) != 0) {
				Collections.sort(mappedApplications, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> m1, Map<String, Object> m2) {
						if (StringUtils.isNotBlank((String) m1.get("beginTime")) && StringUtils.isNotBlank((String) m2.get("beginTime"))) {
							try {
								Date date1 = sdf.parse((String) m1.get("beginTime"));
								Date date2 = sdf.parse((String) m2.get("beginTime"));
								return 0 - date1.compareTo(date2);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							return 0;
						} else {
							return 0;
						}
					}

				});
			}
		}
		return mappedApplications;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult cancel(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			String id = ServletRequestUtils.getStringParameter(request, "id");
			desiredOperationManager.remove(Long.parseLong(id));
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
	JsonResult execute(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			String ids = ServletRequestUtils.getStringParameter(request, "ids");
			localTransactionManager.changeStatus(ids, "1");
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

	private List<Map<String, Object>> localTransactionResult(List<LocalTransaction> requistionList) {
		List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>(requistionList.size());
		List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();
		List<Application> apps = new ArrayList<Application>();
		List<SecurityDomain> sds = new ArrayList<SecurityDomain>();
		for (LocalTransaction localTransaction : requistionList) {
			propertyFilters.clear();
			apps.clear();
			sds.clear();
			Map<String, Object> mappedLocalTransaction = localTransaction.toMap(null, null);
			String formatValue = "";
			if (localTransaction.getBeginTime() != null) {
				formatValue = DateFormatUtils.format((Calendar) localTransaction.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
			}
			mappedLocalTransaction.put("beginTime", formatValue);
			formatValue = "";
			if (localTransaction.getEndTime() != null) {
				formatValue = DateFormatUtils.format((Calendar) localTransaction.getEndTime(), "yyyy-MM-dd HH:mm:ss");
			}
			mappedLocalTransaction.put("endTime", formatValue);
			mappedLocalTransaction.put("failMessage", localTransaction.getFailMessage() == null ? "" : localTransaction.getFailMessage());
			// propertyFilters.add(new PropertyFilter("aid", MatchType.EQ,
			// PropertyType.S, localTransaction.getAid()));
			if (localTransaction.getProcedureName().equals(Operation.CREATE_SD.toString())
					|| localTransaction.getProcedureName().equals(Operation.DELETE_SD.toString())
					|| localTransaction.getProcedureName().equals(Operation.LOCK_SD.toString())
					|| localTransaction.getProcedureName().equals(Operation.UNLOCK_SD.toString())
					|| localTransaction.getProcedureName().equals(Operation.SYNC_CARD_SD.toString())
					|| localTransaction.getProcedureName().equals(Operation.UPDATE_KEY.toString())) {
				propertyFilters.add(new PropertyFilter("aid", MatchType.EQ, PropertyType.S, localTransaction.getAid()));
				sds = securityDomainManager.find(propertyFilters);
				if (sds != null && sds.size() != 0) {
					mappedLocalTransaction.put("appName", sds.get(0).getSdName());
					mappedLocalTransaction.put("showType", "sd");
				}
			} else if (localTransaction.getProcedureName().equals("LOCK_CARD") || localTransaction.getProcedureName().equals("UNLOCK_CARD")) {
				propertyFilters.add(new PropertyFilter("mobileNo", MatchType.EQ, PropertyType.S, localTransaction.getMobileNo()));
				List<CustomerCardInfo> ccis = this.customerCardInfoManager.find(propertyFilters);
				CustomerCardInfo cci = null;
				for (CustomerCardInfo _cci : ccis) {
					if (_cci.getCard().getCardNo().equals(localTransaction.getCardNo())) { // 根据卡号和手机号查找CustomerCardInfo，并且列出最新的终端名
						if (cci == null || cci.getId() < _cci.getId()) {
							cci = _cci;
						}
					}
				}
				if (null != cci) {
					mappedLocalTransaction.put("appName", cci.getName());
					mappedLocalTransaction.put("cciIconId", cci.getMobileType().getId());
					mappedLocalTransaction.put("showType", "termial");
				}
			} else {
				propertyFilters.add(new PropertyFilter("aid", MatchType.EQ, PropertyType.S, localTransaction.getAid()));
				apps = applicationManager.find(propertyFilters);
				if (apps != null && apps.size() != 0) {
					mappedLocalTransaction.put("appName", apps.get(0).getName());
					mappedLocalTransaction.put("application_id", apps.get(0).getId());
					mappedLocalTransaction.put("showType", "app");
				}
			}
			if (PlatformMessage.SUCCESS.getCode().equals(localTransaction.getResult())) {
				mappedLocalTransaction.put("result", PlatformMessage.SUCCESS.getMessage());
			} else if (PlatformMessage.TRANS_EXCESSIVING.getCode().equals(localTransaction.getResult())) {
				mappedLocalTransaction.put("result", PlatformMessage.TRANS_EXCESSIVING.getMessage());
			} else if (PlatformMessage.TRANS_EXCEPTION_CLOSED.getCode().equals(localTransaction.getResult())) {
				mappedLocalTransaction.put("result", PlatformMessage.TRANS_EXCEPTION_CLOSED.getMessage());
			} else {
//				for (PlatformErrorCode pe : PlatformErrorCode.values()) {
//					if (pe.getErrorCode().equals(localTransaction.getResult())) {
//						mappedLocalTransaction.put("result", pe.getDefaultMessage());
//					}
//				}
				mappedLocalTransaction.put("result",localTransaction.getFailMessage());
			}
			mappedApplications.add(mappedLocalTransaction);
		}
		return mappedApplications;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage exportConstant() {
		JsonMessage message = new JsonMessage();

		Map<String, Object> constant = new HashMap<String, Object>();

		for (Operation operation : LocalTransaction.Operation.values()) {
			if ("".equals(operation.getCommandId())) {// 如果commandID没有，使用Type作为操作标识
				constant.put(operation.name(), operation.getType());
			} else {// 否则，使用commandID作为操作标识
				constant.put(operation.name(), operation.getCommandId());
			}
		}

		for (CommType commType : LocalTransaction.CommType.values()) {
			constant.put(commType.name(), commType.name());
		}

		message.setMessage(constant);
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage checkCardOptFinish(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String sessionId = ServletRequestUtils.getStringParameter(request, "sessionId");
			boolean flag = localTransactionManager.checkCardOptFinish(sessionId);
			if (!flag) {
				message.setSuccess(flag);
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
}

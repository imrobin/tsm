package com.justinmobile.tsm.application.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.justinmobile.core.dao.support.PropertyFilter.JoinType;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;

@Controller()
@RequestMapping("/appVer/")
public class ApplicationVersionController {

	@Autowired
	ApplicationVersionManager applicationVersionManager;

	@Autowired
	CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	ApplicationManager appManager;
	
	@Autowired
	SysUserManager sysUserManager;

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<ApplicationVersion> page = SpringMVCUtils.getPage(request);
			boolean local = ServletRequestUtils.getBooleanParameter(request, "local",false); // 是否本地应用
			SysUser currentUser = sysUserManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
			if (local && currentUser != null && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())){
				PropertyFilter pf = new PropertyFilter("application", JoinType.L, "location", MatchType.EQ, PropertyType.S, currentUser.getProvince());
				filters.add(pf);
			}
			page = applicationVersionManager.findPage(page, filters);
			result.setPage(page, null, "application.id application.name application.deleteRule application.personalType");
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
	JsonResult indexWithSp(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationVersion> page = SpringMVCUtils.getPage(request);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			String status = ServletRequestUtils.getStringParameter(request, "status");
			if(!StringUtils.isBlank(status)) {
				queryParams.put("status", status);
			}
			String spSelf = ServletRequestUtils.getStringParameter(request, "sp");
			if(!StringUtils.isBlank(spSelf)) {
				String username = SpringSecurityUtils.getCurrentUserName();
				SysUser user = sysUserManager.getUserByName(username);
				queryParams.put("sp", user.getId());
			}
			page = applicationVersionManager.findPageBySp(page, queryParams);
			result.setPage(page, null, "application.id application.name");
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
	JsonResult listSubscribe(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			long id = ServletRequestUtils.getLongParameter(request, "id");
			ApplicationVersion appVersion = applicationVersionManager.load(id);
			page = customerCardInfoManager.getCustomerCardInfoPageByApp(page, appVersion);
			result.setPage(page, null, "customer.nickName card.cardNo");
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
	JsonResult getByAppId(HttpServletRequest request, @RequestParam Long appId, @RequestParam Integer status) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationVersion> page = SpringMVCUtils.getPage(request);
			Application app = appManager.load(appId);
			Page<ApplicationVersion> avpage = applicationVersionManager.getByAppId(app, page, status);
			result.setPage(avpage, null, "application.name");
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
	 * @Title: testAppVersion
	 * @Description: 应用测试
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage testAppVersion(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String versionIds = request.getParameter("appVerIds");
			String[] verIdsArray = versionIds.split(",");
			for (int i = 0; i < verIdsArray.length; i++) {
				applicationVersionManager.changeAppVerStatus(verIdsArray[i], ApplicationVersion.STATUS_TESTED);
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
	 * @Title: publishAppVersion
	 * @Description: 应用发布
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage publishAppVersion(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String versionIds = request.getParameter("appVerIds");
			String mobiles = request.getParameter("mobiles");
			String cardBaseInfoId = request.getParameter("cardBaseInfoId");
			applicationVersionManager.publish(versionIds, mobiles, cardBaseInfoId);
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
	JsonMessage finishAppVersion(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String versionIds = request.getParameter("appVerIds");
			applicationVersionManager.finishAppVersion(versionIds);
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
	JsonMessage getAppIdByAppverId(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String verId = request.getParameter("appVerid[0]");
			ApplicationVersion appVer = applicationVersionManager.load(Long.valueOf(verId));
			Map<String, Object> map = appVer.getApplication().toMap(null, null);
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
	JsonMessage getAidAndVerByAppverId(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String verId = request.getParameter("appverId");
			ApplicationVersion appVer = applicationVersionManager.load(Long.valueOf(verId));
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("aid", appVer.getApplication().getAid());
			map.put("ver", appVer.getVersionNo());
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
	
	
	/**
	 * @Title:archiveApp
	 * @Description: 应用归档
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage archiveApp(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String versionIds = request.getParameter("appVerIds");
			String reason = request.getParameter("reason");
			String[] verIdsArray = versionIds.split(",");
			if (StringUtils.isBlank(reason)) {
				reason = "应用归档申请";
			} 
			applicationVersionManager.archive(verIdsArray, reason);
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
	 * @Title:hasArchiveRequest
	 * @Description: 当前版本是否有归档申请
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage hasArchiveRequest(HttpServletRequest request, @RequestParam Long appVerId) {
		JsonMessage message = new JsonMessage();
		try {
			long hasArchiveRequest = applicationVersionManager.hasArchiveRequest(appVerId);
			message.setMessage(hasArchiveRequest);
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
	JsonMessage completeCreateApplicationVersion(@RequestParam Long applicationVersionId) {
		JsonMessage message = new JsonMessage();

		try {
			applicationVersionManager.completeCreateApplicationVersion(applicationVersionId);
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
	JsonMessage createVersion(@RequestParam long applicationId, @RequestParam String version) {
		JsonMessage message = new JsonMessage();
		Map<String, Object> msg = new HashMap<String, Object>(2);
		message.setSuccess(true);
		try {
			Application app = appManager.load(applicationId);
			ApplicationVersion appVer = applicationVersionManager.getAidAndVersionNo(app.getAid(), version);
			if (appVer != null) {
				throw new Exception("该应用此版本已存在");
			} else {
				appVer = new ApplicationVersion();
				appVer.setApplication(app);
				appVer.setVersionNo(version);
				applicationVersionManager.createVersion(appVer);
				msg.put("applicationVersionId", appVer.getId());
				message.setMessage(msg);
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

	@RequestMapping()
	public @ResponseBody
	JsonResult findUnLinkPage(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationVersion> page = SpringMVCUtils.getPage(request);
			String cardBaseId = request.getParameter("cardBaseId");
			page = applicationVersionManager.findUnLinkPage(page, cardBaseId);
			result.setPage(page, null, "application.name");
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

	@RequestMapping
	public @ResponseBody
	JsonMessage exportConstant() {
		JsonMessage message = new JsonMessage();

		try {
			Map<String, Integer> constant = new HashMap<String, Integer>();

			constant.put("STATUS_INIT", ApplicationVersion.STATUS_INIT);
			constant.put("STATUS_UPLOADED", ApplicationVersion.STATUS_UPLOADED);
			constant.put("STATUS_TESTED", ApplicationVersion.STATUS_TESTED);
			constant.put("STATUS_PULISHED", ApplicationVersion.STATUS_PULISHED);
			constant.put("STATUS_ARCHIVE", ApplicationVersion.STATUS_ARCHIVE);
			constant.put("STATUS_AUDITED", ApplicationVersion.STATUS_AUDITED);

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
	JsonMessage remove(@RequestParam Long applicationVersionId) {
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			ApplicationVersion applicaitionVersion = applicationVersionManager.load(applicationVersionId);

			applicationVersionManager.remove(applicaitionVersion, username);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody
	JsonMessage finishTest(@RequestParam Long appVerId) {
		JsonMessage message = new JsonMessage();
		try {
			applicationVersionManager.finishTest(appVerId);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody
	JsonMessage subReportFinishTest(HttpServletRequest request,@RequestParam Long appverId) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationVersionTestReport testReport = new ApplicationVersionTestReport();
			SpringMVCUtils.bindObject(request, testReport);
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String day = request.getParameter("day");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse(year + month + day);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			testReport.setTestDate(calendar);
			String subType = request.getParameter("subType");
			applicationVersionManager.finishTest(testReport,appverId,subType);
		}catch (PlatformException e) {
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
	JsonMessage saveReport(HttpServletRequest request,@RequestParam Long appverId,@RequestParam Long cardBaseId) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationVersionTestReport testReport = new ApplicationVersionTestReport();
			SpringMVCUtils.bindObject(request, testReport);
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String day = request.getParameter("day");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse(year + month + day);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			testReport.setTestDate(calendar);
			SysUser currentUser = sysUserManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
			testReport.setAuthor(currentUser.getUserName());
			if(StringUtils.isBlank(testReport.getResultComment())){
				if(testReport.getResult().intValue() == ApplicationVersionTestReport.RESULT_PASS){
					testReport.setResultComment("通过");
				}else  if(testReport.getResult().intValue() == ApplicationVersionTestReport.RESULT_UNPASS){
					testReport.setResultComment("不通过");
				}
			}
			applicationVersionManager.saveReport(testReport,appverId,cardBaseId);
		}catch (PlatformException e) {
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
	JsonResult getDownTestFileAppver(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationVersion> page = SpringMVCUtils.getPage(request);
			String appName = request.getParameter("appName");
			page = applicationVersionManager.getDownTestFileAppver(page,appName);
			result.setPage(page, null, "application.id application.name application.deleteRule application.personalType");
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

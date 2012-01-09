package com.justinmobile.tsm.system.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Controller("requistionController")
@RequestMapping("/requistion/")
public class RequistionController {
	@Autowired
	private RequistionManager requistionManager;
	@Autowired
	private ApplicationVersionManager applicationVersionManager;
	/**
	 * 默认列表，支持过滤参数，应用下载状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Requistion> page = SpringMVCUtils.getPage(request);
			String appName = ServletRequestUtils.getStringParameter(request, "appName");
			String status = ServletRequestUtils.getStringParameter(request, "search_EQI_status");
			String orderBy = ServletRequestUtils.getStringParameter(request, "page_orderBy");
			String id = ServletRequestUtils.getStringParameter(request, "id");
			String spId = ServletRequestUtils.getStringParameter(request, "spId");
			Map<String, String> paramMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(appName)) {
				paramMap.put("appName", "%" + appName + "%");
			}
			if (!StringUtils.isEmpty(id)) {
				paramMap.put("id", id);
			}
			if (!StringUtils.isEmpty(status)) {
				paramMap.put("status", status);
			}
			if (!StringUtils.isEmpty(orderBy)) {
				paramMap.put("orderBy", orderBy);
			}
			if (!StringUtils.isEmpty(spId)) {
				paramMap.put("spId", spId);
			}
			paramMap.put("types", "("+Requistion.TYPE_APP_UPLOAD+","+Requistion.TYPE_APP_ARCHIVE+")");
			page = requistionManager.findPageByParam(page, paramMap);
			List<Requistion> requistionList = page.getResult();
			List<Map<String, Object>> requistions = requistionListResult(requistionList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(requistions);
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

	private List<Map<String, Object>> requistionListResult(List<Requistion> requistionList) {
		List<Map<String, Object>> requistions = new ArrayList<Map<String, Object>>(requistionList.size());
		for (Requistion requistion : requistionList) {
			Map<String, Object> mappedRequistion = requistion.toMap(null, null);
			ApplicationVersion appVersion = applicationVersionManager.load(requistion.getOriginalId());
			mappedRequistion.put("appName", appVersion.getApplication() == null ? "" : appVersion.getApplication().getName());
			mappedRequistion.put("appAid", appVersion.getApplication() == null ? "" : appVersion.getApplication().getAid());
			mappedRequistion.put("appDeleteRule", appVersion.getApplication().getDeleteRule());
			mappedRequistion.put("appId", appVersion.getApplication().getId());
			mappedRequistion.put("appPersonalType", appVersion.getApplication().getPersonalType());
			mappedRequistion.put("versionNo", appVersion.getVersionNo());
			mappedRequistion.put("appStatus", appVersion.getApplication().getStatus());
			if (appVersion.getApplication().getPersoCmdTransferSecureAlgorithm() != null){
				mappedRequistion.put("persoCmdTransferSecureAlgorithm",
						SecureAlgorithm.I18N_CONFIG.get(appVersion.getApplication().getPersoCmdTransferSecureAlgorithm().name()));
			}else{
				mappedRequistion.put("persoCmdTransferSecureAlgorithm","");
			}
			
			if (appVersion.getApplication().getPersoCmdSensitiveDataSecureAlgorithm() != null){
				mappedRequistion.put("persoCmdSensitiveDataSecureAlgorithm",
						SecureAlgorithm.I18N_CONFIG.get(appVersion.getApplication().getPersoCmdSensitiveDataSecureAlgorithm().name()));
			}else{
				mappedRequistion.put("persoCmdSensitiveDataSecureAlgorithm","");
			}
			Set<ApplicationLoadFile> applicationLoadFiles = appVersion.getApplicationLoadFiles();
			StringBuffer loadFileNameBuffer = new StringBuffer();
			String loadFileNames = "";
			StringBuffer loadFileIdBuffer = new StringBuffer();
			String loadFileIds = "";

			for(Iterator<ApplicationLoadFile> it = applicationLoadFiles.iterator(); it.hasNext(); ) {
				ApplicationLoadFile applicationLoadFile= (ApplicationLoadFile)it.next();
				loadFileNameBuffer.append(applicationLoadFile.getLoadFileVersion().getLoadFile().getName()+",");
				loadFileIdBuffer.append(applicationLoadFile.getLoadFileVersion().getLoadFile().getId()+",");
				if (!it.hasNext()){
					loadFileNames = loadFileNameBuffer.toString().substring(0,loadFileNameBuffer.toString().length()-1);
					loadFileIds = loadFileIdBuffer.toString().substring(0,loadFileIdBuffer.toString().length()-1);
				}
			}
			mappedRequistion.put("loadFileName", loadFileNames);
			mappedRequistion.put("loadFileIds", loadFileIds);
			requistions.add(mappedRequistion);
		}
		return requistions;
	}

	/**
	 * 
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage updatePublish(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String status = ServletRequestUtils.getStringParameter(request, "statusOriginal", "");
			int typeOriginal = ServletRequestUtils.getIntParameter(request, "typeOriginal", 0);
			String sdIdsStr = ServletRequestUtils.getStringParameter(request, "sdidStr", "");
			String opinion = ServletRequestUtils.getStringParameter(request, "opinion", "");
			String typeTk = ServletRequestUtils.getStringParameter(request, "typeTk", "");
			String typeKek = ServletRequestUtils.getStringParameter(request, "typeKek", "");
			String hsmkeyConfigTK = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigTK", "");
			String hsmkeyConfigKEK = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigKEK", "");
//			String tkVendor = ServletRequestUtils.getStringParameter(request, "tkVendor", "");
//			String kekVendor = ServletRequestUtils.getStringParameter(request, "kekVendor", "");
			
			Requistion ac = requistionManager.load(ServletRequestUtils.getLongParameter(request, "id",0));
			message = requistionManager.updatePublish(status, typeOriginal, sdIdsStr, opinion, ac, typeTk, typeKek,
					hsmkeyConfigTK, hsmkeyConfigKEK);
			if (!message.getSuccess()){
				return message;
			}
			requistionManager.saveOrUpdate(ac);
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
	public @ResponseBody JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		
		try {
			Page<Requistion> page = SpringMVCUtils.getPage(request);
			String orderBy = ServletRequestUtils.getStringParameter(request, "page_orderBy");
			if(org.apache.commons.lang.StringUtils.isBlank(orderBy)) {
				page.setOrderBy("reviewDate");
				page.setOrder(Page.DESC);
			}
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			//&page_orderBy=reviewDate_desc
			
			page = requistionManager.findPage(page, filters);
			result.setPage(page, null, null);
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
	JsonMessage remove(HttpServletRequest request, @RequestParam Long requistionId) {
		JsonMessage message = new JsonMessage();
		try {
			requistionManager.remove(requistionId);
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

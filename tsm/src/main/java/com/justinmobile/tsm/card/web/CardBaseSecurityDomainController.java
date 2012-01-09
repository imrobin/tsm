/**
 * CardBaseApplicationController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;

@Controller()
@RequestMapping("/cardbasesecurity/")
public class CardBaseSecurityDomainController {
	
	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;
	
	@RequestMapping()
	public @ResponseBody JsonResult Index(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CardBaseSecurityDomain> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = cardBaseSecurityDomainManager.findPage(page, filters);
			result.setPage(page,null,"cardBaseInfo.name cardBaseInfo.batchNo securityDomain.sdName securityDomain.aid");
		} catch (PlatformException pe){
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping()
	public @ResponseBody JsonResult doLink(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			String cardBaseid = request.getParameter("cardBaseId");
			String sdId = request.getParameter("sdId");
			String preset = request.getParameter("preset");
			String presetKeyVersion = request.getParameter("presetKeyVersion");
			String presetMode = request.getParameter("presetMode");
			cardBaseSecurityDomainManager.doLink(cardBaseid,sdId,preset,presetKeyVersion,presetMode);
		} catch (PlatformException pe){
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping()
	public @ResponseBody JsonResult delLink(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			String cbsddId = request.getParameter("cbsdId");
			cardBaseSecurityDomainManager.delLink(cbsddId);
		} catch (PlatformException pe){
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping()
	public @ResponseBody JsonMessage checkSDisISD(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		try {
			String cbsdId = request.getParameter("cbsdId");
			message.setSuccess(cardBaseSecurityDomainManager.checkSDisISD(cbsdId));
		} catch (PlatformException pe){
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	
	@RequestMapping()
	public @ResponseBody JsonMessage getCBSD(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		try {
			String cbsddId = request.getParameter("cbsdId");
			CardBaseSecurityDomain cbsd = cardBaseSecurityDomainManager.load(Long.valueOf(cbsddId));
			Map<String, Object> map = cbsd.toMap(null, "securityDomain.id securityDomain.sdName cardBaseInfo.id");
			message.setMessage(map);
		} catch (PlatformException pe){
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
	
	@RequestMapping()
	public @ResponseBody JsonMessage changePrest(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		try {
			String cbsdId = request.getParameter("cbSdId");
			String sdId = request.getParameter("sdId");
			String preset = request.getParameter("preset");
			String presetKeyVersion = request.getParameter("presetKeyVersion");
			String presetMode = request.getParameter("presetMode");
			cardBaseSecurityDomainManager.changePrest(cbsdId,sdId,preset,presetKeyVersion,presetMode);
		} catch (PlatformException pe){
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}
}


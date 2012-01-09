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
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;


/**
 * @ClassName: CardBaseApplicationController
 * @Description:卡片预制关联类
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardbaseapp/")
public class CardBaseApplicationController {
		
	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;
	
	
	@RequestMapping()
	public @ResponseBody JsonResult Index(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CardBaseApplication> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = cardBaseApplicationManager.findPage(page, filters);
			result.setPage(page,null,"cardBase.name cardBase.batchNo applicationVersion.application.name applicationVersion.versionNo");
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
			String cardIdss = request.getParameter("cardBaseIds");
			String appVer = request.getParameter("appVerIds");
			String[] cardids = cardIdss.split(",");
			String[] appVerS = appVer.split(",");
			cardBaseApplicationManager.doLink(appVerS,cardids);
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
	public @ResponseBody JsonResult cardBaseDoLink(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			String cardId = request.getParameter("cardbaseId[0]");
			String appVer = request.getParameter("appverids");
			String[] appVerIdS = appVer.split(",");
			cardBaseApplicationManager.cardBaseDoLink(appVerIdS,cardId);
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
			String cbaId = request.getParameter("cbaId");
			cardBaseApplicationManager.delLink(cbaId);
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
	public @ResponseBody JsonMessage getCBA(HttpServletRequest request){
		JsonMessage message = new JsonMessage();
		try {
			String cbaId = request.getParameter("cbaId");
			CardBaseApplication cba = cardBaseApplicationManager.load(Long.valueOf(cbaId));
			Map<String, Object> map = cba.toMap(null, null);
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
			String cbaId = request.getParameter("cbaId");
			cardBaseApplicationManager.changePrest(cbaId);
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


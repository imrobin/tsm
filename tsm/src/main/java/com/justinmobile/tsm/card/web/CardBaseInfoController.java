/**
 * CardBaseInfoController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;


/**
 * @ClassName: CardBaseController
 * @Description:卡片批次
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardbaseinfo/")
public class CardBaseInfoController {
		
	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult index(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CardBaseInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = cardBaseInfoManager.findPage(page, filters);
			result.setPage(page,null,null);
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
	
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult add(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			CardBaseInfo cbi = new CardBaseInfo();
			BindingResult resut = SpringMVCUtils.bindObject(request, cbi);
			if(!resut.hasErrors()){
				cardBaseInfoManager.addCardBaseInfo(cbi);
			}else{
				result.setSuccess(false);
				result.setMessage("添加失败,请重试.");
			}
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
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult modify(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			String oldId = request.getParameter("id");
			CardBaseInfo cbi = new CardBaseInfo();
			BindingResult resut = SpringMVCUtils.bindObject(request, cbi);
			if(!resut.hasErrors()){
				cardBaseInfoManager.modifyCardBaseInfo(oldId,cbi);
			}else{
				result.setSuccess(false);
				result.setMessage("添加失败,请重试.");
			}
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
	
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonMessage getCardBase(HttpServletRequest request,@RequestParam Long cbiId){
		JsonMessage result = new JsonMessage();
		try {
			CardBaseInfo cardBaseInfo = cardBaseInfoManager.load(cbiId);
			 Map<String, Object> map = cardBaseInfo.toMap(null,null);
			 boolean flag = cardBaseInfoManager.checkPublishCard(cardBaseInfo);
			 map.put("publishCard", flag);
			 result.setMessage(map);
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
	
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult remove(HttpServletRequest request,@RequestParam Long cbiId){
		JsonResult result = new JsonResult();
		try {
			cardBaseInfoManager.checkRemove(cbiId);
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
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonResult findTestedCardBase(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationVersionTestReport> page = SpringMVCUtils.getPage(request);
			String appVerId = request.getParameter("appVerId");
			List<Map<String,Object>> mapList = cardBaseInfoManager.findTestedCardBase(page, appVerId);
			result.setResult(mapList);
			result.setTotalCount(mapList.size());
			result.setTotalPage(mapList.size()/page.getPageSize() + 1);
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
}


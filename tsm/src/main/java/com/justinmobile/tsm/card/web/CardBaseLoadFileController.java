/**
 * CardBaseApplicationController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;
import com.justinmobile.tsm.card.manager.CardBaseLoadFileManager;

@Controller()
@RequestMapping("/cardbaseloadfile/")
public class CardBaseLoadFileController {
	
	@Autowired
	private CardBaseLoadFileManager cardBaseLoadFileManager;
	
	@RequestMapping()
	public @ResponseBody JsonResult Index(HttpServletRequest request){
		JsonResult result = new JsonResult();
		try {
			Page<CardBaseLoadFile> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = cardBaseLoadFileManager.findPage(page, filters);
			result.setPage(page,null,"cardBaseInfo.name cardBaseInfo.batchNo loadFileVersion.versionNo loadFileVersion.loadFile.name");
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
			String cardid = request.getParameter("cardbaseId[0]");
			String loadfileIds = request.getParameter("loadfileVerIds");
			cardBaseLoadFileManager.doLink(cardid,loadfileIds);
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
			String cbldId = request.getParameter("cbld");
			cardBaseLoadFileManager.delLink(cbldId);
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


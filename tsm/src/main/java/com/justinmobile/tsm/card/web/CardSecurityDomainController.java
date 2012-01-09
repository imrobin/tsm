/**
 * CardApplicationController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;


/**
 * @ClassName: CardSecurityDomainController
 * @Description:终端安全域操作类	
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardSd/")
public class CardSecurityDomainController {
		
	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;
	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	@RequestMapping()
	public @ResponseBody JsonMessage getSdByCci(HttpServletRequest request,@RequestParam Long ccid){
		JsonMessage message = new JsonMessage();
		try {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			CustomerCardInfo cci = customerCardInfoManager.load(ccid);
			CardSecurityDomain csd = cardSecurityDomainManager.getISdByCci(cci);
			if(null != csd && csd.getStatus().intValue() == CardSecurityDomain.STATUS_LOCK){
				resultMap.put("card", 1);
				resultMap.put("cardNo", cci.getCard().getCardNo());
				resultMap.put("aid", csd.getSd().getAid());
			}else{
				resultMap.put("card",0);
				resultMap.put("cardNo", cci.getCard().getCardNo());
			}
			message.setMessage(resultMap);
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
	public @ResponseBody JsonMessage checkAndGetDelAppForDelSd(HttpServletRequest request,@RequestParam String cardNo,@RequestParam String sdId){
		JsonMessage message = new JsonMessage();
		try {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			cardSecurityDomainManager.checkAndGetDelAppForDelSd(resultMap,cardNo,sdId);
			message.setMessage(resultMap);
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
	public @ResponseBody JsonMessage checkDel(HttpServletRequest request,@RequestParam String cardNo,@RequestParam String sdId){
		JsonMessage message = new JsonMessage();
		try {
			cardSecurityDomainManager.checkDel(cardNo,sdId);
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


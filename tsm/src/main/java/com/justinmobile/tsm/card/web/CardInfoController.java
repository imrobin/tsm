/**
 * CardBaseInfoController
 *
 * Copyright 2011 JustinMobile, Inc. All rights reserved.
 */
package com.justinmobile.tsm.card.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardInfoManager;


/**
 * @ClassName: CardBaseController
 * @Description:卡片批次
 * @author liqiang.wang
 * @date 2011-4-29 上午09:52:23
 * 
 */
@Controller()
@RequestMapping("/cardinfo/")
public class CardInfoController {
		
	@Autowired
	private CardInfoManager cardInfoManager;
	
	/**
	* @param request
	* @return 
	*/
	@RequestMapping()
	public @ResponseBody JsonMessage checkCard(HttpServletRequest request,@RequestParam String cardNo){
		JsonMessage result = new JsonMessage();
		try {
			cardInfoManager.checkCard(cardNo);
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
	public @ResponseBody JsonMessage getCardInfoByCardNo(HttpServletRequest request,@RequestParam String cardNo){
		JsonMessage message = new JsonMessage();
		try {
			CardInfo card = cardInfoManager.getByCardNo(cardNo);
			if(null == card){
				message.setSuccess(false);
				message.setMessage("此终端芯片不被支持");
			}else{
				message.setMessage(card.toMap(null, null));
			}
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


package com.justinmobile.tsm.transaction.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.endpoint.webservice.MobileWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqAppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqGetApplicationInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqListComment;

@Controller
@RequestMapping("/mobileWS/")
public class MobileController {

	@Autowired
	private MobileWebService mobileWebService;

//	@RequestMapping
//	public @ResponseBody JsonMessage login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
//		JsonMessage message = new JsonMessage();
//		message.setMessage(mobileWebService.login(userName, password));
//		return message;
//
//	}

	@RequestMapping
	public @ResponseBody JsonMessage listApplication(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ReqApplicationList reqApplicationList = new ReqApplicationList();
		try {
			BindingResult result = SpringMVCUtils.bindObject(request, reqApplicationList);
			if (result.hasErrors()) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(SpringMVCUtils.buildErrorMessage(result));
			} else {
				message.setMessage(mobileWebService.listApplication(reqApplicationList));
			}
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage getApplicationInfo(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ReqGetApplicationInfo reqGetApplicationInfo = new ReqGetApplicationInfo();
		try {
			BindingResult result = SpringMVCUtils.bindObject(request, reqGetApplicationInfo);
			if (result.hasErrors()) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(SpringMVCUtils.buildErrorMessage(result));
			} else {
				message.setMessage(mobileWebService.getInfo(reqGetApplicationInfo));
			}
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage execAPDU(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ReqExecAPDU reqExecAPDU = new ReqExecAPDU();
		try {
			BindingResult result = SpringMVCUtils.bindObject(request, reqExecAPDU);
			if (result.hasErrors()) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(SpringMVCUtils.buildErrorMessage(result));
			} else {
				message.setMessage(mobileWebService.execAPDU(reqExecAPDU));
			}
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage postAppComment(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ReqAppComment reqAppComment = new ReqAppComment();
		try {
			BindingResult result = SpringMVCUtils.bindObject(request, reqAppComment);
			if (result.hasErrors()) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(SpringMVCUtils.buildErrorMessage(result));
			} else {
				message.setMessage(mobileWebService.postAppComment(reqAppComment));
			}
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage listComment(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ReqListComment reqListComment = new ReqListComment();
		try {
			BindingResult result = SpringMVCUtils.bindObject(request, reqListComment);
			if (result.hasErrors()) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(SpringMVCUtils.buildErrorMessage(result));
			} else {
				//message.setMessage(mobileWebService.listComment(reqListComment));
			}
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

}

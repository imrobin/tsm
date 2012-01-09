package com.justinmobile.tsm.transaction.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;

@Controller
@RequestMapping("/dersireOpt/")
public class DesiredOperationController {

	@Autowired
	private DesiredOperationManager desiredOperationManager;
	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@RequestMapping
	public @ResponseBody JsonMessage createDO(HttpServletRequest request,@RequestParam("aid") String aid, @RequestParam("opttype") String opttype) {
		JsonMessage message = new JsonMessage();
		try {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			String ccid = request.getParameter("ccid");
			String cardNo = request.getParameter("cardNo");
			String userName = SpringSecurityUtils.getCurrentUserName();
			if(StringUtils.isBlank(userName)){
				CustomerCardInfo customerCardInfo = customerCardInfoManager.getByCardNo(cardNo);
				SysUser user = customerCardInfo.getCustomer().getSysUser();
				userName = user.getUserName();
				ccid = String.valueOf(customerCardInfo.getId());
			}
			DesiredOperation desiredOpt = desiredOperationManager.createDO(aid,opttype,userName,ccid,cardNo);
			if(null!=desiredOpt){
				resultMap.put("id",String.valueOf(desiredOpt.getId()));
				message.setMessage(resultMap);
			}else{
				message.setSuccess(false);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage("当前用户未登录");
		}
		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage getDoInfo(@RequestParam("doId") Long doId) {
		JsonMessage message = new JsonMessage();
		try {
			DesiredOperation doInfo = desiredOperationManager.load(doId);
			Map<String, Object> resultMap = doInfo.toMap(null, null);
			resultMap.put("procedureInt", LocalTransaction.Operation.valueOf(doInfo.getProcedureName()).getType());
			if(null != doInfo.getCustomerCardId()){
				CustomerCardInfo cci = customerCardInfoManager.load(doInfo.getCustomerCardId());
				if(null!=cci){
					resultMap.put("cardNo", cci.getCard().getCardNo());
				}
			}
			message.setMessage(resultMap);
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
	public @ResponseBody JsonMessage changeDesiredOperation(HttpServletRequest request,@RequestParam("doId") Long doId,@RequestParam("sessionId") String sessionId,@RequestParam("flag") int flag,@RequestParam("result") String result,@RequestParam("cardNo") String cardNo) {
		JsonMessage message = new JsonMessage();
		try {
			String phone = request.getParameter("phone");
			if(StringUtils.isNotBlank(phone)){
				result = new String(result.getBytes("iso-8859-1"),"utf-8");
			}
			desiredOperationManager.change(doId, sessionId, flag,result,cardNo);
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
	public @ResponseBody JsonMessage setCCiUseCardNo(HttpServletRequest request,@RequestParam("cardNo") String cardNo ,@RequestParam("doIds") String doIds) {
		JsonMessage message = new JsonMessage();
		try {
			CustomerCardInfo cci = customerCardInfoManager.getByCardNoCancelAndReplaced(cardNo);
			if(null != cci){
				desiredOperationManager.setCustomerCardInfo(doIds, cci);
			}else{
				throw new PlatformException(PlatformErrorCode.OPERATION_NOT_BELONG_THIS_TERMINAL);
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
	
	@RequestMapping
	public @ResponseBody JsonMessage getBatExecuteResult(HttpServletRequest request,@RequestParam("doIds") String doIds) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage("任务执行成功");
			String[] doIdsArray = doIds.split(",");
			for(int i = 0; i < doIdsArray.length; i++){
				DesiredOperation desiredOperation = desiredOperationManager.load(Long.valueOf(doIdsArray[i]));
				if (desiredOperation.getIsExcuted().intValue() == DesiredOperation.NOT_FINISH_EXCUTED){
					message.setMessage("存在执行失败的任务，请到未完成任务中查看原因");
					break;
				}
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
	
	@RequestMapping
	public @ResponseBody JsonResult getBatchDoInfo(@RequestParam("doIds") String doIds) {
		JsonResult result = new JsonResult();
		try {
			List<Map<String,Object>> reslutMapList = new ArrayList<Map<String,Object>>();
			String[] doIdsArray = doIds.split(",");
			StringBuffer downloadIds = new StringBuffer();
			for(int i = 0; i < doIdsArray.length; i++){
				Map<String,Object> resutlMap = new HashMap<String,Object>();
				DesiredOperation desiredOperation = desiredOperationManager.load(Long.valueOf(doIdsArray[i]));
				resutlMap.put("aid", desiredOperation.getAid());
				resutlMap.put("operation",LocalTransaction.Operation.valueOf(desiredOperation.getProcedureName()).getType());
				if (desiredOperation.getProcedureName().equals(Operation.DOWNLOAD_APP.toString())){
					downloadIds.append(desiredOperation.getId()+",");
				}
				reslutMapList.add(resutlMap);
			}
			result.setResult(reslutMapList);
			if (downloadIds.length() != 0) {
				result.setMessage(downloadIds.toString().substring(0, downloadIds.toString().length() - 1));
			} else {
				result.setMessage("");
			}
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
	public @ResponseBody JsonMessage getDoIdByAidAndOpt(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String aid = request.getParameter("aid");
			String opt = request.getParameter("opt");
			String cardNo = request.getParameter("cardNo");
			DesiredOperation desiredOperation = desiredOperationManager.getDoIdByAidAndOpt(aid,opt,cardNo);
			if(null !=  desiredOperation){
				message.setSuccess(false);
			}
			message.setMessage(desiredOperation.getId());
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

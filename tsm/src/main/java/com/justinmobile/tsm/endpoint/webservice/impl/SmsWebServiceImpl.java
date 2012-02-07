package com.justinmobile.tsm.endpoint.webservice.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.SmsWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@WebService(serviceName = "SmsWebService", targetNamespace = NameSpace.CM, portName = "SmsWebServiceHttpPort")
@Service("smsService")
public class SmsWebServiceImpl implements SmsWebService {

	@Autowired
	private CardInfoManager cardManager;

	@Override
	public Status hanldeMessage(String content, String mobileNo) {
		  Status status = new Status();
		  content = new String(ConvertUtils.hexString2ByteArray(content));
	      String command = content.substring(0,4);
	      if(command.equalsIgnoreCase("1001")){
	    	 register(status,content,mobileNo);
	      }
	      return status;
	}
	private Status register(Status status,String content,String mobileNo){
		try{
		String seId = content.substring(4, 24);
		String imsi = content.substring(24, 64);
		String challengeNo = content.substring(64, 70);
		// 如果卡未注册，注册卡
		CardInfo card = cardManager.buildCardInfoIfNotExist(seId);

		// ismi不用但手机号相同，换卡不换号
		if(imsi.equals(card.getImsi())){
			card.setRegisterable(CardInfo.REGISTERABLE_LOGIN);
		}
		else if ( mobileNo.equals(card.getMobileNo())) {
			card.setRegisterable(CardInfo.REGISTERABLE_CHANGE_SIM);
		} else {
			card.setRegisterable(CardInfo.REGISTERABLE_NEW);
		}
		card.setImsi(imsi);
		card.setMobileNo(mobileNo);
		card.setChallengeNo(challengeNo);
		cardManager.generateToken(card);
		}catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}
		return status;
	}

}

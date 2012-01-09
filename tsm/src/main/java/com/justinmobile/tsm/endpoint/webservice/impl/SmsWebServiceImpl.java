package com.justinmobile.tsm.endpoint.webservice.impl;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
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
		try {
			String seId = content.substring(0, 20);
			String imsi = content.substring(20, 38);
			String challengeNo = content.substring(38, 44);

			// 如果卡未注册，注册卡
			CardInfo card = cardManager.buildCardInfoIfNotExist(seId);

			// ismi不用但手机号相同，换卡不换号
			if (!imsi.equals(card.getImsi()) && mobileNo.equals(card.getMobileNo())) {
				card.setRegisterable(CardInfo.REGISTERABLE_CHANGE_SIM);
			} else {
				card.setRegisterable(CardInfo.REGISTERABLE_NEW);
			}
			card.setImsi(imsi);
			card.setMobileNo(mobileNo);
			card.setChallengeNo(challengeNo);

			cardManager.generateToken(card);
		} catch (PlatformException e) {
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

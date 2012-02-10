package com.justinmobile.tsm.endpoint;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardAppletManager;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Controller("TestWebServiceControler")
@RequestMapping("/test/")
public class TestWebServiceController {
	@Autowired
	private CardLoadFileManager cardLoadfileManager;
	@Autowired
	private CardAppletManager cardAppletManager;
	@Autowired
	private CardInfoManager cardInfoManager;
	@Autowired
	private CardApplicationManager caManager;
	@Autowired
	private CardSecurityDomainManager csdManager;
    
	@RequestMapping
	public @ResponseBody
	JsonMessage subscribe(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String aidName = ServletRequestUtils.getStringParameter(request, "aidName");
			String[] s = aidName.split("-");
			String appAid = s[0];
			String msisdn = ServletRequestUtils.getStringParameter(request, "mobileNo");
			String seId = ServletRequestUtils.getStringParameter(request, "cardNo");
			Integer eventId = ServletRequestUtils.getIntParameter(request, "eventId");
			String seq = System.currentTimeMillis() + "";
			Holder<String> seqNum = new Holder<String>(seq);
			String sessionId = seq;
			Holder<String> timeStamp = new Holder<String>(seq);
			Integer commType = CommType.GPC.getType();
			Holder<Status> status = new Holder<Status>(new Status());
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(ProviderCallTsmWebService.class);
			factory.setAddress(SystemConfigUtils.getTsmUrl());
			ProviderCallTsmWebService client = (ProviderCallTsmWebService) factory.create();
			client.businessEventNotify(seqNum, sessionId, timeStamp, commType, msisdn, appAid, seId, eventId, status);
			message.setMessage(status.value.getStatusDescription());
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
	public @ResponseBody
	JsonMessage clear(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String seId = ServletRequestUtils.getStringParameter(request, "cardNo");
			CardInfo card = cardInfoManager.getByCardNo(seId);
			List<CardLoadFile> clfList = cardLoadfileManager.getByCard(card);
			for(CardLoadFile f:clfList){
				cardLoadfileManager.remove(f);
			}
			cardAppletManager.getByCard(card);
			List<CardApplet> caList = cardAppletManager.getByCard(card);
			for(CardApplet ca:caList){
				cardAppletManager.remove(ca);
			}
			List<CardApplication> cappList = caManager.getCardAppByCard(card);
			for(CardApplication ca:cappList){
				ca.setStatus(CardApplication.STATUS_UNDOWNLOAD);
				caManager.saveOrUpdate(ca);
			}
			List<CardSecurityDomain> csdList = csdManager.getByCard(card);
			for(CardSecurityDomain csd:csdList){
				if(csd.getSd().getId()!=9999){
					csd.setStatus(CardSecurityDomain.STATUS_UNCREATE);
					csd.setCurrentKeyVersion(null);
					csdManager.saveOrUpdate(csd);
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
}

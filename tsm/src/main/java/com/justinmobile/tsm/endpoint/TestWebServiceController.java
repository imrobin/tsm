package com.justinmobile.tsm.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Controller("TestWebServiceControler")
@RequestMapping("/test/")
public class TestWebServiceController {

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
}

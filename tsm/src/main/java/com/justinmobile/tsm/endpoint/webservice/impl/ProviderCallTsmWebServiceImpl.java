package com.justinmobile.tsm.endpoint.webservice.impl;

import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;
import com.justinmobile.tsm.endpoint.manager.ProviderWebServiceManager;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.DomainKey;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.log.manager.ProviderProcessManager;

@WebService(serviceName = "ProviderCallTsmWebService", targetNamespace = NameSpace.CM, portName = "ProviderCallTsmWebServiceHttpPort")
@Service("providerCallTsmService")
public class ProviderCallTsmWebServiceImpl implements ProviderCallTsmWebService {

	@Autowired
	private ProviderProcessManager providerProcessManager;

	@Autowired
	private ProviderWebServiceManager providerWebServiceManager;

	@Override
	public void applicationAPDU(Holder<String> seqNum, String sessionId, String sessionType, Holder<String> timeStamp, Integer commType,
			Integer cmdType, Integer endflag, String msisdn, String appAid, String seId, String fileContent, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadApplication(Holder<String> seqNum, String sessionId, String sessionType, Holder<String> timeStamp,
			Integer commType, String msisdn, String seId, String appAid, String domainAid, String ssdDapSign, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteApplication(Holder<String> seqNum, String sessionID, String sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String seId, String appAid, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSSD(Holder<String> seqNum, String sessionID, String sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String appAid, String seId, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSSD(Holder<String> seqNum, String sessionID, String sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String appAid, String seId, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDomainKey(Holder<String> seqNum, String sessionID, String sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String seId, String domainAid, String keyVersion, List<DomainKey> domainKey, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lockApplication(Holder<String> seqNum, String sessionID, String sessionType, Holder<String> timeStamp, Integer commType,
			String msisdn, String seId, String appAid, Integer lockFlag, Holder<Status> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void businessEventNotify(Holder<String> seqNum, String sessionId, Holder<String> timeStamp, Integer commType, String msisdn,
			String appAid, String seId, Integer eventId, Holder<Status> statusHolder) {
		Status status = new Status();
		try {
			ProviderProcess process = new ProviderProcess();

			process.setSeqNum(seqNum.value);
			process.setSessionId(sessionId);
			process.setAppAid(appAid);
			process.setTimeStamp(timeStamp.value);
			process.setCommType(commType);
			process.setMsisdn(msisdn);
			process.setSeId(seId);
			process.setEventId(eventId);

			providerProcessManager.saveOrUpdate(process);

			providerWebServiceManager.businessEventNotify(process);
		} catch (PlatformException e) {
			e.printStackTrace();
			status.setStatusCode(e.getErrorCode().getErrorCode());
			status.setStatusDescription(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			status.setStatusCode(PlatformErrorCode.UNKNOWN_ERROR.getErrorCode());
			status.setStatusDescription(e.getMessage());
		}

		statusHolder.value = status;
	}

	@Override
	public void acquireToken(Holder<String> seqNum, String sessionID, Holder<String> timeStamp, Integer commType, String msisdn,
			String appAid, String seId, String domainAid, String hashValue, Holder<String> Token, Holder<Status> Status) {
		// TODO Auto-generated method stub

	}

}

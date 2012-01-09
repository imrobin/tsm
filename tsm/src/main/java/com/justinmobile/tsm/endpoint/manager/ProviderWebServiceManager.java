package com.justinmobile.tsm.endpoint.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;

@Transactional
public interface ProviderWebServiceManager {

	void businessEventNotify(ProviderProcess process);

}

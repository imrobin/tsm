package com.justinmobile.tsm.endpoint.webservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.webservice.ProxyServiceFactory;
import com.justinmobile.tsm.endpoint.webservice.ProviderCallTsmWebService;
import com.justinmobile.tsm.system.domain.SystemParams;
import com.justinmobile.tsm.system.manager.SystemParamsManager;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("tsmCaller")
public class TsmCaller {

	@Autowired
	private SystemParamsManager systemParamsManager;

	public ProviderCallTsmWebService getSevice() {
		String tsmUrl = null;
		String tsmServiceName = null;
		if (SystemConfigUtils.isTestRuntimeEnvironment()) {// 使用测试环境，tsm的url和serviceName由配置文件决定
			tsmUrl = SystemConfigUtils.getTsmUrl();
			tsmServiceName = SystemConfigUtils.getServiceName();
		} else {// 使用正式环境，tsm的url和serviceName由配置文件决定
			List<SystemParams> params = systemParamsManager.getParamsByType("systemConfig");
			for (SystemParams param : params) {
				if ("tsmUrl".equals(param.getKey())) {
					tsmUrl = param.getValue();
				}
				if ("tsmServiceName".equals(param.getKey())) {
					tsmServiceName = param.getValue();
				}
			}
		}

		ProxyServiceFactory factory = new ProxyServiceFactory(tsmUrl, tsmServiceName);
		return factory.getHttpPort(ProviderCallTsmWebService.class);
	}

}

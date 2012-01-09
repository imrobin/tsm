package com.justinmobile.tsm.fee.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.core.utils.web.KeyLongValue;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;

@Transactional
public interface FeeRuleFunctionManager extends EntityManager<FeeRuleFunction> {
	List<KeyValue> getSpNameHasApp();

	List<KeyValue> getAppNameBySp(Long spId);

	List<KeyValue> getSdNameBySp(Long spId);

	List<SecurityDomain> getSdBySp(Long spId);

	List<Application> getAppBySp(Long spId);

	List<KeyLongValue> getTransByAidAndVersion(String aid, String version,
			String start, String end);

	FeeRuleFunction getPerFrf(Long spId);

	FeeRuleFunction getFrfBySpAndGranularity(Long spId, Integer granularity);

	public Page<FeeRuleFunction> getFrfpForIndex(Page<FeeRuleFunction> page,
			Map<String, Object> values);

}

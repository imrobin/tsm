package com.justinmobile.tsm.fee.dao;

import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;

public interface FeeRuleFunctionDao extends EntityDao<FeeRuleFunction, Long> {
	FeeRuleFunction getPerFrf(Long spId);

	FeeRuleFunction getMonthFrpBySpAndSize(Long spId, Long size);

	FeeRuleFunction getFrfBySpAndGranularity(Long spId, Integer granularity);

	Page<FeeRuleFunction> getFrfpForIndex(Page<FeeRuleFunction> page,
			Map<String, Object> values);

}

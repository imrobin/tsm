package com.justinmobile.tsm.fee.dao;

import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;

public interface FeeRuleSpaceDao extends EntityDao<FeeRuleSpace, Long> {
	public FeeRuleSpace getFrpByAidAndVersion(String aid, String version);

	public Page<FeeRuleSpace> getFrpForIndex(Page<FeeRuleSpace> page,
			Map<String, Object> values);

	public FeeRuleSpace getFrpByAid(String aid);

}

package com.justinmobile.tsm.fee.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;
import com.justinmobile.core.utils.web.KeyValue;

@Transactional
public interface FeeRuleSpaceManager extends EntityManager<FeeRuleSpace> {
	public List<KeyValue> getSpNameHasSd();

	public List<KeyValue> getSdNameBySp(Long spId);

	public List<KeyValue> getAppNameBySp(Long spId);

	public List<SecurityDomain> getSdBySp(Long spId);

	public List<Application> getAppBySp(Long spId);

	public List<Application> getAppBySd(Long sdId);

	public Long getCountByAppAndDate(Application app, String start, String end);

	public List<String> getCardNoCreateSD(String aid, String start, String end);

	public List<String> getCardNoByAppAndDate(Application app, String start,
			String end);

	public FeeRuleSpace getFrpByAidAndVersion(String aid, String version);

	public FeeRuleSpace getFrpByAid(String aid);

	public Long getAppVerSize(ApplicationVersion appVer);

	public List<KeyValue> getSpName();

	public Page<FeeRuleSpace> getFrpForIndex(Page<FeeRuleSpace> page,
			Map<String, Object> values);

	/**
	 * 查询出只订购过应用版本的用户人数，没有订购过该应用的其他版本
	 */
	public Long getCountByOnlyAppVerAndDate(ApplicationVersion appVer,
			String start, String end);

	/**
	 * 查询出订购过该应用的多个版本，且最后订购的是该应用版本的人数
	 */
	public List<BigDecimal> getCountByMultiAppVerAndDate(Application app,
			String start, String end);

	/**
	 * 查询出某段时间内某个用户最后订购的版本
	 */
	Long getAppVerIdByCustomerCardInfoAndDate(Application app,
			Long customerCardInfoId, String start, String end);

}

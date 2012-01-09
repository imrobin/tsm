package com.justinmobile.tsm.fee.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.fee.dao.FeeRuleSpaceDao;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;

@Repository("feeRuleSpaceDao")
public class FeeRuleSpaceDaoHibernate extends
		EntityDaoHibernate<FeeRuleSpace, Long> implements FeeRuleSpaceDao {

	@Override
	public FeeRuleSpace getFrpByAidAndVersion(String aid, String version) {

		StringBuilder hql = new StringBuilder();
		hql.append("select frp from ").append(FeeRuleSpace.class.getName())
				.append(" as frp where frp.aid='").append(aid).append("'");
		if (!StringUtils.isBlank(version)) {
			hql.append(" and frp.version ='").append(version).append("'");
		}
		return findUnique(hql.toString(), null);
	}

	@Override
	public Page<FeeRuleSpace> getFrpForIndex(Page<FeeRuleSpace> page,
			Map<String, Object> values) {

		StringBuilder hql = new StringBuilder();
		Map<String, Object> _values = new HashMap<String, Object>(2);
		hql.append("select frp from ").append(FeeRuleSpace.class.getName())
				.append(" as frp where 1=1 ");
		if (!StringUtils.isEmpty(values.get("spName").toString())) {
			hql.append(" and  frp.sp.name like :spName escape ' ' ");
			String spName = values.get("spName").toString()
					.replaceAll(" ", "  ").replaceAll("%", " %")
					.replaceAll("_", " _");
			spName = "%" + spName + "%";
			_values.put("spName", spName);
		}
		if (!StringUtils.isEmpty(values.get("appName").toString())){
			hql.append(" and frp.appName like :appName escape ' ' ");
			String appName = values.get("appName").toString()
			       .replace(" ", "  ").replace("%", " %").replaceAll("_", " _");
			appName = "%" + appName + "%";
			_values.put("appName", appName);
		}
		if(!StringUtils.isEmpty(values.get("type").toString())){
			hql.append(" and frp.type=:type");
			_values.put("type", values.get("type"));
		}
		hql.append(" order by frp.aid, frp.granularity");
		return findPage(page, hql.toString(), _values);
	}

	@Override
	public FeeRuleSpace getFrpByAid(String aid) {

		StringBuilder hql = new StringBuilder();
		hql.append("select frp from ").append(FeeRuleSpace.class.getName())
				.append(" as frp where frp.aid='").append(aid).append("'");
		return findUnique(hql.toString(), null);
	}

}

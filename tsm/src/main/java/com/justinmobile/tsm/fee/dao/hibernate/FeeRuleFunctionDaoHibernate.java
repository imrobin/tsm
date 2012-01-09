package com.justinmobile.tsm.fee.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.fee.dao.FeeRuleFunctionDao;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;

@Repository("feeRuleFunctionPerDao")
public class FeeRuleFunctionDaoHibernate extends
		EntityDaoHibernate<FeeRuleFunction, Long> implements FeeRuleFunctionDao {

	@Override
	public FeeRuleFunction getPerFrf(Long spId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select frf from ").append(FeeRuleFunction.class.getName())
				.append(" as frf where frf.sp.id=").append(spId)
				.append(" and frf.pattern=")
				.append(FeeRuleFunction.PATTERN_PER);
		return findUnique(hql.toString(), null);
	}

	@Override
	public Page<FeeRuleFunction> getFrfpForIndex(Page<FeeRuleFunction> page,
			Map<String, Object> values) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> _values = new HashMap<String, Object>(2);
		hql.append("select frfp from ").append(FeeRuleFunction.class.getName())
				.append(" as frfp where 1=1 ");
		if (!StringUtils.isEmpty(values.get("spName").toString())) {
			hql.append(" and  frfp.sp.name like :spName escape ' ' ");
			String spName = values.get("spName").toString()
					.replaceAll(" ", "  ").replaceAll("%", " %")
					.replaceAll("_", " _");
			spName = "%" + spName + "%";
			_values.put("spName", spName);
		}
		hql.append(" order by frfp.sp.name,frfp.granularity");
		return findPage(page, hql.toString(), _values);

	}

	@Override
	public FeeRuleFunction getFrfBySpAndGranularity(Long spId,
			Integer granularity) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select frf from ").append(FeeRuleFunction.class.getName())
				.append(" as frf where frf.sp.id=").append(spId);
		if (null != granularity) {
			hql.append(" and frf.granularity=").append(granularity);
			return findUnique(hql.toString(), null);
		} else {
			List<FeeRuleFunction> list = find(hql.toString(), map);
			if (list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		}

	}

	@Override
	public FeeRuleFunction getMonthFrpBySpAndSize(Long spId, Long size) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> values = new HashMap<String, Object>();
		hql.append("select frf from ").append(FeeRuleFunction.class.getName())
				.append(" as frf where frf.sp.id=").append(spId)
				.append(" and frf.pattern=")
				.append(FeeRuleFunction.PATTERN_MONTH)
				.append(" and frf.granularity>=").append(size)  
		        .append(" order by frf.granularity-").append(size).append(" asc");
		
		List<FeeRuleFunction> list = find(hql.toString(), values);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

}

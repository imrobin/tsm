package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.SecurityDomain;

@Repository("securityDomainDao")
public class SecurityDomainDaoHibernate extends
		EntityDaoHibernate<SecurityDomain, Long> implements SecurityDomainDao {

	@Override
	public SecurityDomain getIsd() {
		StringBuilder hqlBuilder = new StringBuilder();
		hqlBuilder
				.append(" from ")
				.append(SecurityDomain.class.getName())
				.append(" as sd where sd.model = :model and sd.status = :status");
		String hql = hqlBuilder.toString();

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("model", SecurityDomain.MODEL_ISD);
		values.put("status", SecurityDomain.STATUS_PUBLISHED);

		return super.findUnique(hql, values);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Page<Map<String, Object>> findPage(Page<Map<String, Object>> page,
			Map<String, Object> params) {

		String hql = "select c.id, c.name, c.aid, c.sdName, c.privilege, c.status from ";
		hql += "";
		// hql += "(";
		hql += "select -b.id id, b.sp.name name, b.aid, b.sdName, b.privilege, b.status from SecurityDomainApply b where b.status = ：status";
		hql += " union ";
		hql += "select a.id id, a.sp.name name, a.aid, a.sdName, a.privilege, a.status from SecurityDomain a where a.status in (2, 3)";
		// hql += ") c";
		hql += " order by id desc ";
		Query q = getSession().createSQLQuery(hql);

		if (page.isAutoCount()) {
			int totalCount = countHqlResult(hql, params);
			page.setTotalCount(totalCount);
		}

		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());

		List result = q.list();
		System.out.println(result.isEmpty());
		// page.setResult(result);

		return page;
	}

	@Override
	public Page<SecurityDomain> findPageByStatus(Page<SecurityDomain> page, Map<String, Object> params) {
		String hql = "from SecurityDomain a where 1 = ? ";

		if (!params.isEmpty()) {
			if(params.containsKey("spId")) {
				Object spId = params.get("spId");
				if (spId != null)
					hql += " and a.sp.id = " + spId;
			}

			if(params.containsKey("status")) {
				Integer[] status = (Integer[]) params.get("status");
				String var = "";
				final String prefix = ",";
				
				for (Integer status_ : status) {
					var += prefix + status_;
				}
				
				if (var.startsWith(prefix)) {
					var = var.replaceFirst(prefix, "");
				}
				
				hql += " and a.status in (" + var + ")";
			}
			
			if(params.containsKey("name")) {
				String name = (String)params.get("name");
				hql += " and a.sp.name like '%" + name + "%'";
			}
			
			if(params.containsKey("province")) {
				String province = (String)params.get("province");
				hql += " and a.sp.locationNo = '" + province + "'";
			}
		}

		page = this.findPage(page, hql, 1);
		return page;
	}

	@Override
	public List<KeyValue> getSpNameHasSd() {

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select distinct sd.sp.id as key,sd.sp.name as value  from ")
				.append(SecurityDomain.class.getName()).append(" as sd");
		return find(hql.toString());
	}

	@Override
	public List<KeyValue> getSdNameBySp(Long spId) {

		StringBuilder hql = new StringBuilder();
		hql.append("select sd.aid||'-'||sd.sdName as key,sd.sdName as value from ")
				.append(SecurityDomain.class.getName())
				.append(" as sd where sd.sp.id=").append(spId)
				.append(" and sd.status=").append(SecurityDomain.STATUS_PUBLISHED);
		return find(hql.toString());
	}

	@Override
	public List<SecurityDomain> getSdBySp(Long spId) {

		StringBuilder hql = new StringBuilder();
		hql.append("select sd from ").append(SecurityDomain.class.getName())
				.append(" as sd where sd.sp.id=").append(spId)
				.append(" and (sd.status=").append(SecurityDomain.STATUS_PUBLISHED)
				.append(" or sd.status=").append(SecurityDomain.STATUS_ARCHIVED)
				.append(")");
		
		return find(hql.toString());
	}
   
	@Override
	public SecurityDomain getByAid(String aid) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select sd from ").append(SecurityDomain.class.getName())
		.append(" as sd where sd.aid='").append(aid).append("'");
		return findUnique(hql.toString(),null);
	}

	@Override
	public List<SecurityDomain> getByLikeName(String sdName) {
		String hql = "from " + SecurityDomain.class.getName() + " as sd where sd.sdName like '%" + sdName + "%'";
		return find(hql);
	}
}
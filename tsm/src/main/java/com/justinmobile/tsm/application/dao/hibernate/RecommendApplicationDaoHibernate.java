package com.justinmobile.tsm.application.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.application.dao.RecommendApplicationDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Repository("recommendApplicationDao")
public class RecommendApplicationDaoHibernate extends EntityDaoHibernate<RecommendApplication, Long> implements RecommendApplicationDao {

	@Override
	public Page<RecommendApplication> findRecommendApplication(Page<RecommendApplication> page, List<PropertyFilter> filters,
			SysUser currentUser, Customer customer, boolean local) {
		StringBuffer hsql = new StringBuffer("select g from " + RecommendApplication.class.getName() + " as g where 1=1 ");
		for (PropertyFilter filter : filters) {
			if (filter.getPropertyName().equals("id")) {
				hsql.append(" and g.id=" + filter.getMatchValue());
			} else if (filter.getPropertyName().equals("name")) {
				String name = filters.get(0).getMatchValue().toString();
				name = "%" + name + "%";
				hsql.append(" and g.application.name like '" + name + "' escape '/'");
			} else if (filter.getPropertyName().equals("showAll")) {
				if (filter.getMatchValue().equals("no")) {
					hsql.append(" and g.application.status=1 and g.application.sp.inBlack<>1 and g.application.sp.status=1 ");
				}
			}
		}
		if (currentUser != null && local && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())) {
			if (currentUser.getSysRole().getRoleName().equals(SpecialRoleType.CUSTOMER.toString())) { // 普通用户判断customer里的location
				hsql.append(" and g.application.location = '" + customer.getLocation() + "'");
			} else {
				hsql.append(" and g.application.location = '" + currentUser.getProvince() + "'");
			}
		} else if (currentUser != null && currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())) {
			hsql.append(" and 1=1");
		} else if (!local) {
			hsql.append(" and g.application.location like '%" + Application.LOCATION_TOTAL_NETWORK + "%'");
		}

		if (page.getOrderBy() != null && page.getOrderBy().equals("application_name")) {
			hsql.append(" order by g.application.name " + page.getOrder());
		} else if (page.getOrderBy() != null && page.getOrderBy().equals("orderNo")) {
			hsql.append(" order by g.orderNo " + page.getOrder());
		}
		return findPage(page, hsql.toString());
	}

	@Override
	public List<RecommendApplication> getByApplication(Application application) {
		String hql = "from " + RecommendApplication.class.getName() + " as ra where ra.application = ?";

		return find(hql, application);
	}
	
	@Override
	public Page<RecommendApplication> recommendAppListForMobile(Page<RecommendApplication> page, String cardNo, 
			SysUser currentUser, Customer customer) {
		StringBuffer hql = new StringBuffer("select g from " + RecommendApplication.class.getName() + " as g ");
		hql.append(" where g.application.id not in (");
		hql.append(" select distinct ca.applicationVersion.application.id from ").append(CardApplication.class.getName());
		hql.append(" as ca where ca.status in (");
		for (Integer status : CardApplication.STATUS_USEABLE) {
			hql.append(status).append(",");
		}
		hql.deleteCharAt(hql.length() - 1);
		hql.append(") and ca.cardInfo.cardNo='").append(cardNo).append("'");
		hql.append(")").append(" and g.application.status=").append(Application.STATUS_PUBLISHED);
		hql.append(" and g.application.sp.status=").append(SpBaseInfo.NORMAL).append(" and g.application.sp.inBlack <>").append(SpBaseInfo.INBLACK);
		hql.append(" and (g.application.location like '%" + Application.LOCATION_TOTAL_NETWORK + "%' ");
		hql.append(" or g.application.location like '%" + customer.getLocation() + "%')");
		hql.append(" order by g.orderNo");
		return findPage(page, hql.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
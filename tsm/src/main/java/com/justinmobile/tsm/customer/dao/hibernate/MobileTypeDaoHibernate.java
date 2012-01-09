package com.justinmobile.tsm.customer.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.dao.MobileTypeDao;
import com.justinmobile.tsm.customer.domain.MobileType;

@Repository("mobileTypeDao")
public class MobileTypeDaoHibernate extends EntityDaoHibernate<MobileType, Long> implements MobileTypeDao {

	
	@Override
	public List<String> getMobileBrand() {
		
		String hql = "select distinct(tm.brandChs) from "+ MobileType.class.getName()+" as tm";
		List<String> result = find(hql);
		return result;
	}

	@Override
	public List<String> getTypeByBrand(String brand) {
		String hql = "select tm.type from " + MobileType.class.getName() + " as tm where tm.brandChs=:brandChs";
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("brandChs", brand);
		List<String> result = find(hql, values);
		return result;
	}
	   
	@Override
	public Page<MobileType> getMobileByBrand(Page<MobileType> page,String brand) {
 		String hql = "select tm from " + MobileType.class.getName() + " as tm where tm.brandChs=:brandChs";
		Map<String,Object> values = new HashMap<String,Object>(1);
		values.put("brandChs", brand);
		page = findPage(page,hql,values);
		return page;
	}

	@Override
	public Page<MobileType> getMobileByKeyword(final Page<MobileType> page,String keyword) {
		Map<String,Object> values = new HashMap<String,Object>(3);
		String hql = "select tm from " + MobileType.class.getName() +" as tm ";
		if (!StringUtils.isEmpty(keyword)){
			hql += " where upper(tm.brandChs) like :keyword0 escape ' ' " +
			"or upper(tm.brandEng) like :keyword1 escape ' ' or upper(tm.type) like :keyword2 escape ' '";
			keyword = keyword.replaceAll(" ", "  ").replaceAll("%", " %").replaceAll("_", " _");
			keyword = "%" + keyword + "%";
			values.put("keyword0",keyword);
			values.put("keyword1",keyword);
			values.put("keyword2",keyword);
		}
		return  findPage(page,hql,values);
		
	}

	@Override
	public Page<MobileType> getMobileByBrandAndType(final Page<MobileType> page,String brand, String type) {
		String hql = "select tm from " + MobileType.class.getName() + " as tm where tm.brandChs=:brandChs and tm.type=:type";
		Map<String,Object> values = new HashMap<String,Object>(3);
		values.put("brandChs", brand);
		values.put("type",type);
		return  findPage(page,hql,values);
		 
	}
    
	@Override
	public Page<MobileType> getAllMobile(final Page<MobileType> page) {
		return getAll(page);
	}

	@Override
	public List<String> getSuggestByKeyword(String keyword) {
		String hql = "select concat(tm.brandChs,tm.type) from " + MobileType.class.getName() + " as tm where concat(tm.brandChs,tm.type) like :keyword";
		Map<String,Object> values = new HashMap<String,Object>(3);
		values.put("keyword","%"+keyword+"%");
		return find(hql, values);
	}
	
	@Override
	public List<MobileType> getTypeAndValueByBrand(String brand) {
		String hql = "select tm from " + MobileType.class.getName() + " as tm where tm.brandChs=:brandChs";
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("brandChs", brand);
		List<MobileType> result = find(hql, values);
		return result;
	}


	@Override
	public Page<MobileType> getMobileByKeywordForIndex(Page<MobileType> page,
			Map<String,Object> values) {
		Map<String,Object> _values = new HashMap<String,Object>(3);
		StringBuffer hql = new StringBuffer("select tm from " + MobileType.class.getName() +" as tm where 1=1 ");
		if (!StringUtils.isEmpty(values.get("brandChs").toString())){
			hql.append(" and  tm.brandChs like :brandChs escape ' ' ");
			String brandChs = values.get("brandChs").toString().replaceAll(" ", "  ").replaceAll("%", " %").replaceAll("_", " _");
			brandChs = "%" + brandChs + "%";
			_values.put("brandChs",brandChs);
		}
		if (!StringUtils.isEmpty(values.get("type").toString())){
			hql.append(" and  tm.type like :type escape ' ' ");
			String type = values.get("type").toString().replaceAll(" ", "  ").replaceAll("%", " %").replaceAll("_", " _");
			type = "%" + type + "%";
			_values.put("type",type);
		}
		return  findPage(page,hql.toString(),_values);
	}
}
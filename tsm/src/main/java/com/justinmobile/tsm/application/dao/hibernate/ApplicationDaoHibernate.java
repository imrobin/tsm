package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.utils.reflection.ConvertUtils;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationType;
import com.justinmobile.tsm.application.domain.RecommendApplication;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Repository("applicationDao")
public class ApplicationDaoHibernate extends EntityDaoHibernate<Application, Long> implements ApplicationDao {

	@Override
	public Page<Application> advanceSearch(Page<Application> page, Map<String, String> paramMap) {
		Map<String, Object> values = new HashMap<String, Object>();
		StringBuilder hsql = new StringBuilder("select g from " + Application.class.getName()
				+ " as g" + " where g.status= 1 and g.sp.inBlack <>1 and  g.sp.status=1 ");
		if (StringUtils.isNotEmpty(paramMap.get("name"))){
			String name = paramMap.get("name").replaceAll(" ", "  ").replaceAll("%", " %").replaceAll("_", " _");
			name = "%" + name + "%";
			hsql.append(" and g.name like '"+name+"' escape ' '");
		}
		if (StringUtils.isNotEmpty(paramMap.get("childIds"))){
			hsql.append(" and g.childType.id in ("+paramMap.get("childIds")+")");
		}else if (StringUtils.isNotEmpty(paramMap.get("father"))){
			values.put("father", Long.parseLong(paramMap.get("father")));
			hsql.append(" and g.childType.parentType.id =:father");
		}
		
		if (StringUtils.isNotEmpty(paramMap.get("spId"))){
			values.put("spId", Long.parseLong(paramMap.get("spId")));
			hsql.append(" and g.sp.id = :spId");
		}
		if (StringUtils.isNotEmpty(paramMap.get("star"))){
			values.put("star", Integer.parseInt(paramMap.get("star")));
			hsql.append(" and g.starNumber = :star");
		}
		hsql.append(" order by g.downloadCount desc,g.id asc ");
		return findPage(page, hsql.toString(), values);
	}

	@Override
	public Page<Application> recommendAppList(Page<Application> page, SysUser currentUser) {
		StringBuilder hsql = new StringBuilder("select a from " + Application.class.getName()
				+ " as a where a.status= "+Application.STATUS_PUBLISHED+" and a.sp.status="+SpBaseInfo.STATUS_AVALIABLE+
				" and a.sp.inBlack <>"+SpBaseInfo.INBLACK+" and a.id not in (select ra.application.id from "
				+RecommendApplication.class.getName()+" as ra)");
		if (currentUser != null && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())){
			hsql.append(" and a.location='"+currentUser.getProvince()+"'");
		}
		return findPage(page, hsql.toString());
	}

	@Override
	public Page<Application> getDownloadableApps(Page<Application> page, String cardNo, Map<String, ?> filters) {
		
		StringBuilder hql = new StringBuilder("select app from ").append(Application.class.getName()).append(" as app");
		hql.append(" where app.id not in (");
		hql.append("select distinct ca.applicationVersion.application.id from ").append(CardApplication.class.getName());
		hql.append(" as ca where ca.status in (");
		for (Integer status : CardApplication.STATUS_USEABLE) {
			hql.append(status).append(",");
		}
		hql.deleteCharAt(hql.length() - 1);
		hql.append(") and ca.cardInfo.cardNo='").append(cardNo).append("'");
		hql.append(")").append(" and app.status=").append(Application.STATUS_PUBLISHED);
		hql.append(" and app.sp.status=").append(SpBaseInfo.NORMAL).append(" and app.sp.inBlack <>").append(SpBaseInfo.INBLACK);
		Map<String, Object> map = new HashMap<String, Object>();
		if (MapUtils.isNotEmpty(filters)) {
			for (Map.Entry<String, ?> entry : filters.entrySet()) {
				String[] keys = null;
				if (StringUtils.contains(entry.getKey(), PropertyFilter.OR_SEPARATOR)) {
					keys = StringUtils.splitByWholeSeparator(entry.getKey(), PropertyFilter.OR_SEPARATOR);
				} else {
					keys = new String[]{entry.getKey()};
				}
				hql.append(" and (");
				for (int i = 0; i < keys.length; i++) {
					String orginalKey = keys[i];
					String key = StringUtils.substringAfterLast(orginalKey, "_");
					String option = StringUtils.substringBeforeLast(orginalKey, "_");
					MatchType matchType = MatchType.valueOf(StringUtils.substring(option, 0, option.length() - 1));
					Class<?> propertyClass = Enum.valueOf(PropertyType.class, StringUtils.substring(option, option.length() - 1, option.length())).getValue();
					if (i > 0) {
						hql.append(" or ");
					}
					hql.append(" app.").append(key).append(" ").append(matchType.getOption()).append(" :");
					if (StringUtils.contains(key, ".")) {
						key = StringUtils.replace(key, ".", "_");
					}
					hql.append(key);
					String value = String.valueOf(entry.getValue());
					if (matchType.equals(MatchType.LIKE)) {
						value = "%" + value + "%";
					}
					map.put(key, ConvertUtils.convertStringToObject(value, propertyClass));
				}
				hql.append(") ");
			}
		}
		return findPage(page, hql.toString(), map);
	}

	@Override
	public Page<Application> findByAppType(Page<Application> page, Long parentId) {
		StringBuilder hsql = new StringBuilder("select a from " + Application.class.getName()
				+ " as a where a.childType.parentType.id = "+parentId+" and a.status= "+Application.STATUS_PUBLISHED+
				" and a.sp.status="+SpBaseInfo.STATUS_AVALIABLE+" and a.sp.inBlack <>"+SpBaseInfo.INBLACK);
		return findPage(page, hsql.toString());
	}
     
	@Override
	public List<KeyValue> getSpHasApp() {
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct app.sp.id as key,app.sp.name as value from ")
		.append(Application.class.getName()).append(" as app")
		.append(" where app.status=").append(Application.STATUS_PUBLISHED);
		return find(hql.toString());
	}
  
	@Override
	public List<Application> getAppBySp(Long spId) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select app from ").append(Application.class.getName())
		.append(" as app where app.sp.id=").append(spId)
		.append(" and (app.status=").append(Application.STATUS_PUBLISHED)
		.append(" or app.status=").append(Application.STATUS_ARCHIVED)
		.append(")");
		return find(hql.toString());
	}
    
	@Override
	public List<KeyValue> getAppNameBySp(Long spId) {
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct app.aid||'-'||app.name as key,app.name as value from ").append(Application.class.getName())
		.append(" as app where app.sp.id=").append(spId)
		.append(" and app.status=").append(Application.STATUS_PUBLISHED);
		return find(hql.toString());
	}
	
	@Override
	public List<Application> getAppBySd(Long sdId) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select app from ").append(Application.class.getName())
		.append(" as app where app.sd.id=").append(sdId)
		.append(" and (app.status=").append(Application.STATUS_PUBLISHED)
		.append(" or app.status=").append(Application.STATUS_ARCHIVED)
		.append(")");
		return find(hql.toString());
	}

	@Override
	public List<Application> getApplistByTypeIncludeChildTypeOrderByDownloadCount(ApplicationType at) {
		String hql =  "from " + Application.class.getName() + " as app where app.childType = ? order by app.downloadCount desc";
		List<Application> appList = this.find(hql, at);
		hql = "from " + Application.class.getName() + " as app where app.childType.parentType = ? order by app.downloadCount desc";
		List<Application> childList = this.find(hql, at);
		appList.addAll(childList);
		return appList;
	}
}

package com.justinmobile.tsm.system.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.system.dao.RequistionDao;
import com.justinmobile.tsm.system.domain.Requistion;

@Repository("requistionDao")
public class RequistionDaoHibernate extends EntityDaoHibernate<Requistion, Long> implements RequistionDao {

	@Override
	public Page<Requistion> findPageByParam(Page<Requistion> page, Map<String, String> paramMap, SysUser currentUser) {
		Map<String, Object> values = new HashMap<String, Object>();
		String hsql = "select g from " + Requistion.class.getName() + " as g," + ApplicationVersion.class.getName() + " as av ";
		hsql += " where g.originalId = av.id and av.application.id is not null ";
		
		if (StringUtils.isEmpty(paramMap.get("spId"))) {
			hsql += " and g.status="+Requistion.STATUS_INIT;
			hsql += " and g.type in " + paramMap.get("types");
		}
		if (!StringUtils.isEmpty(paramMap.get("appName"))) {
			hsql += " and av.application.name like :appName";
			values.put("appName", paramMap.get("appName"));
		}
		if (!StringUtils.isEmpty(paramMap.get("status"))) {
			hsql += " and g.status = :status ";
			values.put("status", Integer.parseInt(paramMap.get("status")));
		}
		if (!StringUtils.isEmpty(paramMap.get("id"))) {
			hsql += " and g.id = :id ";
			values.put("id", Long.parseLong(paramMap.get("id")));
		}
		//有spid表示前台显示，不用判断location
		if (!StringUtils.isEmpty(paramMap.get("spId"))) {
			hsql += " and av.application.sp.id = :spId ";
			values.put("spId", Long.parseLong(paramMap.get("spId")));
		}else if (currentUser != null && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())){
			hsql += " and av.application.location = '"+currentUser.getProvince()+"'";
		}
		if (!StringUtils.isEmpty(paramMap.get("orderBy"))) {
			String orderBy = paramMap.get("orderBy").replace("_", " ");
			if (orderBy.startsWith("appName")) {
				orderBy = orderBy.replace("appName", "name");
				hsql += " order by av.application." + orderBy;
			}else if (orderBy.startsWith("versionNo")) {
				hsql += " order by av." + orderBy;
			} else {
				hsql += " order by g." + orderBy;
			}
		}
		return findPage(page, hsql, values);
	}

	@Override
	public int getCountByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId) {
		String hql = "select count(*) from " + Requistion.class.getName()
				+ " as r where r.type=:type and r.status=:status and r.originalId=:originalId";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("type", type);
		values.put("status", status);
		values.put("originalId", originalId);

		return countHqlResult(hql, values);
	}
	
	@Override
	public List<Requistion> getByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId) {
		String hql = "select distinct r from " + Requistion.class.getName()
				+ " as r where r.type=:type and r.status=:status and r.originalId=:originalId";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("type", type);
		values.put("status", status);
		values.put("originalId", originalId);

		return find(hql, values);
	}
	
	@Override
	public boolean deleteRequistions(Integer type, Long originalId) {
		boolean bln = false;
		batchExecute("delete from Requistion a where a.type = ? and a.originalId = ?", type, originalId);
		bln = true;
		return bln;
	}

	@Override
	public Page<Requistion> findPageByType(Page<Requistion> page, Long originalId, Integer... types) {
		String typeIn = "";
		if(types != null && types.length > 0) {
			for(Integer type : types) {
				typeIn += "," + type;
			}
			if(!org.apache.commons.lang.StringUtils.isBlank(typeIn)) {
				typeIn = " and a.type in(" + typeIn.replaceFirst(",", "") + ")";
			}
		}
		
		String hql = "from Requistion a where a.originalId = ?" + typeIn + " order by a.submitDate desc, a.reviewDate desc";
		page = findPage(page, hql, originalId);
		return page;
	}

	@Override
	public Page<Requistion> findPageForSD(Page<Requistion> page, Long originalId) {
		return findPageByType(page, originalId, Requistion.TYPE_SD_ARCHIVE, Requistion.TYPE_SD_MODIFY, Requistion.TYPE_SD_PUBLISH);
	}

	@Override
	public Page<Requistion> findPageForSD(Page<Requistion> page) {
		String hql = "from Requistion a where a.type in (?, ?, ?)";
		return findPage(page, hql, Requistion.TYPE_SD_ARCHIVE, Requistion.TYPE_SD_MODIFY, Requistion.TYPE_SD_PUBLISH);
	}
	
	@Override
	public Requistion findRequistionByOriginalIdAndType(Long originalId, Integer... type) {
		String condition = "";
		String hql = "from Requistion a where a.originalId = :originalId ";
		if(type.length > 0) {
			String typeString = "";
			condition = " and a.type in (";
			for(Integer t : type) {
				typeString += "," + t;
			}
			condition += typeString.replaceFirst(",", "") + ") ";
		}
		hql += condition + " order by a.id desc";
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("originalId", originalId);
		List<Requistion> list = find(hql, values);
		Requistion requistion = null;
		if(list != null && !list.isEmpty()) requistion = list.get(0);
		return requistion;
	}
}
package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.LoadModuleDao;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;

@Repository("loadModuleDao")
public class LoadModuleDaoHibernate extends EntityDaoHibernate<LoadModule, Long> implements LoadModuleDao {

	@Override
	public boolean isAidExist(LoadFileVersion loadFileVersion, String aid) {
		String hql = "from " + LoadModule.class.getName() + " as lm where lm.loadFileVersion.id=:loadFileVersionId and lm.aid=:aid";
		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("loadFileVersionId", loadFileVersion.getId());
		values.put("aid", aid);

		LoadModule result = super.findUnique(hql, values);

		return (null != result);
	}

	@Override
	public void remove(LoadModule entity) {
		entity.unassignLoadFileVersion();
		
		super.remove(entity);
	}
	
	@Override
	public void saveOrUpdate(LoadModule entity){
		entity.unassignLoadFileVersion();
		
		super.saveOrUpdate(entity);
		
		entity.assignLoadFileVersion(entity.getLoadFileVersion());
	}
}
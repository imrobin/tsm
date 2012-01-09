package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadModule;

@Repository("appletDao")
public class AppletDaoHibernate extends EntityDaoHibernate<Applet, Long> implements AppletDao {

	@Override
	public List<Applet> getByInstallOrder(Long applicationVersionId) {
		String hql = "from " + Applet.class.getName()
				+ " as a where a.applicationVersion.id=:applicationVersionId order by a.orderNo asc, a.id asc";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("applicationVersionId", applicationVersionId);

		List<Applet> result = find(hql, values);

		return result;
	}

	@Override
	public List<Applet> getByLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId) {
		String hql = "from " + Applet.class.getName()
				+ " as a where a.applicationVersion.id=:applicationVersionId and a.loadModule.loadFileVersion.id=:loadFileVersionId";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersionId", applicationVersionId);
		values.put("loadFileVersionId", loadFileVersionId);

		List<Applet> result = find(hql, values);

		return result;
	}

	@Override
	public int getCountThatBelongLoadModule(long loadModuleId) {
		String hql = "select count(*) from " + Applet.class.getName() + " as a where a.loadModule.id=:loadModuleId";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("loadModuleId", loadModuleId);
		return countHqlResult(hql, values);
	}

	@Override
	public void saveOrUpdate(Applet entity) {
		entity.unassignApplicationVersion();
		entity.unassignLoadModule();

		super.saveOrUpdate(entity);

		entity.assignApplicationVersion(entity.getApplicationVersion());
		entity.assignLoadModule(entity.getLoadModule());

		super.saveOrUpdate(entity);
	}

	@Override
	public void remove(Applet entity) {
		entity.unassignApplicationVersion();
		entity.unassignLoadModule();

		super.remove(entity);
	}

	@Override
	public Applet getByAidAndLoadModuleAndApplicationVersion(String aid, LoadModule loadModule, ApplicationVersion applicationVersion) {
		String hql = "from " + Applet.class.getName() + " as a where a.aid = ? and a.loadModule = ? and a.applicationVersion = ?";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("aid", aid);
		values.put("loadModule", loadModule);
		values.put("applicationVersion", applicationVersion);

		return findUniqueEntity(hql, aid, loadModule, applicationVersion);
	}
}
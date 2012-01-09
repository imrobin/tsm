package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Repository("loadFileVersionDao")
public class LoadFileVersionDaoHibernate extends EntityDaoHibernate<LoadFileVersion, Long> implements LoadFileVersionDao {

	@Override
	public List<LoadFileVersion> getByApplicationVersion(Long applicationVersionId) {
		String hql = "select lf from "
				+ LoadFileVersion.class.getName()
				+ " as lf "
				+ "left join lf.applicationLoadFiles as alf where alf.applicationVersion.id=:applicationVersionId order by lf.loadFile.shareFlag asc";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("applicationVersionId", applicationVersionId);

		return super.find(hql, values);
	}

	@Override
	public int getCountByLoadFileAndVersionNo(LoadFile loadFile, String versionNo) {
		String hql = "select count(*) from " + LoadFileVersion.class.getName()
				+ " as lfv where lfv.loadFile.id=:loadFileId and lfv.versionNo=:versionNo";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("loadFileId", loadFile.getId());
		values.put("versionNo", versionNo);

		return countHqlResult(hql, values);
	}

	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersion(Long applicationVersionId, Integer shareFlag) {
		String hql = "select lfv from "
				+ LoadFileVersion.class.getName()
				+ " as lfv "
				+ "left join lfv.applicationLoadFiles as alf where alf.applicationVersion.id=:applicationVersionId and lfv.loadFile.shareFlag=:shareFlag order by lfv.loadFile.aid";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersionId", applicationVersionId);
		values.put("shareFlag", shareFlag);

		return super.find(hql, values);
	}

	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersionAndType(ApplicationVersion applicationVersion, int type) {
		String hql = "select lfv from "
				+ LoadFileVersion.class.getName()
				+ " as lfv "
				+ "left join lfv.applicationLoadFiles as alf where alf.applicationVersion = :applicationVersion and lfv.loadFile.type = :type order by lfv.loadFile.aid";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersion", applicationVersion);
		values.put("type", type);

		return find(hql, values);
	}

	@Override
	public void remove(LoadFileVersion entity) {
		entity.getLoadFile().removeLoadFileVersion(entity);
		super.remove(entity);
	}

	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersionOrderByAidAsc(ApplicationVersion applicationVersion) {
		String hql = "select lfv from " + LoadFileVersion.class.getName() + " as lfv "
				+ "left join lfv.applicationLoadFiles as alf where alf.applicationVersion = :applicationVersion order by lfv.loadFile.aid";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("applicationVersion", applicationVersion);

		return find(hql, values);
	}

}
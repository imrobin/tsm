package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationLoadFileDao;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Repository("applicationLoadFileDao")
public class ApplicationLoadFileDaoHibernate extends EntityDaoHibernate<ApplicationLoadFile, Long> implements ApplicationLoadFileDao {

	@Override
	public ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(Long applicationVersionId, Long loadFileVersionId) {
		String hql = "select alf from " + ApplicationLoadFile.class.getName()
				+ " as alf where alf.loadFileVersion.id=:loadFileVersionId and alf.applicationVersion.id=:applicationVersionId";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersionId", applicationVersionId);
		values.put("loadFileVersionId", loadFileVersionId);

		return findUnique(hql, values);
	}

	@Override
	public ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(ApplicationVersion applicationVersion,
			LoadFileVersion loadFileVersion) {
		return getByApplicationVersionAndLoadFileVersion(applicationVersion.getId(), loadFileVersion.getId());
	}

	@Override
	public List<ApplicationLoadFile> getExclusiveByDownloadOrder(Long applicationVersionId) {
		String hql = "select distinct alf from "
				+ ApplicationLoadFile.class.getName()
				+ " as alf where alf.applicationVersion.id=:applicationVersionId and alf.loadFileVersion.loadFile.shareFlag=:shareFlag order by alf.downloadOrder asc, alf.id asc";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersionId", applicationVersionId);
		values.put("shareFlag", LoadFile.FLAG_EXCLUSIVE);

		List<ApplicationLoadFile> result = find(hql, values);
		if (logger.isDebugEnabled()) {
			for (ApplicationLoadFile item : result) {
				logger.debug("\n" + "id: " + item.getId() + "\n");
			}
		}

		return result;
	}

	@Override
	public List<ApplicationLoadFile> getExclusiveByDeleteOrder(Long applicationVersionId) {
		String hql = "select distinct alf from "
				+ ApplicationLoadFile.class.getName()
				+ " as alf where alf.applicationVersion.id=:applicationVersionId and alf.loadFileVersion.loadFile.shareFlag=:shareFlag order by alf.deleteOrder asc, alf.id desc";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("applicationVersionId", applicationVersionId);
		values.put("shareFlag", LoadFile.FLAG_EXCLUSIVE);

		List<ApplicationLoadFile> result = find(hql, values);
		if (logger.isDebugEnabled()) {
			for (ApplicationLoadFile item : result) {
				logger.debug("\n" + "id: " + item.getId() + "\n");
			}
		}

		return result;
	}

	@Override
	public List<ApplicationLoadFile> getByApplicationVersionAsDownloadOrder(ApplicationVersion applicationVersion) {
		String hql = "select distinct alf from " + ApplicationLoadFile.class.getName()
				+ " as alf where alf.applicationVersion.id=:applicationVersionId order by alf.downloadOrder asc, alf.id asc";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersionId", applicationVersion.getId());

		List<ApplicationLoadFile> result = find(hql, values);

		return result;
	}

	@Override
	public List<ApplicationLoadFile> getByApplicationVersionAsDeleteOrder(ApplicationVersion applicationVersion) {
		String hql = "select distinct alf from " + ApplicationLoadFile.class.getName()
				+ " as alf where alf.applicationVersion.id=:applicationVersionId order by alf.deleteOrder asc, alf.id desc";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("applicationVersionId", applicationVersion.getId());

		List<ApplicationLoadFile> result = find(hql, values);

		return result;
	}

	@Override
	public void saveOrUpdate(ApplicationLoadFile entity) {
		entity.unassignApplicationVersionAndLoadFileVersion();

		super.saveOrUpdate(entity);

		entity.assignApplicationVersionAndLoadFileVersion(entity.getApplicationVersion(), entity.getLoadFileVersion());

		super.saveOrUpdate(entity);
	}

	@Override
	public void remove(ApplicationLoadFile entity) {
		entity.unassignApplicationVersionAndLoadFileVersion();

		super.remove(entity);
	}

	@Override
	public ApplicationLoadFile getByApplicationVersionAndLoadFile(ApplicationVersion applicationVersion, LoadFile loadFile) {
		String hql = "select distinct alf from " + ApplicationLoadFile.class.getName()
				+ " as alf where alf.applicationVersion = :applicationVersion and alf.loadFileVersion.loadFile = :loadFile";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("applicationVersion", applicationVersion);
		values.put("loadFile", loadFile);

		return findUnique(hql, values);
	}
}
package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Repository("loadFileDao")
public class LoadFileDaoHibernate extends EntityDaoHibernate<LoadFile, Long> implements LoadFileDao {

	@Override
	public List<LoadFile> getLoadFilesWhichExclusivAndBelongSpAndUnassociateWithApplicationVersion(SpBaseInfo sp, Long applicationVersionId) {
		String hql = "select distinct lf from "
				+ LoadFile.class.getName()
				+ " as lf where lf.shareFlag=:shareFlag and lf.id not in (select distinct lf.id from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where alf.applicationVersion.id=:applicationVersionId)";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("shareFlag", LoadFile.FLAG_EXCLUSIVE);
		values.put("applicationVersionId", applicationVersionId);

		if (null != sp) {
			hql = hql + " and lf.sp.id=:spId";
			values.put("spId", sp.getId());
		}

		hql = hql + " order by lf.aid asc";

		return find(hql, values);
	}

	@Override
	public List<LoadFile> getSharedLoadFilesWhichUnassociateWithApplicationVersion(Long applicationVersionId) {
		String hql = "select distinct lf from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where lf.shareFlag=:shareFlag and lf.id not in (select distinct lf.id from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where alf.applicationVersion.id=:applicationVersionId) order by lf.aid asc";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("shareFlag", LoadFile.FLAG_SHARED);
		values.put("applicationVersionId", applicationVersionId);

		return find(hql, values);
	}

	@Override
	public List<LoadFile> getThatIsSharedAndIsnotSelfAndIsnotDependent(LoadFileVersion loadFileVersion) {
		String hql = "select distinct lf from "
				+ LoadFile.class.getName()
				+ " as lf where lf.id!=:loadFileId and lf.shareFlag=:shareFlag and lf.id not in (select distinct lf.id from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.children as children where children.id=:loadFileVersionId) order by lf.id asc";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("shareFlag", LoadFile.FLAG_SHARED);
		values.put("loadFileId", loadFileVersion.getLoadFile().getId());
		values.put("loadFileVersionId", loadFileVersion.getId());

		return find(hql, values);
	}

	@Override
	public Page<LoadFile> loadByIds(Page<LoadFile> page, String loadFileIds) {
		String hql = "select lf from " + LoadFile.class.getName() + " as lf where lf.id in (" + loadFileIds + ")";
		return findPage(page, hql);
	}

	@Override
	public List<LoadFile> getUnusedByApplicationVersionAndType(ApplicationVersion applicationVersion, int fileType) {
		String hql = "select distinct lf from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where lf.type = :type and alf.applicationVersion.application = :application and lf.id not in "
				+ "(select distinct lf.id from "
				+ LoadFile.class.getName()
				+ " as lf left join lf.versions as lfv left join lfv.applicationLoadFiles as alf where alf.applicationVersion = :applicationVersion)"
				+ " order by lf.aid asc";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("type", fileType);
		values.put("applicationVersion", applicationVersion);
		values.put("application", applicationVersion.getApplication());

		return find(hql, values);
	}
}
package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.ApplicationClientInfoDao;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;

@Repository("applicationClientInfoDao")
public class ApplicationClientInfoDaoHibernate extends EntityDaoHibernate<ApplicationClientInfo, Long> implements
		ApplicationClientInfoDao {

	@Override
	public Page<ApplicationClientInfo> getByApplicationVersion(Page<ApplicationClientInfo> page,
			Long applicationVersionId) {
		String hql = "select aci from " + ApplicationClientInfo.class.getName()
				+ " as aci join aci.applicationVersions as avs where avs.id=:applicationVersionId";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("applicationVersionId", applicationVersionId);

		return findPage(page, hql, values);
	}

	@Override
	public ApplicationClientInfo getByApplicationVersionAndSysRequirmentAndFileTypeAndVersion(
			ApplicationVersion applicationVersion, String sysRequirment, String fileType, String version) {
		StringBuilder hql = new StringBuilder("select aci from ").append(ApplicationClientInfo.class.getName());
		hql.append(" as aci left join aci.applicationVersions as avs where avs=:applicationVersion and aci.sysRequirment=:sysRequirment and aci.fileType = :fileType and aci.version = :version");

		Map<String, Object> values = new HashMap<String, Object>(4);
		values.put("applicationVersion", applicationVersion);
		values.put("sysRequirment", sysRequirment);
		values.put("fileType", fileType);
		values.put("version", version);

		return findUnique(hql.toString(), values);
	}

	@Override
	public ApplicationClientInfo getByApplicationVersionTypeVersionFileType(ApplicationVersion applicationVersion,
			String sysType, String sysRequirment, String fileType) {
		StringBuilder hql = new StringBuilder("select aci from ");
		hql.append(ApplicationClientInfo.class.getName());
		hql.append(" as aci left join aci.applicationVersions as avs where aci.busiType=");
		hql.append(ApplicationClientInfo.BUSI_TYPE_APPLICATION_CLIENT);
		hql.append(" and aci.status=").append(ApplicationClientInfo.STATUS_RELEASE);
		hql.append(" and avs=:applicationVersion and aci.sysType=:sysType and aci.fileType=:fileType");
		Map<String, Object> values = new HashMap<String, Object>(4);
		values.put("applicationVersion", applicationVersion);
		values.put("sysType", sysType);
		values.put("fileType", fileType);
		if (StringUtils.isBlank(sysRequirment)) {
			hql.append(" order by aci.sysRequirment desc");
		} else {
			hql.append(" and aci.sysRequirment=:sysRequirment");
			values.put("sysRequirment", sysRequirment);
		}
		List<ApplicationClientInfo> list = find(hql.toString(), values);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Page<ApplicationClientInfo> getApplicationClientInfoForIndex(final Page<ApplicationClientInfo> page,
			Map<String, Object> values) {

		Map<String, Object> _values = new HashMap<String, Object>(3);
		StringBuffer hql = new StringBuffer("select aci from " + ApplicationClientInfo.class.getName()
				+ " as aci where busiType=:busiType");
		_values.put("busiType", values.get("busiType"));
		if (!StringUtils.isEmpty(values.get("name").toString())) {
			hql.append(" and  aci.name like :name escape ' ' ");
			String type = values.get("name").toString().replaceAll(" ", "  ").replaceAll("%", " %")
					.replaceAll("_", " _");
			type = "%" + type + "%";
			_values.put("name", type);
		}
		if (!StringUtils.isEmpty(values.get("status").toString())) {
			hql.append(" and aci.status = :status");
			_values.put("status", new Integer(values.get("status").toString()));
		}
		return findPage(page, hql.toString(), _values);
	}

	@Override
	public List<Map<String, Object>> getHistoryVersion(String sysType, String sysRequirment) {
		StringBuilder hql = new StringBuilder();
		hql.append("select new map(aci.sysType as sysType,aci.name as name,aci.version as version,aci.id as id) from ");
		hql.append(ApplicationClientInfo.class.getName());
		hql.append(" aci where aci.status=").append(ApplicationClientInfo.STATUS_RELEASE);
		hql.append(" and aci.sysType='").append(sysType).append("'");
		hql.append(" and aci.sysRequirment='").append(sysRequirment).append("'");
		hql.append(" and aci.busiType=").append(ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER);
		hql.append(" order by aci.version asc");
		return find(hql.toString());
	}

	@Override
	public Integer getMaxVersionCode(Integer busiType, String sysType, String sysRequirment) {

		StringBuilder hql = new StringBuilder();
		hql.append("select max(aci.versionCode) from ").append(ApplicationClientInfo.class.getName());
		hql.append(" aci where aci.busiType=").append(busiType);
		hql.append(" and aci.sysType='").append(sysType).append("'");
		hql.append(" and aci.sysRequirment='").append(sysRequirment).append("'");
		Integer result = findUniqueEntity(hql.toString());
		if (result == null) {
			return 1;
		} else {
			return result;
		}
	}

	@Override
	public Integer getMaxVersionCodeByAppVer(Integer busiType, String sysType, String sysRequirment, Long appVerId) {
		StringBuilder hql = new StringBuilder("select max(aci.versionCode) from ");
		hql.append(ApplicationClientInfo.class.getName());
		hql.append(" as aci left join aci.applicationVersions as avs where aci.busiType=");
		hql.append(busiType).append(" and avs.id=").append(appVerId);
		hql.append(" and aci.sysType='").append(sysType).append("'");
		hql.append("and aci.sysRequirment='").append(sysRequirment).append("'");
		Integer result = findUniqueEntity(hql.toString());
		if (result == null) {
			return 1;
		} else {
			return result;
		}

	}

	@Override
	public String getMocamMaxVersion() {
		StringBuilder hql = new StringBuilder("select max(aci.version) from ");
		hql.append(ApplicationClientInfo.class.getName());
		hql.append(" as aci where aci.busiType=").append(ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER);
		String result = findUniqueEntity(hql.toString());
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}

}
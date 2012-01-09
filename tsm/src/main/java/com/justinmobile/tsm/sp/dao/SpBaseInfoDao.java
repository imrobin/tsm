package com.justinmobile.tsm.sp.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;

import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

public interface SpBaseInfoDao extends EntityDao<SpBaseInfo, Long> {
	
	public Long generateServiceProviderNumber();
	
	public Page<SpBaseInfo> getUnAuditSp(final Page<SpBaseInfo> page,final Object... values);

	public Page<SpBaseInfo> advanceSearch(Page<SpBaseInfo> page, Map<String, String> paramMap);

	public Page<SpBaseInfo> recommendSpList(Page<SpBaseInfo> page, SysUser currentUser);
	
	/**
	 * 撤销申请时，删除SP及SP关联的申请数据
	 * @param spId
	 * @return
	 */
	public boolean deleteSpForUnavaliable(Long spId);
	
	public boolean isPropertyUniqueForAvaliable(final String propertyName, final Object newValue, final Object oldValue);
	
	public List<KeyValue> getSpName();
	
	public Page<SpBaseInfo> getSpForAvailableApplication(Page<SpBaseInfo> page, Map<String, String> params);
}
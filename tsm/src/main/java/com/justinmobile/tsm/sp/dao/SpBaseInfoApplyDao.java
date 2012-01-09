package com.justinmobile.tsm.sp.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;

public interface SpBaseInfoApplyDao extends EntityDao<SpBaseInfoApply, Long>{

	public void delete(Long id);
	
	public boolean isPropertyUniqueForAvaliable(final String propertyName, final Object newValue, final Object oldValue);
}

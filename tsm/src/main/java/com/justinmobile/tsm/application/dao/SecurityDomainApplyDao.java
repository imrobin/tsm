package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;

public interface SecurityDomainApplyDao extends EntityDao<SecurityDomainApply, Long> {

	boolean deleteSecurityDomainApplyByFormalId(Long sdId);
	
	boolean isPropertyUniqueForAidByStatus(final Object newValue, final Object orgValue);
}

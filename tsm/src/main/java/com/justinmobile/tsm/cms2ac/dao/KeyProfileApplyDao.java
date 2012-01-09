package com.justinmobile.tsm.cms2ac.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.cms2ac.domain.KeyProfileApply;

public interface KeyProfileApplyDao extends EntityDao<KeyProfileApply, Long> {
	
	public void removeAll(Long securityDomainApplyId);

}

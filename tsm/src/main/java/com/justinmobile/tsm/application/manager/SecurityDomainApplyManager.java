package com.justinmobile.tsm.application.manager;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;

@Transactional
public interface SecurityDomainApplyManager extends EntityManager<SecurityDomainApply> {
	
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, Long spId);
	
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, String orderBy, Map<String, Object> params);
	
	public Page<SecurityDomainApply> findPage(Page<SecurityDomainApply> page, Integer status, String orderBy);
}

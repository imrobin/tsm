package com.justinmobile.tsm.sp.manager;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;

@Transactional
public interface SpBaseInfoApplyManager extends EntityManager<SpBaseInfoApply> {

	public Page<SpBaseInfoApply> findPage(Page<SpBaseInfoApply> page, String orderBy, Map<String, Object> params) throws PlatformException;
	
	public SpBaseInfoApply get(Long id) throws PlatformException;
}

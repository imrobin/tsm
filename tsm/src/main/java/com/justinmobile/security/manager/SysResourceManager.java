package com.justinmobile.security.manager;

import java.util.Collection;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysResource;

@Transactional
public interface SysResourceManager extends EntityManager<SysResource> {
	
	@Transactional(readOnly = true)
	SysResource getResourceByFilterString(String filterString) throws PlatformException;

	Map<Long, String> getResToMap(Collection<SysResource> sysResources) throws PlatformException;
	
	@Transactional(readOnly = true)
	void checkResource(SysResource res) throws PlatformException;
	
}

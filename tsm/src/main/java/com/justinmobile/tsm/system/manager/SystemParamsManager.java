package com.justinmobile.tsm.system.manager;

import java.util.List;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.system.domain.SystemParams;

public interface SystemParamsManager extends EntityManager<SystemParams> {
	
	List<SystemParams> getParamsByType(String type) throws PlatformException;
	
	List<String> getAllParamType() throws PlatformException;
	
	boolean checkExistByTypeAndKey(SystemParams param) throws PlatformException;
}

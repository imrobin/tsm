package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;

public interface LoadModuleDao extends EntityDao<LoadModule, Long> {

	boolean isAidExist(LoadFileVersion loadFileVersion, String aid);
}
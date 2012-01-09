package com.justinmobile.tsm.system.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.system.domain.SystemParams;

public interface SystemParamsDao extends EntityDao<SystemParams, Long> {

	List<String> getAllParamType();

	boolean checkExistByTypeAndKey(SystemParams param);

}

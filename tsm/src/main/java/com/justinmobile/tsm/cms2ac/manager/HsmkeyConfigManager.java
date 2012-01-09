package com.justinmobile.tsm.cms2ac.manager;

import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

public interface HsmkeyConfigManager extends EntityManager<HsmkeyConfig> {

	HsmkeyConfig getByKeyProfileVendor(KeyProfile keyProfile, String vendorName);

	HsmkeyConfig getByKeyProfileVendor(ApplicationKeyProfile keyProfile, String vendorName);

	public Page<HsmkeyConfig> qurySsdRelation(int pageNo, int pageSize, Map<String, String> sortMap, Map<String, Object> filterMap);

	public void update(HsmkeyConfig hsmKeyConfig) throws PlatformException;
}
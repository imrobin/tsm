package com.justinmobile.tsm.cms2ac.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

public interface HsmkeyConfigDao extends EntityDao<HsmkeyConfig, Long> {
	HsmkeyConfig getByKeyProfileVendor(ApplicationKeyProfile keyProfile, String vendorName);

	HsmkeyConfig getByKeyProfileVendor(KeyProfile keyProfile, String vendorName);
}
package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.dto.KeyInfo;

public interface ApplicationKeyProfileDao extends EntityDao<ApplicationKeyProfile, Long> {

	ApplicationKeyProfile getByApplictionAndKeyInfo(Application application, KeyInfo keyInfo);

}
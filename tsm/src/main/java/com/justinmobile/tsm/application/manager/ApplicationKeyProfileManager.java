package com.justinmobile.tsm.application.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.dto.KeyInfo;

@Transactional
public interface ApplicationKeyProfileManager extends EntityManager<ApplicationKeyProfile> {

	ApplicationKeyProfile getByApplictionAndKeyInfo(Application application, KeyInfo keyInfo);

}
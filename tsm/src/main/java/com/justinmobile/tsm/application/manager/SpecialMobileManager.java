package com.justinmobile.tsm.application.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.SpecialMobile;

@Transactional
public interface SpecialMobileManager extends EntityManager<SpecialMobile> {
	
}
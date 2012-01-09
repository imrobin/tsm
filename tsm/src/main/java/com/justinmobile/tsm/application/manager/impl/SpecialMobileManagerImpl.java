package com.justinmobile.tsm.application.manager.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.SpecialMobileDao;
import com.justinmobile.tsm.application.domain.SpecialMobile;
import com.justinmobile.tsm.application.manager.SpecialMobileManager;

@Service("specialMobileManager")
public class SpecialMobileManagerImpl extends EntityManagerImpl<SpecialMobile, SpecialMobileDao> implements SpecialMobileManager {
	
}
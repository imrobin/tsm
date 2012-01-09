package com.justinmobile.tsm.application.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationStyleDao;
import com.justinmobile.tsm.application.domain.ApplicationStyle;
import com.justinmobile.tsm.application.manager.ApplicationStyleManager;

@Service("applicationStyleManager")
public class ApplicationStyleManagerImpl extends EntityManagerImpl<ApplicationStyle, ApplicationStyleDao> implements ApplicationStyleManager {

	@Autowired
	private ApplicationStyleDao applicationStyleDao;
	
}

package com.justinmobile.tsm.application.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationImageDao;
import com.justinmobile.tsm.application.domain.ApplicationImage;
import com.justinmobile.tsm.application.manager.ApplicationImageManager;

@Service("applicationImageManager")
public class ApplicationImageManagerImpl extends EntityManagerImpl<ApplicationImage, ApplicationImageDao> implements ApplicationImageManager {

	@Autowired
	ApplicationImageDao  applicationImageDao;
	
	@Override
	public ApplicationImage loadById(Long id) {
		return applicationImageDao.loadById(id);
	}
	
}
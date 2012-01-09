package com.justinmobile.tsm.application.manager;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationImage;

public interface ApplicationImageManager extends EntityManager<ApplicationImage>{
	public ApplicationImage loadById(Long id);
}
package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.ApplicationImage;

public interface ApplicationImageDao extends EntityDao<ApplicationImage, Long> {
	public ApplicationImage loadById(long id);
}
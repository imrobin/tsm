package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.ApplicationIdentifier;

public interface ApplicationIdentifierDao extends EntityDao<ApplicationIdentifier, Long> {

	public void saveApplicationIdentifiers(List<ApplicationIdentifier> list);
}

package com.justinmobile.tsm.transaction.manager;

import java.util.List;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.transaction.domain.PersonalizeCommand;

public interface PersonalizeCommandManager extends EntityManager<PersonalizeCommand> {
	
	List<PersonalizeCommand> getByAppAidAndBatch(String appAid, int batch, int type) throws PlatformException;
	
	int getMaxBatchIndex(String appAid, int type) throws PlatformException;

}

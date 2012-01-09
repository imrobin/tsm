package com.justinmobile.tsm.transaction.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.transaction.domain.PersonalizeCommand;

public interface PersonalizeCommandDao extends EntityDao<PersonalizeCommand, Long> {

	int getMaxBatchIndex(String appAid, int type);

	List<PersonalizeCommand> getByAppAidAndBatchAsIndexAsc(String appAid, int batch, int type);

}

package com.justinmobile.tsm.transaction.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.transaction.dao.PersonalizeCommandDao;
import com.justinmobile.tsm.transaction.domain.PersonalizeCommand;
import com.justinmobile.tsm.transaction.manager.PersonalizeCommandManager;

@Service("personalizeCommandManager")
public class PersonalizeCommandManagerImpl extends EntityManagerImpl<PersonalizeCommand, PersonalizeCommandDao> implements
		PersonalizeCommandManager {

	@Autowired
	private PersonalizeCommandDao personalizeCommandDao;

	@Override
	public List<PersonalizeCommand> getByAppAidAndBatch(String appAid, int batch, int type) throws PlatformException {
		try {
			return personalizeCommandDao.getByAppAidAndBatchAsIndexAsc(appAid, batch, type);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public int getMaxBatchIndex(String appAid, int type) throws PlatformException {
		try {
			return personalizeCommandDao.getMaxBatchIndex(appAid, type);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}

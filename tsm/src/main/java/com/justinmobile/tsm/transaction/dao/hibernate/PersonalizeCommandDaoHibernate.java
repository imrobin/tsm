package com.justinmobile.tsm.transaction.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.transaction.dao.PersonalizeCommandDao;
import com.justinmobile.tsm.transaction.domain.PersonalizeCommand;

@Repository("personalizeCommandDao")
public class PersonalizeCommandDaoHibernate extends EntityDaoHibernate<PersonalizeCommand, Long> implements PersonalizeCommandDao {

	@Override
	public int getMaxBatchIndex(String appAid, int type) {
		String hql = " select max(pc.batch) from " + PersonalizeCommand.class.getName() + " as pc where pc.appAid = ? and pc.type = ?";
		Integer maxBatch = findUniqueEntity(hql, appAid, type);
		if (maxBatch == null) {
			maxBatch = 0;
		}
		return maxBatch;
	}

	@Override
	public List<PersonalizeCommand> getByAppAidAndBatchAsIndexAsc(String appAid, int batch, int type) {
		String hql = "from " + PersonalizeCommand.class.getName() + " as pc where pc.appAid = ? and pc.batch = ? and pc.type = ? order by pc.cmdIndex asc";
		return find(hql, appAid, batch, type);
	}

}

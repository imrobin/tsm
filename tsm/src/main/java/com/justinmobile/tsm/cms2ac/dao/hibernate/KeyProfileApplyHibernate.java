package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.cms2ac.dao.KeyProfileApplyDao;
import com.justinmobile.tsm.cms2ac.domain.KeyProfileApply;

@Repository("keyProfileApplyDao")
public class KeyProfileApplyHibernate extends EntityDaoHibernate<KeyProfileApply, Long> implements KeyProfileApplyDao {

	@Override
	public void removeAll(Long securityDomainApplyId) {
		Session session = this.getSession();
		SQLQuery query = session.createSQLQuery("delete from KEY_PROFILE_HSMKEY_APPLY a where exists (select 1 from key_profile_apply b where a.KEY_PROFILE_ID = b.ID and b.SD_ID = "+securityDomainApplyId+")");
		int count = query.executeUpdate();
		this.logger.debug("delete KEY_PROFILE_HSMKEY_APPLY : " + count);
		this.batchExecute("delete from KeyProfileApply a where a.securityDomainApply.id = ?", securityDomainApplyId);
	}
}

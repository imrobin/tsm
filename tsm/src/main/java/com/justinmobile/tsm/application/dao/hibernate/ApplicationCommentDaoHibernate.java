package com.justinmobile.tsm.application.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationCommentDao;
import com.justinmobile.tsm.application.domain.ApplicationComment;

@Repository("applicationCommentDao")
public class ApplicationCommentDaoHibernate extends EntityDaoHibernate<ApplicationComment, Long> implements ApplicationCommentDao {

	@Override
	public Integer countComments(long appId) {
		String hql = "select count(*) from " + ApplicationComment.class.getName() + " as ac where ac.application.id = ?";
		return findUniqueEntity(hql, appId);
	}

	@Override
	public Integer isCommented(long appId, long customerId) {
		String hql = "select count(*) from " + ApplicationComment.class.getName() + " as ac where ac.application.id = ? and ac.customer.id=?";
		return Integer.parseInt(findUniqueEntity(hql, appId, customerId)+"");
	}

	@Override
	public ApplicationComment getByAppIdAndCustomerId(long appId, long customerId) {
		String hql = "from " + ApplicationComment.class.getName() + " as ac where ac.application.id = ? and ac.customer.id=?";
		return findUniqueEntity(hql, appId, customerId);
	}
}
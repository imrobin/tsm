package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.ApplicationComment;

public interface ApplicationCommentDao extends EntityDao<ApplicationComment, Long> {

	Integer countComments(long appId);

	Integer isCommented(long appId, long customerId);

	ApplicationComment getByAppIdAndCustomerId(long appId, long customerId);
}
package com.justinmobile.tsm.cms2ac.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.cms2ac.dao.TaskDao;
import com.justinmobile.tsm.cms2ac.domain.Task;

@Repository("taskDao")
public class TaskDaoHibernate extends EntityDaoHibernate<Task, Long> implements TaskDao {
}
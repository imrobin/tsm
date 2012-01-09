package com.justinmobile.tsm.cms2ac.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.cms2ac.dao.TaskDao;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;

@Service("taskManager")
public class TaskManagerImpl extends EntityManagerImpl<Task, TaskDao> implements TaskManager {

	@SuppressWarnings("unused")
	@Autowired
	private TaskDao taskDao;}
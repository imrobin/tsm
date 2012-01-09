package com.justinmobile.tsm.application.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.GradeStatisticsDao;
import com.justinmobile.tsm.application.domain.GradeStatistics;
import com.justinmobile.tsm.application.manager.GradeStatisticsManager;

@Service("gradeStatisticsManager")
public class GradeStatisticsManagerImpl extends EntityManagerImpl<GradeStatistics, GradeStatisticsDao> implements GradeStatisticsManager {

	@SuppressWarnings("unused")
	@Autowired
	private GradeStatisticsDao gradeStatisticsDao;}
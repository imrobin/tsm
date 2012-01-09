package com.justinmobile.tsm.application.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.GradeStatisticsDao;
import com.justinmobile.tsm.application.domain.GradeStatistics;

@Repository("gradeStatisticsDao")
public class GradeStatisticsDaoHibernate extends EntityDaoHibernate<GradeStatistics, Long> implements GradeStatisticsDao {
}
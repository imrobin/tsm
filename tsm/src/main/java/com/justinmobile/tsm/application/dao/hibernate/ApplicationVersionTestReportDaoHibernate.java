package com.justinmobile.tsm.application.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.ApplicationVersionTestReportDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Repository("onlineTestDao")
public class ApplicationVersionTestReportDaoHibernate extends EntityDaoHibernate<ApplicationVersionTestReport, Long> implements ApplicationVersionTestReportDao {

	@Override
	public ApplicationVersionTestReport getReportByAppver(ApplicationVersion av) {
		return this.findUniqueByProperty("appVer", av);
	}

	@Override
	public Page<ApplicationVersionTestReport> findByAppverAndTestpass(Page<ApplicationVersionTestReport> page, ApplicationVersion av) {
		String hql = "from " + ApplicationVersionTestReport.class.getName() + " as avtr where avtr.appVer = ? and avtr.result = ?";
		return this.findPage(page, hql, av, ApplicationVersionTestReport.RESULT_PASS);
	}

	@Override
	public List<ApplicationVersionTestReport> findByAppVerAndCardBase(ApplicationVersion av, CardBaseInfo cbi) {
		String hql = "from " + ApplicationVersionTestReport.class.getName() + " as avtr where avtr.appVer = ? and avtr.cardBaseInfo = ?";
		return this.find( hql, av, cbi);
	}
	
}
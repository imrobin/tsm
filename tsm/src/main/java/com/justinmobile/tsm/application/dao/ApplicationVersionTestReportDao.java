package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

public interface ApplicationVersionTestReportDao extends EntityDao<ApplicationVersionTestReport, Long> {

	ApplicationVersionTestReport getReportByAppver(ApplicationVersion av);

	Page<ApplicationVersionTestReport> findByAppverAndTestpass(Page<ApplicationVersionTestReport> page, ApplicationVersion av);

	List<ApplicationVersionTestReport> findByAppVerAndCardBase(ApplicationVersion av, CardBaseInfo cbi);


}
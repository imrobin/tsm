package com.justinmobile.tsm.application.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Transactional
public interface ApplicationVersionTestReportManager extends EntityManager<ApplicationVersionTestReport> {

	ApplicationVersionTestReport getReportByAppver(Long appVerId);

	List<ApplicationVersionTestReport> findByAppver(ApplicationVersion av);

	public Page<ApplicationVersionTestReport> findByAppverAndTestpass(Page<ApplicationVersionTestReport> page, Long appVerId);

	List<ApplicationVersionTestReport> findByAppVerAndCardBase(ApplicationVersion av, CardBaseInfo cbi);


}
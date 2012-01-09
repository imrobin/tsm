package com.justinmobile.tsm.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.utils.hibernate.OpenSessionInMethod;
import com.justinmobile.tsm.fee.manager.FeeStatManager;

public class FeeStatTask {

	@Autowired
	private FeeStatManager fsManager;

	@Autowired
	private OpenSessionInMethod openSession;
	
	private static final Logger log = LoggerFactory.getLogger(FeeStatTask.class);

	public void genFeeStatByMonth() {
		log.info("==========>fee stat task is excuting now !");
		openSession.openSession();
		fsManager.genFeeStatTask();
		openSession.releaseSession();
		log.info("==========>fee stat task has finished !");
	}
}

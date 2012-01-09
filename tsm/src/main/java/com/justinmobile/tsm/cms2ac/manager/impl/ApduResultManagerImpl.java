package com.justinmobile.tsm.cms2ac.manager.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.tsm.cms2ac.dao.ApduResultDao;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.tsm.cms2ac.manager.ApduResultManager;
import com.justinmobile.core.manager.EntityManagerImpl;

@Service("apduResultManager")
public class ApduResultManagerImpl extends EntityManagerImpl<ApduResult, ApduResultDao> implements ApduResultManager {

	@SuppressWarnings("unused")
	@Autowired
	private ApduResultDao apduResultDao;}
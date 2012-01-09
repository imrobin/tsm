package com.justinmobile.tsm.cms2ac.manager.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.tsm.cms2ac.dao.Cms2acParamDao;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.manager.Cms2acParamManager;
import com.justinmobile.core.manager.EntityManagerImpl;

@Service("cms2acParamManager")
public class Cms2acParamManagerImpl extends EntityManagerImpl<Cms2acParam, Cms2acParamDao> implements Cms2acParamManager {

	@SuppressWarnings("unused")
	@Autowired
	private Cms2acParamDao cm2acParamDao;}
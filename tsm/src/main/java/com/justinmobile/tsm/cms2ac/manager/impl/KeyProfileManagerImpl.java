package com.justinmobile.tsm.cms2ac.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.cms2ac.dao.KeyProfileDao;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.manager.KeyProfileManager;

@Service("keyProfileManager")
public class KeyProfileManagerImpl extends EntityManagerImpl<KeyProfile, KeyProfileDao> implements KeyProfileManager {

	@SuppressWarnings("unused")
	@Autowired
	private KeyProfileDao keyProfileDao;}
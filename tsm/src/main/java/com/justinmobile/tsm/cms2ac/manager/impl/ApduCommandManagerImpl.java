package com.justinmobile.tsm.cms2ac.manager.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.tsm.cms2ac.dao.ApduCommandDao;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.manager.ApduCommandManager;
import com.justinmobile.core.manager.EntityManagerImpl;

@Service("apduCommandManager")
public class ApduCommandManagerImpl extends EntityManagerImpl<ApduCommand, ApduCommandDao> implements ApduCommandManager {

	@SuppressWarnings("unused")
	@Autowired
	private ApduCommandDao apduCommandDao;}
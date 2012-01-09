package com.justinmobile.tsm.sp.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.sp.dao.RecommendSpDao;
import com.justinmobile.tsm.sp.domain.RecommendSp;
import com.justinmobile.tsm.sp.manager.RecommendSpManager;

@Service("recommendSpManager")
public class RecommendSpManagerImpl extends EntityManagerImpl<RecommendSp, RecommendSpDao>
		implements RecommendSpManager {

	@SuppressWarnings("unused")
	@Autowired
	private RecommendSpDao RecommendSpDao;

}
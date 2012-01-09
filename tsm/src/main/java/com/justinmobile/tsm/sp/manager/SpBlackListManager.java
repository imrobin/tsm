package com.justinmobile.tsm.sp.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.sp.domain.SpBlackList;

@Transactional
public interface SpBlackListManager extends EntityManager<SpBlackList>{

	/**
	* @Title: addBlackList
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param blackList 
	*/
	public void addBlackList(SpBlackList blackList);

	/**
	* @Title: removeBlackList
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param blackList 
	*/
	public void removeBlackList(SpBlackList blackList);
}
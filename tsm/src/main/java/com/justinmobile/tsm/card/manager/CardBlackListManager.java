package com.justinmobile.tsm.card.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.card.domain.CardBlackList;

@Transactional
public interface CardBlackListManager extends EntityManager<CardBlackList>{

	/**
	* @Title: add
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param blackList 包装好的黑名单
	* @param CustomerCardId  需要同步的customerCardInfo记录
	*/
	public void addBlackList(CardBlackList blackList);


	/**
	* @Title: removeBlackList
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param blackList 
	*/
	public void removeBlackList(CardBlackList blackList);
}
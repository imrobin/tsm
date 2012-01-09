package com.justinmobile.tsm.system.manager;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.tsm.system.domain.Requistion;

public interface RequistionManager extends EntityManager<Requistion> {

	Page<Requistion> findPageByParam(Page<Requistion> page, Map<String, String> paramMap);

	Requistion getRequistionByTypeAndId(Integer type, Long originalId);

	/**
	 * 查找指定的类型、状态、originalId的申请数目
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param originalId
	 * @return 满足条件的申请数目
	 */
	int getCountByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId);

	JsonMessage updatePublish(String status, int typeOriginal,String sdIdsStr,String opinion, Requistion ac, String typeTk,
			String typeKek, String hsmkeyConfigTK, String hsmkeyConfigKEK);

	/**
	 * 查找指定的类型、状态、originalId的申请
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param originalId
	 * @return 满足条件的申请
	 */
	List<Requistion> getByTypeAndStatusAndOrignaId(Integer type, Integer status, Long originalId);
}
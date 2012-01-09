package com.justinmobile.tsm.system.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.system.domain.Requistion;

public interface RequistionDao extends EntityDao<Requistion, Long> {

	Page<Requistion> findPageByParam(Page<Requistion> page, Map<String, String> paramMap, SysUser currentUser);

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

	/**
	 * 删除指定类型的记录
	 * 
	 * @param type
	 * @param originalId
	 * @return
	 */
	boolean deleteRequistions(Integer type, Long originalId);

	/**
	 * 查询指定类型的申请记录
	 * 
	 * @param page
	 * @param originalId
	 * @param types
	 * @return
	 */
	Page<Requistion> findPageByType(Page<Requistion> page, Long originalId, Integer... types);

	Page<Requistion> findPageForSD(Page<Requistion> page, Long originalId);

	Page<Requistion> findPageForSD(Page<Requistion> page);

	Requistion findRequistionByOriginalIdAndType(Long originalId, Integer... type);

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
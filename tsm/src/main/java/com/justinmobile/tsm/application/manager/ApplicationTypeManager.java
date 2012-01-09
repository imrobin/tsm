package com.justinmobile.tsm.application.manager;

import java.util.List;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationType;

public interface ApplicationTypeManager extends EntityManager<ApplicationType>{

	Page<ApplicationType> getChild(Page<ApplicationType> page, Long parentId) throws PlatformException;

	void checkName(ApplicationType type) throws PlatformException;

	/**
	 * 设置首页显示的分类列表
	 * @param idArray
	 */
	void setShowIndex(String[] idArray);

	List<ApplicationType> getShowIndexTypeList();

	List<ApplicationType> getShowIndexTypeListOrderById();

	List<ApplicationType> getAllTopLevel();
}
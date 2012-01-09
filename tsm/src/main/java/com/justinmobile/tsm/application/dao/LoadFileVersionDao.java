package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

public interface LoadFileVersionDao extends EntityDao<LoadFileVersion, Long> {

	/**
	 * 查找某应用版本所使用的加载文件
	 * 
	 * @param applicationVersionId
	 *            应用版本的ID
	 * @return 查找结果
	 */
	List<LoadFileVersion> getByApplicationVersion(Long applicationVersionId);

	/**
	 * 查找属于指定加载文件和指定版本号的加载文件版本数
	 * 
	 * @param loadFile
	 * @param versionNo
	 * @return
	 */
	int getCountByLoadFileAndVersionNo(LoadFile loadFile, String versionNo);

	/**
	 * 查找某应用版本所使用的共享的加载文件
	 * 
	 * @deprecated
	 * @param applicationVersionId
	 *            应用版本的ID
	 * @param shareFlag
	 *            加载文件的共享类型
	 * @return 查找结果
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersion(Long applicationVersionId, Integer shareFlag);

	/**
	 * 根据加载文件的类型查找被指定应用版本使用的加载文件版本，根据加载文件AID升序排序
	 * 
	 * @param applicationVersion
	 *            指定应用版本
	 * @param type
	 *            加载文件的类型
	 * @return
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersionAndType(ApplicationVersion applicationVersion, int type);

	/**
	 * 获取指定应用版本所使用的加载文件版本，根据加载文件AID升序排序
	 * 
	 * @param applicationVersion
	 *            指定的应用版本
	 * @return
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersionOrderByAidAsc(ApplicationVersion applicationVersion);
}
package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

public interface ApplicationLoadFileDao extends EntityDao<ApplicationLoadFile, Long> {

	/**
	 * 查找指定应用版本和加载文件版本的关联类
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * @return 关联类
	 * 
	 * @deprecated getByApplicationVersionAndLoadFileVersion(ApplicationVersion
	 *             applicationVersion, LoadFileVersion loadFileVersion)
	 */
	ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(Long applicationVersionId, Long loadFileVersionId);

	/**
	 * 查找指定应用版本和加载文件版本的关联类
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param loadFileVersion
	 *            加载文件版本
	 * @return 关联类
	 */
	ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(ApplicationVersion applicationVersion, LoadFileVersion loadFileVersion);

	/**
	 * 获取一个应用版本所使用的加载文件，按照下载顺序升序排序，如果下载顺序相同则按照ID升序排序
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	List<ApplicationLoadFile> getExclusiveByDownloadOrder(Long applicationVersionId);

	/**
	 * 获取一个应用版本所使用的加载文件，按照删除顺序升序排序，如果删除顺序相同则按照ID降序排序
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	List<ApplicationLoadFile> getExclusiveByDeleteOrder(Long applicationVersionId);

	List<ApplicationLoadFile> getByApplicationVersionAsDownloadOrder(ApplicationVersion applicationVersion);

	List<ApplicationLoadFile> getByApplicationVersionAsDeleteOrder(ApplicationVersion applicationVersion);

	/**
	 * 根据应用版本和加载文件查找唯一记录
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param loadFile
	 *            加载文件
	 * @return 检索结果<br/>
	 *         null-如果满足条件的记录不存在
	 */
	ApplicationLoadFile getByApplicationVersionAndLoadFile(ApplicationVersion applicationVersion, LoadFile loadFile);

}
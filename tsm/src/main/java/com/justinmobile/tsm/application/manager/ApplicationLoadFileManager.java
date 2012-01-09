package com.justinmobile.tsm.application.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;

@Transactional
public interface ApplicationLoadFileManager extends EntityManager<ApplicationLoadFile> {

	/**
	 * 设置下载顺序
	 * 
	 * @param loadFileVersionId
	 *            待设置的加载文件版本ID
	 * @param applicationVersionId
	 *            待设置的应用版本ID
	 * @param order
	 *            下载顺序
	 * @param username
	 *            发起请求的用户的用户名
	 */
	void setDownloadOrder(Long loadFileVersionId, Long applicationVersionId, Integer order, String username);

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

	/**
	 * 设置删除顺序
	 * 
	 * @param loadFileVersionId
	 *            待设置的加载文件版本ID
	 * @param applicationVersionId
	 *            待设置的应用版本ID
	 * @param order
	 *            删除顺序
	 * @param username
	 *            发起请求的用户的用户名
	 */
	void setDeleteOrder(Long loadFileVersionId, Long applicationVersionId, Integer order, String username);

	/**
	 * 获取从指定加载文件版本生成、被指定应用版本使用的实例
	 * 
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return
	 */
	ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(Long loadFileVersionId, Long applicationVersionId);

	/**
	 * 移除应用版本对加载文件版本的引入关系
	 * 
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * @param applicationVersionId
	 *            应用版本ID
	 * @param username
	 *            发起请求的用户的用户名
	 * @throws APPLICAION_REMOVE_IMPORT_SP_DISCARD
	 *             用户无权进行该操作
	 */
	void removeImportBetweenLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId, String username);

	/**
	 * 建立应用版本对加载文件版本的引入关系
	 * 
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * @param applicationVersionId
	 *            应用版本ID
	 * @param username
	 *            发起请求的用户的用户名
	 * @return 建立的引入关系
	 * @throws APPLICAION_BUILD_IMPORT_SP_DISCARD
	 *             用户无权进行该操作
	 */
	ApplicationLoadFile buildImportBetweenLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId,
			String username);

	/**
	 * 计算加载文件版本的下载顺序，计算完成后保存数据库
	 * 
	 * @param applicationVersion
	 *            加载文件版本
	 */
	void sortDownloadOrder(ApplicationVersion applicationVersion);

	/**
	 * 计算加载文件版本的删除顺序，计算完成后保存数据库
	 * 
	 * @param applicationVersion
	 *            加载文件版本
	 */
	void sortDeleteOrder(ApplicationVersion applicationVersion);

	/**
	 * 判断引用关系能否被修改<br/>
	 * 结果由发起请求的用户修改对引用关系的应用版本的修改权限决定
	 * 
	 * @param username
	 * @param applicationLoadFile
	 * @return
	 */
	boolean isEditable(String username, ApplicationLoadFile applicationLoadFile);

	/**
	 * 获取指定应用版本的下载顺序
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @return
	 */
	List<ApplicationLoadFile> getAllByDownloadOrder(ApplicationVersion applicationVersion);

	/**
	 * 获取指定应用版本的删除顺序
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @return
	 */
	List<ApplicationLoadFile> getAllByDeleteOrder(ApplicationVersion applicationVersion);

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
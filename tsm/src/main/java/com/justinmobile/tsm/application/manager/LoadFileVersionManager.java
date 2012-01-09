package com.justinmobile.tsm.application.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Transactional
public interface LoadFileVersionManager extends EntityManager<LoadFileVersion> {

	/**
	 * 查找某应用版本所使用的加载文件
	 * 
	 * @param applicationVersionId
	 *            应用版本的ID
	 * @return 查找结果
	 */
	List<LoadFileVersion> getByApplicationVersion(Long applicationVersionId);

	/**
	 * 添加新的加载文件版本，新添加的加载文件版本将被指定的应用版本引入
	 * 
	 * @param loadFileVersion
	 *            加载文件版本
	 * @param params
	 *            其他参数，包括
	 *            <ul>
	 *            <li>tempDir：临时目录路径</li>
	 *            <li>tempFileAbsPath：上传的Cap文件在服务器临时文件的绝对路径</li>
	 *            <li>loadFileId：所属加载文件ID</li>
	 *            <li>applicationVersionId：被引入的应用版本ID</li>
	 *            </ul>
	 * @param username
	 *            发起请求的用户名
	 */
	void createNewLoadFileVersionForApplicaitonVersion(LoadFileVersion loadFileVersion, Map<String, String> params, String username);

	/**
	 * @deprecated 查找某应用版本所使用的共享的加载文件
	 * 
	 * @param applicationVersionId
	 *            应用版本的ID
	 * @param shareFlag
	 *            加载文件的共享类型
	 * @return 查找结果
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersion(Long applicationVersionId, Integer shareFlag);

	/**
	 * 计算所以赖的公用加载文件的下载顺序
	 * 
	 * @param origins
	 *            直接依赖的公用加载文件集合
	 * @return
	 */
	List<LoadFileVersion> calcDependenceAsDownloadOrder(Collection<LoadFileVersion> origins);

	/**
	 * 计算所以赖的公用加载文件的删除顺序
	 * 
	 * @param origins
	 *            直接依赖的公用加载文件集合
	 * @return
	 */
	List<LoadFileVersion> calcDependenceAsDeleteOrder(Collection<LoadFileVersion> origins);

	/**
	 * 判断加载文件版本能否被发起请求的用户修改<br />
	 * 结果由发起请求的用户修改对加载文件版本所属加载文件的修改权限决定
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param loadFileVersion
	 *            被修改的加载文件版本
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	boolean isEditable(String username, LoadFileVersion loadFileVersion);

	/**
	 * 添加新的加载文件版本
	 * 
	 * @param loadFileVersion
	 *            加载文件版本
	 * @param params
	 *            其他参数，包括
	 *            <ul>
	 *            <li>tempDir：临时目录路径</li>
	 *            <li>tempFileAbsPath：上传的Cap文件在服务器临时文件的绝对路径</li>
	 *            <li>loadFileId：所属加载文件ID</li>
	 *            </ul>
	 * @param username
	 *            发起请求的用户名
	 */
	void createNewLoadFileVersion(LoadFileVersion loadFileVersion, Map<String, String> params, String username);

	/**
	 * 为加载文件版本添加依赖信息
	 * 
	 * @param parent
	 *            被依赖的加载文件版本
	 * @param child
	 *            添加依赖的加载文件版本
	 * @param username
	 *            发起请求的用户名
	 * @throws PlatformErrorCode.LOAD_FILE_SP_DISCARD
	 *             发起请求的用户无权修改加载文件版本时
	 */
	void addDependence(LoadFileVersion parent, LoadFileVersion child, String username);

	/**
	 * 为加载文件版本删除依赖信息
	 * 
	 * @param parent
	 *            被依赖的加载文件版本
	 * @param child
	 *            删除依赖的加载文件版本
	 * @param username
	 *            发起请求的用户名
	 * @throws PlatformErrorCode.LOAD_FILE_SP_DISCARD
	 *             发起请求的用户无权修改加载文件版本时
	 */
	void removeDependence(LoadFileVersion parent, LoadFileVersion child, String username);

	Page<LoadFileVersion> findUnLinkPage(Page<LoadFileVersion> page, String cardBaseId);

	/**
	 * 验证加载文件版本是否有循环依赖
	 * 
	 * @param loadFileVersion
	 *            待验证循环依赖的加载文件版本
	 */
	void validateCircularDependence(LoadFileVersion loadFileVersion);

	/**
	 * 查找被指定应用版本使用的、类型为cms2ac加载文件的文件版本
	 * 
	 * @param applicationVersion
	 *            指定应用版本
	 * @param fileType
	 *            文件类型
	 * @return
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersionAndType(ApplicationVersion applicationVersion, int fileType);

	/**
	 * 如果该加载文件版本没有被任何应用版本引入，删除该加载文件版本
	 * 
	 * @param loadFileVersion
	 */
	void removeIfNotImport(LoadFileVersion loadFileVersion);

	/**
	 * 获取指定应用版本所使用的加载文件版本
	 * 
	 * @param applicationVersion
	 *            指定的应用版本
	 * @return
	 */
	List<LoadFileVersion> getWhichImportedByApplicationVersion(ApplicationVersion applicationVersion);
}
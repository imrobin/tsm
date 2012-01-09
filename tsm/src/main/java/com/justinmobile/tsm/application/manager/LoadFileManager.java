package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Transactional
public interface LoadFileManager extends EntityManager<LoadFile> {

	/**
	 * 在应用创建新版本时上传新的加载文件，新创建的加载文件是私有的
	 * 
	 * @param loadFile
	 *            新上传的加载文件
	 * @param loadFileVersion
	 *            加载文件的初始版本
	 * @param params
	 *            其他参数，包括
	 *            <ul>
	 *            <li>tempDir：临时目录路径</li>
	 *            <li>tempFileAbsPath：上传的Cap文件在服务器临时文件的绝对路径</li>
	 *            <li>sdId：所属安全域ID<br/>
	 *            如果指定的所属安全域是主安全域或第三方辅助安全域，此字段忽略<br/>
	 *            如果指定的所属安全域是DAP安全域或Token安全域，此字段必须存在</li>
	 *            <li>applicationVersionId：引入的应用版本ID</li>
	 *            </ul>
	 * @param username
	 *            发起请求的用户
	 */
	void createNewLoadFileForApplicationVersion(LoadFile loadFile, LoadFileVersion loadFileVersion, Map<String, String> params,
			String username);

	/**
	 * 判断加载文件能否被发起请求的用户修改<br />
	 * 只有加载文件的拥有者可以修改加载文件
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param loadFile
	 *            被修改的加载文件
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	public boolean isEditable(String username, LoadFile loadFile);

	/**
	 * 获取属于指定Sp且未被指定应用版本引入的私有加载文件
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @param username
	 *            sp的用户名
	 * @return 检索结果
	 */
	List<LoadFile> getExclusiveLoadFilesBySpAndApplicationVersion(Long applicationVersionId, String username);

	/**
	 * 获取未被指定应用版本引入的共享加载文件
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	List<LoadFile> getSharedLoadFilesWhichUnassociateWithApplicationVersion(Long applicationVersionId);

	/**
	 * 上传新的共享加载文件
	 * 
	 * @param loadFile
	 *            新上传的加载文件
	 * @param loadFileVersion
	 *            加载文件的初始版本
	 * @param params
	 *            其他参数，包括
	 *            <ul>
	 *            <li>tempDir：临时目录路径</li>
	 *            <li>tempFileAbsPath：上传的Cap文件在服务器临时文件的绝对路径</li>
	 *            <li>sdId：所属安全域ID<br/>
	 *            如果指定的所属安全域是主安全域或第三方辅助安全域，此字段忽略<br/>
	 *            如果指定的所属安全域是DAP安全域或Token安全域，此字段必须存在</li>
	 *            </ul>
	 * @param username
	 *            发起请求的用户
	 */
	void createNewSharedLoadFile(LoadFile loadFile, LoadFileVersion loadFileVersion, Map<String, String> params, String username);

	/**
	 * 查找所有版本都未被指定加载文件版本依赖的加载文件
	 * 
	 * @param loadFileVersion
	 *            指定加载文件版本
	 * @return
	 */
	List<LoadFile> getUndependentLoadFiles(LoadFileVersion loadFileVersion);

	Page<LoadFile> loadByIds(Page<LoadFile> page, String loadFileIds);

	/**
	 * 验证加载文件的AID的唯一性<br/>
	 * 加载文件的AID不能与以下对象AID重复
	 * <ul>
	 * <li>应用</li>
	 * <li>其他加载文件</li>
	 * <li>模块</li>
	 * <li>实例</li>
	 * <li>安全域</li>
	 * </ul>
	 * 
	 * @param aid
	 * 
	 * @throws LOAD_FILE_AID_REDULICATE
	 *             如果AID重复
	 */
	void validateAid(String aid);

	/**
	 * 根据AID查找加载文件
	 * 
	 * @param aid
	 * @return
	 */
	LoadFile getByAid(String aid);

	/**
	 * 删除加载文件
	 * 
	 * @param loadFile
	 *            加载文件
	 * @param username
	 *            发起请求的用户的用户名
	 * @throws LOAD_FILE_SP_DISCARD
	 *             用户无权对加载文件进行操作
	 * @throws LOAD_FILE_USED
	 *             加载文件的一个或多个版本已经被应用使用
	 * @throws LOAD_FILE_DEPENDED
	 *             加载文件的一个或多个版本已经被其他文件依赖
	 */
	void remove(LoadFile loadFile, String username) throws PlatformException;

	/**
	 * 查找未被指定应用版本使用但被指定应用版本所属应用的其他版本使用的、类型为指定类型的加载文件
	 * 
	 * @param applicationVersion
	 *            指定的加载文件
	 * @param fileType
	 *            加载文件的类型
	 * @return 查找结果
	 */
	List<LoadFile> getUnusedByApplicationVersionAndType(ApplicationVersion applicationVersion, int fileType);

	/**
	 * 如果该加载文件没有任何版本，则删除该加载文件
	 * 
	 * @param loadFile
	 */
	void removeIfNotHasVersion(LoadFile loadFile);

}
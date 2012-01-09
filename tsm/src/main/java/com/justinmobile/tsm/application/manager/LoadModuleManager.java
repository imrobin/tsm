package com.justinmobile.tsm.application.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.LoadModule;

@Transactional
public interface LoadModuleManager extends EntityManager<LoadModule> {

	/**
	 * 创建一个新的模块
	 * 
	 * @param loadModule
	 *            待创建的模块
	 * @param loadFileVersionId
	 *            模块所属加载文件版本的ID
	 * @param username
	 *            请求创建模块的用户的用户名
	 * @throws PlatformErrorCode.LOAD_FILE_SP_DISCARD
	 *             当用户请求添加模块的加载文件不属于请求者时
	 * @throws PlatformErrorCode.LOAD_MODULE_AID_REDUPLICATE
	 *             当模块AID与所属加载文件版本其他模块AID重复时
	 */
	void createNewLoadModule(LoadModule loadModule, long loadFileVersionId, String username);

	/**
	 * 移除一个模块
	 * 
	 * @param loadModuleId
	 *            被移除模块ID
	 * @param username
	 *            发起请求的用户的用户名
	 */
	void removeLoadModule(Long loadModuleId, String username);

	/**
	 * 判断模块能否被发起请求的用户修改<br />
	 * 结果由发起请求的用户修改对模块所属加载文件版本的修改权限决定
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param loadModule
	 *            被修改的加载文件版本
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	boolean isEditable(String username, LoadModule loadModule);
}
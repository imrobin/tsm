package com.justinmobile.tsm.application.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadModule;

@Transactional
public interface AppletManager extends EntityManager<Applet> {

	/**
	 * SP创建新实例
	 * 
	 * @param applet
	 *            实例
	 * @param applicationVersionId
	 *            使用实例的应用版本ID
	 * @param loadModuleId
	 *            生成实例的模块ID
	 * @param username
	 *            SP的用户名
	 */
	void createNewApplet(Applet applet, Long applicationVersionId, Long loadModuleId, String username) throws PlatformException;

	/**
	 * 获得指定应用版本的所有实例，按安装顺序升序排序
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	List<Applet> getInstallOrder(Long applicationVersionId) throws PlatformException;

	/**
	 * SP创建新实例
	 * 
	 * @param appletId
	 *            实例ID
	 * @param order
	 *            安装顺序
	 * @param username
	 *            SP的用户名
	 */
	void setInstallOrder(Long appletId, Integer order, String username) throws PlatformException;

	/**
	 * 获取从指定加载文件版本生成并被指定应用版本使用的实例
	 * 
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 查询结果
	 */
	@Transactional(readOnly = true)
	List<Applet> getAppletGeneratLoadFileVersionAndImportApplicationVersion(Long loadFileVersionId, Long applicationVersionId)
			throws PlatformException;

	/**
	 * 删除指定的实例
	 * 
	 * @param appletId
	 *            实例的ID
	 * @param username
	 *            发起请求的用户的用户名
	 * @throws APPLICAION_REMOVE_APPLET_SP_DISCARD
	 *             当使用实例的应用不属于发起请求的用户时
	 */
	void removeApplet(Long appletId, String username) throws PlatformException;

	/**
	 * 查找属于指定模块的实例数目
	 * 
	 * @param loadModuleId
	 *            模块ID
	 * @return 属于指定模块的实例数目
	 */
	@Transactional(readOnly = true)
	int getCountThatBelongLoadModule(long loadModuleId) throws PlatformException;

	/**
	 * 判断实例能否被发起请求的用户修改<br />
	 * 结果由发起请求的用户修改对使用实例的应用版本修改权限决定
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param applet
	 *            被修改的实例
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	@Transactional(readOnly = true)
	boolean isEditable(String username, Applet applet) throws PlatformException;

	@Transactional(readOnly = true)
	List<Applet> getByAid(String aid) throws PlatformException;

	/**
	 * 根据AID查找创建于指定模块且属于指定应用版本的实例
	 * 
	 * @param aid
	 *            aid
	 * @param loadModule
	 *            指定模块
	 * @param applicationVersion
	 *            指定应用版本
	 * @return 满足条件的实例<br/>
	 *         null-如果满足条件的实例不存在
	 */
	Applet getByAidAndLoadModuleAndApplicationVersion(String aid, LoadModule loadModule, ApplicationVersion applicationVersion);
}
package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadModule;

public interface AppletDao extends EntityDao<Applet, Long> {

	/**
	 * 获得指定应用版本的所有实例，按安装顺序升序排列，如果安装顺序相同则按照ID升序排列
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	List<Applet> getByInstallOrder(Long applicationVersionId);

	/**
	 * 根据应用版本和加载文件版本获取实例
	 * 
	 * @param loadFileVersionId
	 *            加载文件版本ID
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return
	 */
	List<Applet> getByLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId);

	/**
	 * 查找属于指定模块的实例数目
	 * 
	 * @param loadModuleId
	 *            模块ID
	 * @return 属于指定模块的实例数目
	 */
	int getCountThatBelongLoadModule(long loadModuleId);

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
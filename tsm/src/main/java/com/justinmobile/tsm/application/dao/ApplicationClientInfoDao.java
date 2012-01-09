package com.justinmobile.tsm.application.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;

public interface ApplicationClientInfoDao extends EntityDao<ApplicationClientInfo, Long> {

	/**
	 * 找到指定应用版本的客户端
	 * 
	 * @param page
	 *            分页信息
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	Page<ApplicationClientInfo> getByApplicationVersion(Page<ApplicationClientInfo> page, Long applicationVersionId);

	/**
	 * 
	 * 根据应用版本和平台信息查找客户端
	 * 
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param sysRequirment
	 *            平台信息
	 * @param fileType
	 *            文件类型
	 * @param version
	 *            客户端版本号
	 * @return
	 */
	ApplicationClientInfo getByApplicationVersionAndSysRequirmentAndFileTypeAndVersion(ApplicationVersion applicationVersion,
			String sysRequirment, String fileType, String version);

	/**
	 * 根据应用版本，平台信息查找客户端
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param sysType
	 *            系统类型
	 * @param sysRequirment
	 *            平台信息
	 * @param fileType
	 *            文件类型
	 */
	ApplicationClientInfo getByApplicationVersionTypeVersionFileType(ApplicationVersion applicationVersion, String sysType,
			String sysRequirment, String fileType);

	/**
	 * 查询应用管理器
	 * 
	 * @param page
	 *            分页参数
	 * @param Map参数
	 */
	Page<ApplicationClientInfo> getApplicationClientInfoForIndex(final Page<ApplicationClientInfo> page, Map<String, Object> values);
	
	/**
	 * 根据系统类型查找应用管理器的历史版本,
	 * @param sysType
	 * 
	 * @param fileType
	 */
	List<Map<String,Object>> getHistoryVersion(String sysType,String sysRequirement);
    
	/**
	 * 根据业务类型,系统版本获取应用客户端的最大开发版本
	 * @param busiType 业务类型
	 * @param sysType 系统类型
	 * @param sysRequirement 系统版本
	 */
	Integer getMaxVersionCode(Integer busiType,String sysType,String sysRequirement);
	/**
	 * 根据业务类型,系统版本,应用版本，系统版本获取应用客户端的最大开发版本
	 * @param busiType 业务类型
	 * @param sysType 系统类型
	 * @param sysRequirement 系统版本
	 * @param AppVer 应用版本
	 */
	Integer getMaxVersionCodeByAppVer(Integer busiType,String sysType,String sysRequirement,Long appVerId);
	/**
	 *  获取手机钱包的最大版本号
	 *  @param card
	 */
	String getMocamMaxVersion();
}
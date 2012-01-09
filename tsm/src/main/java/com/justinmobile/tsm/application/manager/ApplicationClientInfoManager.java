package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;

@Transactional
public interface ApplicationClientInfoManager extends EntityManager<ApplicationClientInfo> {

	/**
	 * 上传一个新的应用客户端
	 * 
	 * @param client
	 *            应用客户端
	 * @param tempFileAbsPath
	 *            临时文件绝对路径
	 * @param saveFileAbsDir
	 *            保存目录就对路径
	 * @param applicationVersionId
	 *            所属应用版本ID
	 * @param filename
	 *            客户端保存的文件名
	 * @param tempIconAbsPath
	 *            图标临时文件的绝对路径
	 */
	void uploadApplicationClient(ApplicationClientInfo client, String tempFileAbsPath, String saveFileAbsDir, long applicationVersionId,
			String filename, String tempIconAbsPath);

	/**
	 * 找到指定应用版本的客户端
	 * 
	 * @param page
	 *            分页信息
	 * @param applicationVersionId
	 *            应用版本ID
	 * @return 检索结果
	 */
	Page<ApplicationClientInfo> getByApplicationVersion(Page<ApplicationClientInfo> page, Long applicationVersionId);

	/**
	 * 根据应用版本，平台信息查找客户端
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param sysRequirment
	 *            平台信息
	 * @param sysType
	 *            系统类型
	 */
	ApplicationClientInfo getByApplicationVersionSysTypeSysRequirementFileType(ApplicationVersion appVer, String sysType,
			String sysRequirment, String fileType);

	/**
	 * 根据系统类型和平台信息查找应用管理器
	 * 
	 * @param sysRequirment
	 *            平台信息
	 * @param sysType
	 *            系统类型
	 */
	List<ApplicationClientInfo> getAppManagerByTypeAndVersion(String sysType, String sysRequirment);

	ApplicationClientInfo getAppManagerByTypeAndReqAndVersion(String sysType, String sysRequirment, String clientVersion);

	/**
	 * 查询应用管理器
	 * 
	 * @param page
	 *            分页参数
	 * @param Map参数
	 */
	Page<ApplicationClientInfo> getApplicationClientInfoForIndex(final Page<ApplicationClientInfo> page, Map<String, Object> values);

	/**
	 * 根据系统类型和文件类型查找应用管理器
	 * 
	 * @param sysType
	 *            系统类型
	 * @param sysRequirement
	 *            系统版本
	 */
	List<Map<String, Object>> getHistoryVersion(String sysType, String sysRequirment);

	/**
	 * 根据业务类型,系统版本获取应用管理器的最大开发版本
	 * 
	 * @param busiType
	 *            业务类型
	 * @param sysType
	 *            系统类型
	 * @param sysRequirement
	 *            系统版本
	 */
	Integer getMaxVersionCode(Integer busiType, String sysType, String sysRequirement);

	/**
	 * 根据业务类型,系统版本,应用版本，系统版本获取应用客户端的最大开发版本
	 * 
	 * @param busiType
	 *            业务类型
	 * @param sysType
	 *            系统类型
	 * @param sysRequirement
	 *            系统版本
	 * @param AppVerId
	 *            应用版本Id
	 */
	Integer getMaxVersionCodeByAppVer(Integer busiType, String sysType, String sysRequirement, Long appVerId);

	/**
	 *  根据aid cardNo获取应用客户端的最大开发版本, android和j2me有就都返回
	 * @param aid
	 * @param cardNo
	 * @return
	 */
	List<ApplicationClientInfo> getByAidAndCardNo(String aid, String cardNo);
	/**
	 *  获取手机钱包的最大版本号
	 *  @param card
	 */
	String getMocamMaxVersion();
}
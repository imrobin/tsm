package com.justinmobile.tsm.application.manager;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardInfo;

@Transactional
public interface ApplicationVersionManager extends EntityManager<ApplicationVersion> {

	/**
	 * @Title: getByAppId
	 * @Description:
	 * @param app
	 * @param page
	 * @param status
	 */
	Page<ApplicationVersion> getByAppId(Application app, Page<ApplicationVersion> page, Integer status);

	/**
	 * @param statusTested
	 * @Title: changeAppVerStatus
	 * @Description:
	 * @param string
	 */
	void changeAppVerStatus(String verIds, Integer statusTested);

	/**
	 * @Title: publish
	 * @Description:
	 * @param versionIds
	 *            ,mobiles,cardBaseInfoId 应用版本id， 手机号， 卡批次
	 * @param cardBaseInfoId
	 * @param mobiles
	 */
	void publish(String versionIds, String mobiles, String cardBaseInfoId);

	/**
	 * 完成应用版本信息的录入
	 * 
	 * @param applicationVersionId
	 *            应用版本ID
	 */
	void completeCreateApplicationVersion(long applicationVersionId);

	/**
	 * 判断应用版本能否被发起请求的用户修改<br />
	 * 结果由发起请求的用户修改对应用版本所属应用的修改权限决定
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param applicationVersion
	 *            被修改的应用版本
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	boolean isEditable(String username, ApplicationVersion applicationVersion);

	/**
	 * 应用归档
	 * 
	 * @param verIdsArray
	 * @param reason
	 */
	void archive(String[] verIdsArray, String reason);

	ApplicationVersion getAidAndVersionNo(String aid, String versionNo);

	/**
	 * 获取卡能支持的应用最新版本
	 * 
	 * @param card
	 *            卡
	 * @param application
	 *            应用
	 * @return
	 */
	ApplicationVersion getLastestAppVersionSupportCard(CardInfo card, Application app);

	/**
	 * 根据AID和版本号查找应用版本
	 * 
	 * @param aid
	 *            应用的AID
	 * @param versionNo
	 *            版本的版本号
	 * @return 查找结果，如果指定AID指定版本号的应用版本不存在，返回null
	 */
	ApplicationVersion getByAidAndVersionNo(String aid, String versionNo);

	public void finishAppVersion(String versionIds);

	Page<ApplicationVersion> findUnLinkPage(Page<ApplicationVersion> page, String cardBaseId);

	/**
	 * 创新应用新版本
	 * 
	 * @param appVer
	 *            应用版本
	 * @return
	 */
	public void createVersion(ApplicationVersion appVer);

	/**
	 * 删除应用版本
	 * 
	 * @param applicaitionVersion
	 *            待删除的应用版本
	 * @param username
	 *            发起请求的Sp的用户名
	 * 
	 * @throws APPLICATION_SP_DISCARD
	 *             如果用户无权操作该应用版本
	 * @throws APPLICATION_VERSION_NOT_INIT
	 *             该应用版本的状态不是“初始化”
	 */
	void remove(ApplicationVersion applicaitionVersion, String username);

	/**
	 * 应用版本是否被卡支持？
	 * 
	 * @param card
	 *            卡
	 * @param applicationVersion
	 *            应用版本
	 * @return true-支持<br/>
	 *         false-不支持
	 */
	boolean isSupportByCard(CardInfo card, ApplicationVersion applicationVersion);

	/**
	 * @Title:hasArchiveRequest
	 * @Description: 当前版本是否有归档申请
	 * @param request
	 * @return
	 */
	long hasArchiveRequest(Long appVerId);

	/**
	 * 完成测试
	 * 
	 * @param appverId
	 */
	void finishTest(Long appverId);

	/**
	 * 查询指定状态、SP的应用
	 * 
	 * @param page
	 * @param queryParams
	 * @return
	 * @throws PlatformException
	 */
	Page<ApplicationVersion> findPageBySp(Page<ApplicationVersion> page, Map<String, Object> queryParams) throws PlatformException;

	/**
	 * 查找支持卡片并且允许用户手机号使用的应用版本
	 * 
	 * @param card
	 *            卡 片
	 * @param app
	 *            要查找版本的应用
	 * @param mobileNo
	 *            用户手机号
	 * @return 查找结果<br/>
	 *         null-没有满足条件的应用版本
	 */
	ApplicationVersion getLastestAppVersionSupportCard(CardInfo card, Application app, String mobileNo);

	/** 
	 * 提交报告完成
	 * @param testReport
	 * @param appverId
	 * @param subType 
	 */
	void finishTest(ApplicationVersionTestReport testReport, Long appverId, String subType);

	/**列出下载能够下载文件的APPVER
	 * @param page
	 * @param appName 
	 * @return
	 */
	Page<ApplicationVersion> getDownTestFileAppver(Page<ApplicationVersion> page, String appName);

	void saveReport(ApplicationVersionTestReport testReport, Long appverId, Long cardBaseId);
}
package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.card.domain.CardInfo;

@Transactional
public interface ApplicationManager extends EntityManager<Application> {

	/**
	 * 创建一个新的应用
	 * 
	 * @param username
	 *            创建应用的SP的用户名
	 * @param application
	 *            应用基本信息
	 * @param params
	 */
	public void createNewApplication(String username, Application application, Map<String, String> params) throws PlatformException;

	/**
	 * @Title: changeAppStatus
	 * @Description: 应用周期管理,改变应用状态
	 * @param appId
	 * @param status
	 *            By:liqiang.wang
	 */
	void changeAppStatus(Long appId, int status) throws PlatformException;

	/**
	 * @Title: getPcImgByAppId
	 * @Description: 获取应用的网页图片字节
	 * @param appId
	 * @return
	 */
	public byte[] getPcImgByAppId(Long appId) throws PlatformException;

	/**
	 * @Title: archiveApp
	 * @Description:
	 * @param appId
	 */
	public void archiveApp(Long appId) throws PlatformException;

	@Transactional(readOnly = true)
	public Page<Application> advanceSearch(Page<Application> page, Map<String, String> paramMap) throws PlatformException;

	/**
	 * 判断应用能否被发起请求的用户修改<br />
	 * 只有应用的拥有者可以修改加载文件
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param application
	 *            被修改的应用
	 * @return true-可以修改<br/ >
	 *         false-不能修改
	 */
	boolean isEditable(String username, Application application) throws PlatformException;

	@Transactional(readOnly = true)
	Application getByAid(String aid) throws PlatformException;

	/**
	 * 更改应用定义的设置
	 * 
	 * @param appId
	 * @param deleteRule
	 * @param personalType
	 */
	public void defChange(String appId, String deleteRule, String personalType);

	/**
	 * 判断卡是否支持指定应用
	 * 
	 * @param card
	 *            卡
	 * @param applicationVersion
	 *            应用版本
	 * @return true-支持<br/>
	 *         false-不支持
	 */
	public boolean isSupport(CardInfo card, Application application);

	/**
	 * 修改应用信息
	 * 
	 * @param username
	 *            发起请求用户的用户名
	 * @param application
	 *            被修改的应用
	 * @param params
	 *            其他数据
	 */
	public void modifyApplicationBaseInfo(String username, Application application, Map<String, String> params);

	/**
	 * 应用推荐列表
	 * 
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<Application> recommendAppList(Page<Application> page);

	@Transactional(readOnly = true)
	Page<Application> getDownloadableApps(Page<Application> page, String cardNo, Map<String, ?> filters) throws PlatformException;

	/**
	 * 验证应用的AID的唯一性<br/>
	 * 应用的AID不能与以下对象AID重复
	 * <ul>
	 * <li>其他应用</li>
	 * <li>加载文件</li>
	 * <li>实例</li>
	 * <li>安全域</li>
	 * </ul>
	 * 
	 * @param aid
	 * @throws APPLICAION_AID_REDULICATE
	 *             如果应用AID重复
	 */
	void validateAid(String aid);

	/**
	 * 删除应用
	 * 
	 * @param application
	 *            待删除的应用
	 * @param username
	 *            发起删除请求的sp
	 * 
	 * @throws APPLICATION_AID_DISCARD
	 *             如果发起请求的用户无权操作应用
	 * @throws APPLICATION_NOT_INIT
	 *             如果应用状态不是“初始化”
	 */
	void remove(Application application, String username);

	public Page<Application> findByAppType(Page<Application> page, Long parentId);

	/**
	 * 获取手机号码与mobile_section中的对应状态
	 * @param cardNo
	 * @param appLocation 
	 */
	public String getLocationMobileStatus(String cardNo, String appLocation);
	
	/**
	 * 验证业务平台URL有效性
	 * @param businessPlatformUrl
	 * @param serviceName
	 */
	public void validateBuissinessUrl(String businessPlatformUrl, String serviceName);

	public List<Map<String, Object>> getShowTypeApp();
}
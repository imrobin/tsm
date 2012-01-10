package com.justinmobile.tsm.customer.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Transactional
public interface CustomerCardInfoManager extends EntityManager<CustomerCardInfo> {

	/**
	 * @Title: getCustomerCardByCustomerName
	 * @Description: 根据用户名获取其下的终端
	 * @param userName
	 * @param status
	 * @return
	 * @throws PlatformException
	 */
	public List<CustomerCardInfo> getCustomerCardByCustomerName(String userName, Integer status) throws PlatformException;

	/**
	 * @Title: getCustomerCardByCustomerName
	 * @Description: 根据用户名获取其下的指定状态卡片终端
	 * @param userName
	 * @return
	 * @throws PlatformException
	 */
	public List<CustomerCardInfo> getCustomerCardCanChange(String userName) throws PlatformException;

	/**
	 * @Title: getCustomerCardInfoById
	 * @Description: 根据ID获取CUSTOMERCARDINFO
	 * @param ccIdL
	 * @return
	 */
	public CustomerCardInfo getCustomerCardInfoById(Long ccIdL);

	/**
	 * @Title: changeCustomerCardStatus
	 * @Description: 根据ID和状态修改用户终端状态
	 * @param ccId
	 * @param statusLost
	 */
	public void changeCustomerCardStatus(Long ccId, int statusLost);

	/**
	 * @Title: changeCustomerCardStatus
	 * @Description: 根据ID和状态修改用户终端激活状态
	 * @param ccId
	 * @param statusLost
	 */
	public void changeCustomerCardActive(Long ccId, Long active);

	/**
	 * @Title: doCustomerCardInfoLost
	 * @Description: 挂失指定CUSTOMERINFO
	 * @param ccIdL
	 */
	public void doCustomerCardInfoLost(Long ccIdL);

	/**
	 * @Title: doCustomerCardInfoRecover
	 * @Description: 挂失后恢复终端
	 * @param ccIdL
	 */
	public void doCustomerCardInfoRecover(Long ccIdL);

	/**
	 * @Title: doCustomerCardInfoCancel
	 * @Description: 创建注销终端的需要删除的列表
	 * @param ccIdL
	 */
	public List<Map<String, Object>> createDelAppListForCancelTerm(Long ccIdL);

	/**
	 * @Title: getApplistByCustomerCard
	 * @Description: 查找指定终端下的应用
	 * @param customerCardId
	 * @return
	 */
	public List<Application> getApplistByCustomerCard(Long customerCardId);

	/**
	 * @Title: getApplistByCustomerCard
	 * @Description: 查找指定终端下的应用多了版本信息
	 * @param customerCardId
	 * @return
	 */
	public List<Map<String, Object>> getAppMaplistByCustomerCard(Long customerCardId);

	/**
	 * @Title: getAppCountByCustomerCardInfo
	 * @Description: 统计指定终端的应用个数
	 * @param cci
	 * @return
	 */
	public int getAppCountByCustomerCardInfo(CustomerCardInfo cci);

	/**
	 * @Title: calCardSize
	 * @Description: 计算当前卡片上的应用空间与占用比例
	 * @param ccId
	 * @return
	 */
	public Map<String, Object> calCardSize(Long ccId);

	/**
	 * @Title: getCardApplicationsByMobileNo
	 * @Description: 根据手机号获取此终端下的应用
	 * @param moibleNpo
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getCardApplicationsByMobileNo(String moibleNpo, Page<CardApplication> page);

	/**
	 * 绑定终端
	 * 
	 * @param paramMap
	 * @param isChange
	 *            YES=替换 NO=正常绑定
	 * @return
	 */
	public Long bindCardWithMobileType(Map<String, String> paramMap, boolean isChange);

	/**
	 * @Title: sendActive
	 * @Description: 发送激活码
	 * @param customerCardId
	 * @param type
	 *            1:激活描述 2:验证描述
	 */
	public void sendActive(Long customerCardId, String type);

	/**
	 * @Title: activeCard
	 * @Description:激活绑定
	 * @param userName
	 * @param avtiveCode
	 * @param ccId
	 */
	public boolean activeCard(String userName, String avtiveCode, String ccId);

	/**
	 * @Title: changeActive
	 * @Description: 激活更改终端
	 * @param userName
	 * @param avtiveCode
	 * @param ccId
	 * @param oldId
	 * @return
	 */
	public Map<String, Object> changeActive(String userName, String avtiveCode, String ccId, String oldId);

	/**
	 * @Title: listRevertApps
	 * @Description: 显示可恢复应用列表
	 * @param ccId
	 * @return
	 */
	public List<Application> listRevertApps(Long ccId);

	/**
	 * @Title: revertApp
	 * @Description: 获取后台恢复应用
	 * @param oldCci
	 * @param cci
	 */
	public List<CardApplication> getRevertApp(CustomerCardInfo oldCci, CustomerCardInfo cci);

	/**
	 * @Title: getCanRevertByCustomerName
	 * @Description: 获取能够恢复应用的终端
	 * @param userName
	 * @return
	 */
	public List<CustomerCardInfo> getCanRevertByCustomerName(String userName);

	/**
	 * 检查应用恢复状况并修改CUSTOMERCARD
	 * 
	 * @param caId
	 * @param caId2
	 */
	public CustomerCardInfo checkAndFinishRevert(String userNamse, String caId);

	/**
	 * 根据卡片编号获取CUSTOMERCardInfo
	 * 
	 * @param cardNo
	 * @return
	 */
	public CustomerCardInfo getByCardNo(String cardNo);

	/**
	 * 根据手机号获取CUSTOMERCardInfo列表
	 * 
	 * @param cardNo
	 * @return
	 */
	public List<CustomerCardInfo> getByMobileNo(String mobileNo);

	/**
	 * 根据手机号来获取正在替换的终端
	 * 
	 * @param stringNo
	 * @return
	 */
	public CustomerCardInfo getByMobileNoRepalcing(String stringNo);

	/**
	 * 根据卡号获取卡片信息
	 * 
	 * @param cardNo
	 * @return
	 */
	public Map<String, Object> getCardMessageByCardNo(String cardNo);

	/**
	 * 结束更换终端
	 * 
	 * @param ccid
	 * @param oldId
	 */
	public void checkChangeFinish(String ccid, String oldId);

	/**
	 * 结束注销
	 * 
	 * @param ccId
	 */
	public void finashCancel(Long ccId);

	/**
	 * 解除锁定
	 * 
	 * @param ccid
	 */
	public void cancelLost(Long ccid);

	/**
	 * 根据卡查找状态不为“已注销”或者“替换完成”的唯一记录
	 * 
	 * @param card
	 *            卡
	 * @return 查找结果<br/>
	 *         null-如果满足条件的结果不存在
	 */
	CustomerCardInfo getByCardThatStatusNotCanclledOrNotReplaced(CardInfo card);

	/**
	 * 判断是否有对应的客户端
	 * 
	 * @param ccid
	 */
	public boolean hasSysRequirment(CustomerCardInfo cci, CardApplication ca);

	/**
	 * 提示恢复应用的结果
	 * 
	 * @param sessionId
	 * @return
	 */
	public Map<String, Object> tipRevert(String sessionId);

	/**
	 * @param cardNo
	 * @return
	 */
	public CustomerCardInfo getByCardNoCancelAndReplaced(String cardNo);

	/**
	 * 发送更换手机号码的验证码
	 * 
	 * @param newMobileNo
	 * @return
	 */
	public String checkSend(String newMobileNo);

	/**
	 * 更换手机号码
	 * 
	 * @param ccId
	 * @param newmobileNo
	 */
	public void changeMobileNo(String ccId, String newmobileNo);

	/**
	 * 根据安全域信息查询对应的订购用户
	 * 
	 * @param page
	 * @param securityDomain
	 * @return
	 * @throws PlatformException
	 */
	public Page<CustomerCardInfo> getCustomerCardInfoPageBySd(Page<CustomerCardInfo> page, SecurityDomain securityDomain)
			throws PlatformException;

	/**
	 * 根据应用信息查询对应的订购用户
	 * 
	 * @param page
	 * @param appVersion
	 * @return
	 * @throws PlatformException
	 */
	public Page<CustomerCardInfo> getCustomerCardInfoPageByApp(Page<CustomerCardInfo> page, ApplicationVersion appVersion)
			throws PlatformException;

	public List<Map<String, Object>> getCardAppinfoListByCci(Long ccId);

	public List<Map<String, Object>> getCardSDListByCci(Long ccId);

	public List<CustomerCardInfo> getByMobileNoNotCancelAndEnd(String mobileNo);

	Page<CustomerCardInfo> getCustomerCardInfoPageBySd1(Page<CustomerCardInfo> page, SecurityDomain securityDomain)
			throws PlatformException;

	/**
	 * 查找已经被指定用户迁出了指定应用的绑定关系
	 * 
	 * @param application
	 *            指定的应用
	 * @param customer
	 *            指定的用户
	 * @return
	 */
	List<CustomerCardInfo> getByCustomerThatEmigratedApplication(Application application, Customer customer);

	/**
	 * 根据卡信息查找已经被卡所属用户迁出了指定应用的绑定关系
	 * 
	 * @param application
	 *            指定的应用
	 * @param card
	 *            卡信息
	 * @return
	 */
	List<CustomerCardInfo> getByCardThatEmigratedApplication(Application application, CardInfo card);

	/**
	 * @param paramMap
	 *            ("userName 用户名" "cardNo 卡号" "mobileNo 手机号"
	 *            "mobileTypeId 手机类型ID" "mobileName 用户终端自定义名称" )
	 * @param isChange
	 *            是否是替换或者修改终端而不是新建
	 * @return 完成绑定后的用户终端
	 */
	CustomerCardInfo bindCard(Map<String, String> paramMap, boolean isChange);

	/**
	 * 检查注销终端时是否还有未删除或者未迁出的应用
	 * 
	 * @param ccId
	 * @return
	 */
	public boolean checkCancelTermCardApp(Long ccId);

	/**
	 * 检查手机号与应用是否在同一号段内
	 * 
	 * @param cardNo
	 * @param appId
	 *            应用ID
	 * @param forceMap
	 *            放提示信息
	 * @return
	 */
	public boolean checkMobileNoLocation(String cardNo, Long appId, Map<String, Object> forceMap);

	/**
	 * 根据手机号查找全部带分页
	 * 
	 * @param mobileNo
	 * @return
	 */
	public Page<CustomerCardInfo> getByMobileNoAllAndPage(Page<CustomerCardInfo> page, String mobileNo);

	/**
	 * 管理员操作应用特殊查询
	 * 
	 * @param moibleNo
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getCardApplicationsByMobileNoForAdmin(String moibleNo, Page<CardApplication> page);

	/**
	 * 获取迁出但未执行的应用列表
	 * 
	 * @param ccId
	 * @return
	 */
	public boolean getMigratableAppList(Long ccId);

	/**
	 * 管理员后台激活终端,不需要激活码
	 * 
	 * @param customerCardId
	 */
	public void adminActiveCard(Long customerCardId);

	/**
	 * 根据卡信息获取状态为“正常”或“已挂失”的唯一记录
	 * 
	 * @param card
	 *            卡信息
	 * @return 满足条件的记录<br/>
	 *         null-如果满足条件的记录不存在
	 */
	CustomerCardInfo getByCardThatStatusNormalOrLost(CardInfo card);

	/**
	 * @param ccId
	 * @return
	 */
	public List<Map<String, Object>> getSDMaplistByCustomerCard(Long ccId);

	public CustomerCardInfo getCCIByCustomerAndCard(Customer customer, CardInfo card);

	public List<CustomerCardInfo> getCustomerCardLikeCustomerAndCCName(Customer customer, String phoneName);

	/**
	 * 检查卡能否绑定
	 * 
	 * @param cardInfo
	 *            需要绑定的卡
	 * 
	 * @throws CARD_IS_EXIST
	 *             如果卡已经绑定
	 */
	void checkCardBindable(CardInfo cardInfo);

	/**
	 * 获取所有卡上的应用
	 * 
	 * @param userName
	 * @return
	 */
	public List<Map<String, Object>> getAllAppListByUserName(String userName);

	public List<Map<String, Object>> getAllCardAppListByUserName(String userName);

	/**
	 * 根据application和Customer获取CARDAPPLICATION
	 * 
	 * @param userName
	 * @param appId
	 * @return
	 */
	public List<Map<String, Object>> getCardApplicationByUserAndAppId(String userName, Long appId);

	/**
	 * 绑定卡和用户<br/>
	 * 用户是参数中的手机号为登录帐号的用户，如果该用户不存在将创建，默认密码为000000<br/>
	 * 绑定后卡为“已激活”状态
	 * 
	 * @param mobileNo
	 *            手机号
	 * @param cardNo
	 *            卡号
	 */
	void bindCardAsActivedAndCreatCustomerIfNeed(String mobileNo, String cardNo);

	/**
	 * 根据卡号获取状态为“已挂失”的绑定关系
	 * 
	 * @param cardNo
	 *            卡号
	 * @return 绑定关系<br\>
	 *         null-如果指定卡号的卡未绑定或绑定了但未挂失
	 */
	CustomerCardInfo getByCardNoThatStatusLost(String cardNo);
}
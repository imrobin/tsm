package com.justinmobile.tsm.customer.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

public interface CustomerCardInfoDao extends EntityDao<CustomerCardInfo, Long> {

	/**
	 * 获取该SE的绑定关系
	 * 
	 * @param cardInfo
	 * @return
	 */
	List<CustomerCardInfo> getListByCardInfo(CardInfo cardInfo);

	/**
	 * 获取该手机号的绑定关系
	 * 
	 * @param string
	 * @return
	 */
	List<CustomerCardInfo> getListByMobileNo(String mobileNo);

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
	 * 根据终端名称来查找
	 * 
	 * @param customer
	 * @param cardName
	 * @return
	 */
	CustomerCardInfo findByCciName(Customer customer, String cardName);

	List<CustomerCardInfo> getCustomerCardByCustomer(Customer customer, Integer status);

	/**
	 * @param id
	 * @param id2
	 * @param id3
	 * @return
	 */
	List<CustomerCardInfo> getHasRequiremnt(Long id, Long id2, Long id3);

	/**
	 * 根据用户来查找正常和挂失状态的终端
	 * 
	 * @param customer
	 * @return
	 */
	List<CustomerCardInfo> getCustomerCardInfoByIdAsNormomAndLost(Customer customer);

	/**
	 * 查找可疑恢复的终端
	 * 
	 * @param customer
	 * @return
	 */
	List<CustomerCardInfo> findCanRevert(Customer customer);

	/**
	 * 根据状态激活级黑名单来查找终端
	 * 
	 * @param cardInfo
	 * @param status
	 * @param actived
	 * @param inblack
	 * @return
	 */
	CustomerCardInfo findCustomerCardInfo(CardInfo cardInfo, int status, Long actived, int inblack);

	/**
	 * 查找激活且不为已替换和已注销的终端根据手机号
	 * 
	 * @param mobileNo
	 * @return
	 */
	List<CustomerCardInfo> getByMobileNoNotCancelAndEnd(String mobileNo);

	/**
	 * 根据手机号查找终端
	 * 
	 * @param stringNo
	 * @return
	 */
	CustomerCardInfo getByMobileNoRepalcing(String stringNo);

	/**
	 * 查找激活且不为已替换和已注销的终端
	 * 
	 * @param cardInfo
	 * @return
	 */
	CustomerCardInfo findByActiveAndNotCancelEnd(CardInfo cardInfo);

	/**
	 * 根据用户名和手机号来查找是否其他用户也有此手机号终端
	 * 
	 * @param customer
	 * @param newmobileNo
	 * @return
	 */
	List<CustomerCardInfo> findCCIByNotCustomerAndMobiemo(Customer customer, String newmobileNo);

	/**
	 * 根据安全域信息查询对应的订购用户
	 * 
	 * @param page
	 * @param securityDomain
	 * @return
	 */
	Page<CustomerCardInfo> getCustomerCardInfoPageBySd(Page<CustomerCardInfo> page, SecurityDomain securityDomain);

	/**
	 * 根据应用信息查询对应的订购用户
	 * 
	 * @param page
	 * @param application
	 * @return
	 */
	Page<CustomerCardInfo> getCustomerCardInfoPageByApp(Page<CustomerCardInfo> page, ApplicationVersion appVersion);

	List<CustomerCardInfo> getByMobileNo(String mobileNo);

	/**
	 * 查询已经被指定用户迁出指定应用的记录，查询结果根据绑定事件降序排序
	 * 
	 * @param application
	 *            指定应用
	 * @param customer
	 *            指定用户
	 * @return
	 */
	List<CustomerCardInfo> getByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc(Application application,
			Customer customer);

	/**
	 * 获取手机号为条件的分页记录
	 * 
	 * @param page
	 * @param mobileNo
	 * @return
	 */
	Page<CustomerCardInfo> getByMobileNoAllAndPage(Page<CustomerCardInfo> page, String mobileNo);

	/**
	 * 根据卡查询状态为“正常”或“已挂失”
	 * 
	 * @param card
	 *            卡
	 * @return 返回唯一结果<br/>
	 *         null-满足条件的记录不存在
	 */
	CustomerCardInfo getByCardThatStatusNormalOrLost(CardInfo card);

	/**
	 * 根据卡查询状态为“正常”或“已挂失”或“未激活”
	 * 
	 * @param card
	 *            卡
	 * @return 返回唯一结果<br/>
	 *         null-满足条件的记录不存在
	 * @return
	 */
	CustomerCardInfo getByCardThatStatusNormalOrLostOrNotUse(CardInfo card);
	/**
	 * 根据卡查询状态不为"注销"或"已替换"
	 * @param card
	 * @return 返回唯一结果<br/>
	 *         null-满足条件的记录不存在
	 * @return
	 */
	CustomerCardInfo getByCard(CardInfo card);

	CustomerCardInfo getCCIByCustomerAndCard(Customer customer, CardInfo card);

	List<CustomerCardInfo> getCustomerCardLikeCustomerAndCCName(Customer customer, String phoneName);

	/**
	 * 根据用户获取正常和挂失的终端
	 * @param customer
	 * @return
	 */
	List<CustomerCardInfo> getCustomerCardByCustomerThatNormAndLost(Customer customer);
}
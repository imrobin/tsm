package com.justinmobile.tsm.card.manager;

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

@Transactional
public interface CardApplicationManager extends EntityManager<CardApplication> {

	/**
	 * 根据卡号和应用aid得到CardApplication
	 * 
	 * @param cardNo
	 * @param aid
	 * @return
	 * @throws PlatformException
	 */
	CardApplication getByCardNoAid(String cardNo, String aid) throws PlatformException;

	/**
	 * 根据安全域找到所有卡上已安装的应用
	 * 
	 * @param sdId
	 * @return
	 */
	List<CardApplication> getCardAppBySd(long sdId) throws PlatformException;

	/**
	 * 查找卡上指定安全域的应用列表
	 * 
	 * @param card
	 *            卡
	 * @param sd
	 *            指定安全域
	 * @return 应用列表
	 */
	List<CardApplication> getByCardAndApplicationSd(CardInfo card, SecurityDomain sd);

	CardApplication getByCardAndAppver(CardInfo card, ApplicationVersion applicationVersion);

	List<CardApplication> getCardAppByCard(CardInfo card);

	/**
	 * 将应用迁出卡片
	 * 
	 * @param application
	 *            被迁出的应用
	 * @param card
	 *            迁出应用的卡片
	 */
	void emigrate(Application application, CardInfo card);

	/**
	 * 根据用户查找用户卡上的应用
	 * 
	 */
	Page<CardApplication> findPageByCustomer(Page<CardApplication> page, Map<String, Object> queryParams);

	List<Map<String, Object>> findByCustomer(Page<Map<String, Object>> page, String mobileNo);

	/**
	 * 获取次卡片下未删除未迁出的应用
	 * 
	 * @param card
	 * @return
	 */
	List<CardApplication> getCaListNotDelAndNotMigratable(CardInfo card);

	/**
	 * 获取关联的已迁出的应用
	 * 
	 * @param card
	 * @return
	 */
	List<CardApplication> getCaListMigratable(CardInfo card);

	/**
	 * 根据卡号和应用aid得到状态为可用的CardApplication
	 * 
	 * @param cardNo
	 * @param aid
	 * @return
	 * @throws PlatformException
	 */
	CardApplication getAvailbleOrLockedByCardNoAid(String cardNo, String aid);

	/**
	 * 指定应用是否在指定的卡上预置？
	 * 
	 * @param card
	 *            指定的卡
	 * @param application
	 *            指定应用
	 * @return true-预置<br/>
	 *         false-未预置
	 */
	boolean isPreset(CardInfo card, Application application);

	/**
	 * 根据卡获取指定状态的记录
	 * 
	 * @param card
	 *            卡
	 * @param status
	 *            指定状态
	 * @return 查询结果
	 */
	List<CardApplication> getByCardAndStatus(CardInfo card, int status);

	/**
	 * 挂失的时候需要对状态7.8的CARDAPP处理,所以查询
	 * @param card
	 * @return
	 */
	List<CardApplication> getForLostListByCardInfo(CardInfo card);

	/**
	 * 查找此卡片的同一应用下的关联记录
	 * @param card
	 * @param application
	 * @return
	 */
	List<CardApplication> getByCardAndApplication(CardInfo card, Application application);
}
package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;

public interface CardClientDao extends EntityDao<CardClient, Long> {

	/**
	 * 根据卡和应用查找终端上客户端的记录
	 * 
	 * @param card
	 *            卡
	 * @param application
	 *            应用
	 * @return 查找结果
	 */
	List<CardClient> getByCardAndApplication(CardInfo card, Application application);

	/**
	 * 根据卡，应用，系统类型查找终端上客户端的记录
	 * 
	 * @param card
	 *            卡
	 * @application 应用
	 * @sysType 区分操作系统还是J2ME
	 * @return 查找结果
	 */
	CardClient getByCardAndApplicationAndSysType(CardInfo card, Application application, String sysType);

}

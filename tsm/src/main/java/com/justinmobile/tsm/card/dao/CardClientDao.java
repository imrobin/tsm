package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;

public interface CardClientDao extends EntityDao<CardClient, Long> {
	List<ApplicationClientInfo> getClientByCard(CardInfo card);

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
}

package com.justinmobile.tsm.card.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersionTestReport;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Transactional
public interface CardBaseInfoManager extends EntityManager<CardBaseInfo>{

	void addCardBaseInfo(CardBaseInfo cbi);

	void modifyCardBaseInfo(String oldId,CardBaseInfo cbi);

	void checkRemove(Long cbiId);

	CardBaseInfo getCardBaseInfoByCardNo(String cardNo);

	/**
	 * 检查是否已经发卡,若存在相关CARDINFO则认为已发卡
	 * @param cardBaseInfo
	 * @return
	 */
	boolean checkPublishCard(CardBaseInfo cardBaseInfo);

	/**
	 * 查找已经在测试应用中测试通过的批次
	 * @param page
	 * @param appVerId
	 * @return
	 */
	List<Map<String, Object>> findTestedCardBase(Page<ApplicationVersionTestReport> page, String appVerId);
}
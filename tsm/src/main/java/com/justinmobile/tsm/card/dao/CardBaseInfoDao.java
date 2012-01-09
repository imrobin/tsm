package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

public interface CardBaseInfoDao extends EntityDao<CardBaseInfo, Long> {

	CardBaseInfo getCardBaseInfoByCardNo(String cardNo);

	List<CardBaseInfo> findInScope(CardBaseInfo cbi);
}
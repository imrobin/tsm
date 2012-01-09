package com.justinmobile.tsm.card.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardInfo;

@Repository("cardInfoDao")
public class CardInfoDaoHibernate extends EntityDaoHibernate<CardInfo, Long> implements CardInfoDao {

	@Override
	public CardInfo getByCardNo(String cardNo) {
		String hql = "from " + CardInfo.class.getName() + " as cbi where cbi.cardNo=:cardNo";

		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("cardNo", cardNo);

		return findUnique(hql, values);
	}
}
package com.justinmobile.tsm.card.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.card.dao.CardBaseInfoDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Repository("cardBaseInfoDao")
public class CardBaseInfoDaoHibernate extends EntityDaoHibernate<CardBaseInfo, Long> implements CardBaseInfoDao {

	@Override
	public CardBaseInfo getCardBaseInfoByCardNo(String cardNo) {
		if(cardNo.length() == 18){
			cardNo = "00" + cardNo;
		}
		String hql = "from " + CardBaseInfo.class.getName() + " as cbi where cbi.startNo <= ? and cbi.endNo >= ?";
		return this.findUniqueEntity(hql, cardNo ,cardNo);
	}

	@Override
	public List<CardBaseInfo> findInScope(CardBaseInfo cbi) {
		StringBuilder sb = new StringBuilder();
		sb.append("from ");
		sb.append(CardBaseInfo.class.getName());
		sb.append(" as cbi where ");
		sb.append("(cbi.startNo >= :startNo  and cbi.startNo <= :endNo) or ");
		sb.append("(cbi.startNo <= :startNo and cbi.endNo >= :endNo) or ");
		sb.append("(cbi.endNo >= :startNo and cbi.endNo <= :endNo) or ");
		sb.append("(cbi.startNo >= :startNo and cbi.endNo <= :endNo)");
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("startNo",  cbi.getStartNo());
		paramMap.put("endNo", cbi.getEndNo());
		return this.find(sb.toString(), paramMap);
		
	}
}
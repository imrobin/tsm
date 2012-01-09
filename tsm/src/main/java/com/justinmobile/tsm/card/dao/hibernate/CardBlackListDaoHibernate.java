package com.justinmobile.tsm.card.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.card.dao.CardBlackListDao;
import com.justinmobile.tsm.card.domain.CardBlackList;

@Repository("cardBlackListDao")
public class CardBlackListDaoHibernate extends EntityDaoHibernate<CardBlackList, Long> implements CardBlackListDao {
}
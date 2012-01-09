package com.justinmobile.tsm.card.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.card.dao.CardSdScp02KeyDao;
import com.justinmobile.tsm.card.domain.CardSdScp02Key;

@Repository("cardSdScp02KeyDao")
public class CardSdScp02KeyDaoHibernate extends EntityDaoHibernate<CardSdScp02Key, Long> implements CardSdScp02KeyDao {
}
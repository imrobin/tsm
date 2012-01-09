package com.justinmobile.tsm.transaction.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.transaction.dao.ProviderTransactionDao;
import com.justinmobile.tsm.transaction.domain.ProviderTransaction;

@Repository("providerTransactionDao")
public class ProviderTransactionDaoHibernate extends EntityDaoHibernate<ProviderTransaction, Long> implements ProviderTransactionDao {

}

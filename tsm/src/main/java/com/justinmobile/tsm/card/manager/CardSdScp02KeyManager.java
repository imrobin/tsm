package com.justinmobile.tsm.card.manager;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.card.domain.CardSdScp02Key;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

public interface CardSdScp02KeyManager extends EntityManager<CardSdScp02Key>{

	CardSdScp02Key getByCardSdKeyProfile(CardSecurityDomain cardSecurityDomain, KeyProfile keyProfile);
	
	KeyProfile getKeyProfile(CardSecurityDomain cardSecurityDomain, int keyType);
}
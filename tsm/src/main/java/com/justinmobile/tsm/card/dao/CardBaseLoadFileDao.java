package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;

public interface CardBaseLoadFileDao extends EntityDao<CardBaseLoadFile, Long> {

	/**
	 * 根据卡片和版本查找唯一值
	 * @param cbi
	 * @param lfv
	 * @return
	 */
	CardBaseLoadFile getByCardBaseAndVersion(CardBaseInfo cbi, LoadFileVersion lfv);

	CardBaseLoadFile getByLoadfileAndCardbase(LoadFile loadFile, CardBaseInfo cardBase);

	List<CardBaseLoadFile> getBySdAndCardBase(SecurityDomain securityDomain, CardBaseInfo cbi);
}
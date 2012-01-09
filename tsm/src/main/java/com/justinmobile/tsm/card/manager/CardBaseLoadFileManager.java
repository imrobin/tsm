package com.justinmobile.tsm.card.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;

@Transactional
public interface CardBaseLoadFileManager extends EntityManager<CardBaseLoadFile>{

	List<CardBaseLoadFile> getBaseLoadFileByCardBase(CardBaseInfo cbi);

	/**
	 * 关联批次和卡片
	 * @param cardid
	 * @param loadfileIds
	 */
	void doLink(String cardid, String loadfileIds);

	/**
	 * 根据批次和LOADFfile查询是否已存在
	 * @param cardBase
	 * @param loadFileVersion
	 * @return
	 */
	CardBaseLoadFile getByCardBaseAndLoadFile(CardBaseInfo cardBase, LoadFileVersion loadFileVersion);

	void delLink(String cbldId);

	/**
	 * 获取当前批次同一文件的文件版本
	 * @param loadFile
	 * @param cardBase
	 * @return
	 */
	CardBaseLoadFile getByLoadfileAndCardbase(LoadFile loadFile, CardBaseInfo cardBase);

	List<CardBaseLoadFile> getBySdAndCardBase(SecurityDomain securityDomain, CardBaseInfo cbi); 
	
}
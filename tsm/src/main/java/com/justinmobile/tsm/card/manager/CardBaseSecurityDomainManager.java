package com.justinmobile.tsm.card.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;

@Transactional
public interface CardBaseSecurityDomainManager extends EntityManager<CardBaseSecurityDomain>{


	/**
	 * 关联批次和卡片
	 * @param cardid
	 * @param presetMode 
	 * @param presetKeyVersion 
	 * @param preset 
	 * @param loadfileIds
	 */
	void doLink(String cardid, String sdids, String preset, String presetKeyVersion, String presetMode) throws PlatformException;

	/**
	 * 删除关系
	 * @param cbsddId
	 */
	void delLink(String cbsddId) throws PlatformException;

	/**
	 * 改变预置状态
	 */
	void changePrest(String cbsddId, String sdId, String preset, String presetKeyVersion, String presetMode) throws PlatformException;

	List<CardBaseSecurityDomain> findBySecurityDomain(SecurityDomain sd) throws PlatformException;
	
	CardBaseSecurityDomain getBySdAndCardBaseId(SecurityDomain sd, CardBaseInfo cbi) throws PlatformException;

	Boolean checkSDisISD(String cbsdId);
	
	List<CardBaseSecurityDomain> getByCardBase(CardBaseInfo cbi) throws PlatformException;
}
package com.justinmobile.tsm.card.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;

@Transactional
public interface CardLoadFileManager extends EntityManager<CardLoadFile> {

	/**
	 * 根据加载文件的AID和卡号查找卡上加载文件
	 * 
	 * @param aid
	 *            加载文件的AID
	 * @param cardNo
	 *            卡号
	 * @return 卡上加载文件，如果记录不存在，返回null
	 */
	@Transactional(readOnly = true)
	CardLoadFile getByAidAndCardNo(String appAid, String cardNo) throws PlatformException;

	/**
	 * 根据加载文件的版本和卡查找卡上加载文件
	 * 
	 * @param aid
	 *            加载文件版本
	 * @param cardNo
	 *            卡号
	 * @return 卡上加载文件，如果记录不存在，返回null
	 */
	@Transactional(readOnly = true)
	CardLoadFile getByCardAndLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion) throws PlatformException;

	/**
	 * 根据安全域找到所有卡片上安装在该安全域下的Loadfile
	 * 
	 * @param id
	 * @return
	 * @throws PlatformException
	 */
	@Transactional(readOnly = true)
	List<CardLoadFile> getCardLoadFileBySd(long sdId, String cardNo) throws PlatformException;

	/**
	 * 查找卡上指定安全域的加载文件列表
	 * 
	 * @param card
	 *            卡
	 * @param sd
	 *            指定安全域
	 * @return 加载文件列表
	 */
	List<CardLoadFile> getByCardAndLoadFileSd(CardInfo card, SecurityDomain sd);

	/**
	 * 查找卡上所有加载文件
	 * 
	 * @param card
	 *            卡
	 * @return 加载文件列表
	 */
	List<CardLoadFile> getByCard(CardInfo card);

}
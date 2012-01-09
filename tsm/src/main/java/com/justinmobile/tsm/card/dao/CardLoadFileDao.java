package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;

public interface CardLoadFileDao extends EntityDao<CardLoadFile, Long> {

	/**
	 * 根据加载文件的版本和卡查找卡上加载文件
	 * 
	 * @param aid
	 *            加载文件版本
	 * @param cardNo
	 *            卡号
	 * @return 卡上加载文件，如果记录不存在，返回null
	 */
	CardLoadFile getByCardAndLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion);

	/**
	 * 根据加载文件的版本和卡查找卡上加载文件
	 * 
	 * @param aid
	 *            加载文件版本
	 * @param cardNo
	 *            卡号
	 * @return 卡上加载文件，如果记录不存在，返回null
	 */
	CardLoadFile getByAidAndCardNo(String aid, String cardNo);

	List<CardLoadFile> getCardLoadFileBySd(long sdId, String cardNo);

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
}
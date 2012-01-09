package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;

public interface CardAppletDao extends EntityDao<CardApplet, Long> {

	CardApplet getBycardNoAndAppletAid(String cardNo, String appletAid);

	List<CardApplet> getByCardNoAndAppAid(String cardNo, String appAid);

	/**
	 * 获取在指定的卡上指定应用版本且从同一加载文件生成的所有实例
	 * 
	 * @param card
	 *            指定的卡片
	 * @param applicationVersion
	 *            指定的应用版本
	 * @param loadFileVersion
	 *            指定的加载文件版本
	 * @return 满足条件的所有卡上实例
	 */
	List<CardApplet> getByCardNoAndApplicationVersionThatCreateLoadFileVersion(CardInfo card, ApplicationVersion applicationVersion,
			LoadFileVersion loadFileVersion);

	List<CardApplet> getByCardAndAppSd(String cardNo, String sdAid);

	CardApplet getByCardAndApplet(CardInfo card, Applet applet);

	/**
	 * 获取在指定的卡上指定从指定加载文件版本生成的所有实例
	 * 
	 * @param card
	 *            指定的卡
	 * @param loadFileVersion
	 *            指定加载文件版本
	 * @return 满足条件的所有卡上实例
	 */
	List<CardApplet> getByCardNoThatCreateLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion);
}
package com.justinmobile.tsm.card.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;

@Transactional
public interface CardAppletManager extends EntityManager<CardApplet> {

	/**
	 * 实例是否已经在卡上安装？
	 * 
	 * @param cardNo
	 *            卡号
	 * @param aid
	 *            实例AID
	 * @return true-已安装<br/>
	 *         false-未安装
	 */
	@Transactional(readOnly = true)
	boolean isIntallOnCard(String cardNo, String appletAid) throws PlatformException;

	/**
	 * 得到应用在卡上安装的所有实例
	 * 
	 * @param cardNo
	 * @param appAid
	 * @return
	 */
	@Transactional(readOnly = true)
	List<CardApplet> getByCardNoAndAppAid(String cardNo, String appAid) throws PlatformException;

	@Transactional(readOnly = true)
	CardApplet getByCardNoAndAppletAid(String cardNo, String appletAid) throws PlatformException;

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
	List<CardApplet> getByCardNoAndApplicationVersionThatCreateLoadFileVersion(String cardNo, ApplicationVersion appVersion,
			LoadFileVersion loadFileVersion) throws PlatformException;

	List<CardApplet> getByCardAndAppSd(String cardNo, String sdAid) throws PlatformException;

	/**
	 * 获取卡上所有的实例
	 * 
	 * @param card
	 *            卡
	 * @return 实例列表
	 */
	List<CardApplet> getByCard(CardInfo card);

	CardApplet getByCardAndApplet(CardInfo card, Applet applet);

	/**
	 * 获取在指定的卡号的卡上从指定加载文件版本生成的所有实例
	 * 
	 * @param card
	 *            指定的卡
	 * @param loadFileVersion
	 *            指定加载文件版本
	 * @return 满足条件的所有卡上实例
	 */
	List<CardApplet> getByCardNoThatCreateLoadFileVersion(String cardNo, LoadFileVersion loadFileVersion);
}
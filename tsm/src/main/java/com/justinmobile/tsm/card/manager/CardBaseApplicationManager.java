package com.justinmobile.tsm.card.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Transactional
public interface CardBaseApplicationManager extends EntityManager<CardBaseApplication> {

	/**
	 * @Title: doLink
	 * @Description:
	 * @param appVer
	 * @param appids
	 */
	public void doLink(String[] appVer, String[] cardids);

	/**
	 * 根据卡批次和应用找到指定卡批次所支持的指定应用的状态为“已发布”的列表，安装应用版本号降序排序
	 * 
	 * @param cardBaseInfo
	 *            卡批次
	 * @param app
	 *            应用
	 * @return 查找结果
	 */
	public List<CardBaseApplication> getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(CardBaseInfo cardBaseInfo,
			Application app);

	public List<CardBaseApplication> findByApplicationVersion(ApplicationVersion av);

	/**
	 * 已批次为主导来关联应用版本
	 * 
	 * @param appVerIdS
	 * @param cardIds
	 */
	public void cardBaseDoLink(String[] appVerIdS, String cardId);

	public void delLink(String cbaId);

	public void changePrest(String cbaId);

	/**
	 * 根据卡批次和应用版本查找
	 * 
	 * @param cardBaseInfo
	 * @param applicationVersion
	 * @return
	 */
	public CardBaseApplication getByCardBaseAndApplicationVersion(CardBaseInfo cardBaseInfo, ApplicationVersion applicationVersion);

	public List<CardBaseApplication> getBySDandCBIAndPreset(SecurityDomain securityDomain, CardBaseInfo cbi);

	public CardBaseApplication saveCardBaseApplication(CardBaseInfo cbi, int presetMode, ApplicationVersion appver);

	/**
	 * 查找指定应用在指定卡批次的预置信息
	 * 
	 * @param cardBase
	 *            指定卡批次
	 * @param application
	 *            指定应用
	 * @return 预置信息<br/>
	 *         null-如果应用没有在指定批次上预置
	 */
	public CardBaseApplication getByCardBaseAndApplicationThatPreset(CardBaseInfo cardBase, Application application);
}
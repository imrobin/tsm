package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

public interface CardBaseApplicationDao extends EntityDao<CardBaseApplication, Long> {

	/**
	 * 根据卡批次和应用找到指定卡批次所支持的指定应用的状态为“已发布”的列表，安装应用版本号降序排序
	 * 
	 * @param cardBaseInfo
	 *            卡批次
	 * @param app
	 *            应用
	 * @return 查找结果
	 */
	List<CardBaseApplication> getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(CardBaseInfo cardBaseInfo, Application app);

	CardBaseApplication getByCardBaseAndAppver(CardBaseInfo cbi, ApplicationVersion appver);
	
	List<CardBaseApplication> getByCardBase(CardBaseInfo cbi);

	/**
	 * 获取卡批次关联过的同一批次的应用.且
	 * @param application
	 * @param cbi
	 * @return
	 */
	CardBaseApplication getByApplicationAndCardbaseAndPresetMode(Application application, CardBaseInfo cbi);

	List<CardBaseApplication> getBySDandCBIAndPreset(SecurityDomain securityDomain, CardBaseInfo cbi);
}
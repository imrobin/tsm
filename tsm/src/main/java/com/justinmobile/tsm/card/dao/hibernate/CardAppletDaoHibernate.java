package com.justinmobile.tsm.card.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.card.dao.CardAppletDao;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;

@Repository("cardAppletDao")
public class CardAppletDaoHibernate extends EntityDaoHibernate<CardApplet, Long> implements CardAppletDao {

	@Override
	public CardApplet getBycardNoAndAppletAid(String cardNo, String appletAid) {
		String hql = "from " + CardApplet.class.getName() + " as ca where ca.card.cardNo = ?  and ca.applet.aid = ?";
		return findUniqueEntity(hql, cardNo, appletAid);
	}

	@Override
	public List<CardApplet> getByCardNoAndAppAid(String cardNo, String appAid) {
		String hql = "from " + CardApplet.class.getName()
				+ " as ca where ca.card.cardNo = ?  and ca.applet.applicationVersion.application.aid = ?";
		return find(hql, cardNo, appAid);
	}

	@Override
	public List<CardApplet> getByCardNoAndApplicationVersionThatCreateLoadFileVersion(CardInfo card, ApplicationVersion applicationVersion,
			LoadFileVersion loadFileVersion) {
		String hql = "from " + CardApplet.class.getName()
				+ " as ca where ca.card=? and ca.applet.applicationVersion=? and ca.applet.loadModule.loadFileVersion=?";
		return find(hql, card, applicationVersion, loadFileVersion);
	}

	@Override
	public List<CardApplet> getByCardAndAppSd(String cardNo, String sdAid) {
		String hql = "from " + CardApplet.class.getName()
				+ " as ca where ca.card.cardNo = ? and ca.applet.applicationVersion.application.sd.aid = ?";
		return find(hql, cardNo, sdAid);
	}

	@Override
	public CardApplet getByCardAndApplet(CardInfo card, Applet applet) {
		String hql = "from " + CardApplet.class.getName() + " as ca where ca.card = ? and ca.applet = ?";
		return findUniqueEntity(hql, card, applet);
	}

	@Override
	public List<CardApplet> getByCardNoThatCreateLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion) {
		String hql = "from " + CardApplet.class.getName() + " as ca where ca.card=? and ca.applet.loadModule.loadFileVersion=?";
		return find(hql, card, loadFileVersion);
	}

}
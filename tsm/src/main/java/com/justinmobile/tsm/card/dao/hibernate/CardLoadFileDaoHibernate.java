package com.justinmobile.tsm.card.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardLoadFileDao;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;

@Repository("cardLoadFileDao")
public class CardLoadFileDaoHibernate extends EntityDaoHibernate<CardLoadFile, Long> implements CardLoadFileDao {

	@Override
	public CardLoadFile getByCardAndLoadFileVersion(CardInfo card, LoadFileVersion loadFileVersion) {
		String hql = "from " + CardLoadFile.class.getName() + " as clf where clf.loadFileVersion=:loadFileVersion and clf.card=:card";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("loadFileVersion", loadFileVersion);
		values.put("card", card);

		return findUnique(hql, values);
	}

	@Override
	public CardLoadFile getByAidAndCardNo(String aid, String cardNo) {
		String hql = "from " + CardLoadFile.class.getName() + " as clf where clf.loadFileVersion.loadFile.aid = ? and clf.card.cardNo = ?";
		return findUniqueEntity(hql, aid, cardNo);
	}

	@Override
	public List<CardLoadFile> getCardLoadFileBySd(long sdId, String cardNo) {
		String hql = "from " + CardLoadFile.class.getName() + " as clf where clf.loadFileVersion.loadFile.sd.id = ? and clf.card.cardNo = ?";
		return find(hql, sdId, cardNo);
	}

	@Override
	public List<CardLoadFile> getByCardAndLoadFileSd(CardInfo card, SecurityDomain sd) {
		String hql = "from " + CardLoadFile.class.getName() + " as clf where clf.loadFileVersion.loadFile.sd = ? and clf.card = ?";
		return find(hql, sd, card);
	}

}
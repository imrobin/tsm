package com.justinmobile.tsm.card.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardBaseLoadFileDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;

@Repository("cardBaseLoadFileDao")
public class CardBaseLoadFileDaoHibernate extends EntityDaoHibernate<CardBaseLoadFile, Long> implements CardBaseLoadFileDao {

	@Override
	public CardBaseLoadFile getByCardBaseAndVersion(CardBaseInfo cbi, LoadFileVersion lfv) {
		String hql = "from " + CardBaseLoadFile.class.getName() + " as cblf where cblf.cardBaseInfo = ? and cblf.loadFileVersion = ?";
		return findUniqueEntity(hql, cbi,lfv);
	}

	@Override
	public CardBaseLoadFile getByLoadfileAndCardbase(LoadFile loadFile, CardBaseInfo cardBase) {
		String hql = "from " + CardBaseLoadFile.class.getName() + " as cblf where cblf.cardBaseInfo = ? and cblf.loadFileVersion.loadFile = ?";
		return findUniqueEntity(hql,cardBase,loadFile);
	}

	@Override
	public List<CardBaseLoadFile> getBySdAndCardBase(SecurityDomain securityDomain, CardBaseInfo cbi) {
		String hql = "from " + CardBaseLoadFile.class.getName() + " as cblf where cblf.cardBaseInfo = ? and cblf.loadFileVersion.loadFile.sd = ?";
		return find(hql,cbi,securityDomain);
	}
}
package com.justinmobile.tsm.application.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.dao.TestFileDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.TestFile;

@Repository("testFiletDao")
public class TestFileDaoHibernate extends EntityDaoHibernate<TestFile, Long> implements TestFileDao {

	@Override
	public int getMaxSeqNumInAppver(ApplicationVersion appver) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "select max(tf.seqNum) from " + TestFile.class.getName() + " as tf where tf.appVer = :appver";
		Query query = session.createQuery(hql);
		query.setEntity("appver", appver);
		List<?> list = query.list();
		if(null == list.get(0)){
			return 0;
		}else{
			return (Integer) list.get(0);
		}
	}

	@Override
	public Page<TestFile> getTestFileList(Page<TestFile> page) {
		String hql = "from " + TestFile.class.getName() + " as tf where tf.appVer.status in (2,3,4)";
		return this.findPage(page, hql);
	}

	
}
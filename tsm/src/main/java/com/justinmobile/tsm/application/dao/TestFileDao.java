package com.justinmobile.tsm.application.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.TestFile;

public interface TestFileDao extends EntityDao<TestFile, Long> {

	int getMaxSeqNumInAppver(ApplicationVersion appver);

	Page<TestFile> getTestFileList(Page<TestFile> page);


}
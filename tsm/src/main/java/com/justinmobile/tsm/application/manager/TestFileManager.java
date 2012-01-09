package com.justinmobile.tsm.application.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.TestFile;

@Transactional
public interface TestFileManager extends EntityManager<TestFile> {

	/**
	 * 完成上传测试文件
	 * @param appVerId 应用版本ID
	 * @param fileName	测试文件业务名称	
	 * @param fileComment 测试文件说明
	 * @param servletPath  系统路径
	 * @param tempFileName2 测试文件上传的临时文件
	 */
	void finishUpload(String appVerId, String fileName, String fileComment, String tempFileName, String servletPath);

	/**
	 * 删除测试文件
	 * @param servletPath 
	 * @param testFileId
	 */
	void delTestFile(Long tfId, String servletPath);

	/**
	 * 根据应用版本查找测试文件
	 * @param av
	 * @return
	 */
	List<TestFile> findByAppver(ApplicationVersion av);

	/**
	 * 获取常用的测试文件
	 * @param page
	 * @return
	 */
	Page<TestFile> getTestFileList(Page<TestFile> page);

}
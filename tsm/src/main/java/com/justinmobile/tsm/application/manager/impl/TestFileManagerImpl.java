package com.justinmobile.tsm.application.manager.impl;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.TestFileDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.TestFile;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.TestFileManager;

@Service("testFileManager")
public class TestFileManagerImpl extends EntityManagerImpl<TestFile, TestFileDao> implements TestFileManager {
	
	@Autowired
	private TestFileDao testFileDao;
	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Override
	public void finishUpload(String appVerId, String originalName, String fileComment, String tempFileName, String servletPath) {
		try {
			String fullFileName = servletPath + File.separator + "temp" + File.separator + tempFileName;
			File tempFile = new File(fullFileName);
			if(!tempFile.exists()){
				throw new PlatformException(PlatformErrorCode.DB_ERROR);
			}
			TestFile testFile = new TestFile();
			ApplicationVersion appver = applicationVersionManager.load(Long.valueOf(appVerId));
			testFile.setAppVer(appver);
			testFile.setComments(fileComment);
			testFile.setFileName(tempFileName);
			testFile.setOriginalName(originalName);
			String filePath = moveTempFileToCustomDir(tempFile,servletPath);
			testFile.setFilePath(filePath);
			int sequm = testFileDao.getMaxSeqNumInAppver(appver);
			testFile.setSeqNum(sequm + 1);
			testFile.setSp(appver.getApplication().getSp());
			testFile.setUploadDate(Calendar.getInstance());
			testFileDao.saveOrUpdate(testFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}

	private String moveTempFileToCustomDir(File tempFile, String servletPath) {
		ResourceBundle rb = ResourceBundle.getBundle("config/uploadfile");
		String path = rb.getString("testFileDir");
		String targetPath = servletPath + path;
		File targetDir = new File(targetPath);
		if(!targetDir.exists()){
			targetDir.mkdirs();
		}
		File targetFile = new File(targetPath + tempFile.getName());
		tempFile.renameTo(targetFile);
		tempFile.deleteOnExit();
		return path;
	}

	@Override
	public void delTestFile(Long tfId ,String servletPath) {
		try {
			TestFile testFile = this.load(tfId);
			String filePath = servletPath + testFile.getFilePath() + testFile.getFileName();
			delFile(filePath);
			this.remove(testFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}

	private void delFile(String filePath) {
		File file = new File(filePath);
		file.deleteOnExit();
		file.delete();
	}

	@Override
	public List<TestFile> findByAppver(ApplicationVersion av) {
		try {
			return testFileDao.findByProperty("appVer", av);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<TestFile> getTestFileList(Page<TestFile> page) {
		try {
			return testFileDao.getTestFileList(page);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
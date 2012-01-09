package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationLoadFileDao;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;

@Service("applicationLoadFileManager")
public class ApplicationLoadFileManagerImpl extends EntityManagerImpl<ApplicationLoadFile, ApplicationLoadFileDao> implements
		ApplicationLoadFileManager {

	@Autowired
	private ApplicationLoadFileDao applicationLoadFileDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private LoadFileVersionManager loadFileVersionManager;

	@Override
	public void setDownloadOrder(Long loadFileVersionId, Long applicationVersionId, Integer order, String username) {
		try {
			@SuppressWarnings("deprecation")
			ApplicationLoadFile applicationLoadFile = applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(
					applicationVersionId, loadFileVersionId);
			if (null == applicationLoadFile) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST);
			}

			// 判断SP是否有权设置下载顺序
			if (!isEditable(username, applicationLoadFile)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_SET_DOWNLOAD_ORDER_SP_DISCARD);
			}

			applicationLoadFile.setDownloadOrder(order);

			applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationLoadFile> getAllByDownloadOrder(ApplicationVersion applicationVersion) {
		try {
			return applicationLoadFileDao.getByApplicationVersionAsDownloadOrder(applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationLoadFile> getAllByDeleteOrder(ApplicationVersion applicationVersion) {
		try {
			return applicationLoadFileDao.getByApplicationVersionAsDeleteOrder(applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationLoadFile> getExclusiveByDownloadOrder(Long applicationVersionId) {
		try {
			return applicationLoadFileDao.getExclusiveByDownloadOrder(applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationLoadFile> getExclusiveByDeleteOrder(Long applicationVersionId) {
		try {
			return applicationLoadFileDao.getExclusiveByDeleteOrder(applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void setDeleteOrder(Long loadFileVersionId, Long applicationVersionId, Integer order, String username) {
		try {
			@SuppressWarnings("deprecation")
			ApplicationLoadFile applicationLoadFile = applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(
					applicationVersionId, loadFileVersionId);
			if (null == applicationLoadFile) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST);
			}

			// 判断SP是否有权设置删除顺序
			if (!isEditable(username, applicationLoadFile)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_SET_DOWNLOAD_ORDER_SP_DISCARD);
			}

			applicationLoadFile.setDeleteOrder(order);

			applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public ApplicationLoadFile getByApplicationVersionAndLoadFileVersion(Long loadFileVersionId, Long applicationVersionId) {
		return applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(applicationVersionId, loadFileVersionId);
	}

	@Override
	public void removeImportBetweenLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId, String username) {
		try {
			@SuppressWarnings("deprecation")
			ApplicationLoadFile applicationLoadFile = applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(
					applicationVersionId, loadFileVersionId);
			if (null == applicationLoadFile) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST);
			}

			if (!isEditable(username, applicationLoadFile)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_REMOVE_IMPORT_SP_DISCARD);
			}

			LoadFileVersion loadFileVersion = applicationLoadFile.getLoadFileVersion();
			applicationLoadFileDao.remove(applicationLoadFile);

			loadFileVersionManager.removeIfNotImport(loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public ApplicationLoadFile buildImportBetweenLoadFileVersionAndApplicationVersion(Long loadFileVersionId, Long applicationVersionId,
			String username) {
		try {
			ApplicationLoadFile applicationLoadFile = new ApplicationLoadFile();

			LoadFileVersion loadFileVersion = loadFileVersionManager.load(loadFileVersionId);
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);

			if (!applicationVersionManager.isEditable(username, applicationVersion)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_BUILD_IMPORT_SP_DISCARD);
			}

			if ((SecurityDomain.MODEL_ISD == applicationVersion.getApplication().getSdModel().intValue())
					&& (SecurityDomain.MODEL_ISD != loadFileVersion.getLoadFile().getSdModel())) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_LOAD_FILE_SD_DISCARD);
			}

			applicationLoadFile.assignApplicationVersionAndLoadFileVersion(applicationVersion, loadFileVersion);
			applicationLoadFileDao.saveOrUpdate(applicationLoadFile);

			return applicationLoadFile;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void sortDownloadOrder(ApplicationVersion applicationVersion) {
		try {
			Long applicationVersionId = applicationVersion.getId();
			@SuppressWarnings("deprecation")
			List<LoadFileVersion> sharedLoadFileVersion = loadFileVersionManager.getWhichImportedByApplicationVersion(applicationVersionId,
					LoadFile.FLAG_SHARED);

			// 计算公用加载文件的下载顺序
			List<LoadFileVersion> sharedDownloadOrder = loadFileVersionManager.calcDependenceAsDownloadOrder(sharedLoadFileVersion);
			List<ApplicationLoadFile> exclusiveDownloadOrder = getExclusiveByDownloadOrder(applicationVersionId);

			int order = 1;

			// 首先下载公用加载文件
			for (int i = 0; i < sharedDownloadOrder.size(); i++) {
				LoadFileVersion loadFileVersion = sharedDownloadOrder.get(i);
				ApplicationLoadFile applicationLoadFile = applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(
						applicationVersion, loadFileVersion);
				if (null == applicationLoadFile) {// 加载文件版本和应用版本还未建立引入关系
					applicationLoadFile = new ApplicationLoadFile();
					applicationLoadFile.assignApplicationVersionAndLoadFileVersion(applicationVersion, loadFileVersion);
				}
				applicationLoadFile.setDownloadOrder(order);
				order++;

				applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
			}

			// 然后下载自有加载文件
			for (int i = 0; i < exclusiveDownloadOrder.size(); i++) {
				ApplicationLoadFile applicationLoadFile = exclusiveDownloadOrder.get(i);
				applicationLoadFile.setDownloadOrder(order);
				order++;

				applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void sortDeleteOrder(ApplicationVersion applicationVersion) {
		try {
			Long applicationVersionId = applicationVersion.getId();
			@SuppressWarnings("deprecation")
			List<LoadFileVersion> sharedLoadFileVersion = loadFileVersionManager.getWhichImportedByApplicationVersion(applicationVersionId,
					LoadFile.FLAG_SHARED);

			// 计算公用加载文件的下载顺序
			List<LoadFileVersion> sharedDeleteOrder = loadFileVersionManager.calcDependenceAsDeleteOrder(sharedLoadFileVersion);
			List<ApplicationLoadFile> exclusiveDeleteOrder = getExclusiveByDeleteOrder(applicationVersionId);

			int order = 1;

			// 首先删除自有加载文件
			for (int i = 0; i < exclusiveDeleteOrder.size(); i++) {
				ApplicationLoadFile applicationLoadFile = exclusiveDeleteOrder.get(i);
				applicationLoadFile.setDeleteOrder(order);
				order++;

				applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
			}

			// 然后删除公用加载文件
			for (int i = 0; i < sharedDeleteOrder.size(); i++) {
				LoadFileVersion loadFileVersion = sharedDeleteOrder.get(i);
				ApplicationLoadFile applicationLoadFile = applicationLoadFileDao.getByApplicationVersionAndLoadFileVersion(
						applicationVersion, loadFileVersion);
				if (null == applicationLoadFile) {// 加载文件版本和应用版本还未建立引入关系
					applicationLoadFile = new ApplicationLoadFile();
					applicationLoadFile.assignApplicationVersionAndLoadFileVersion(applicationVersion, loadFileVersion);
				}
				applicationLoadFile.setDeleteOrder(order);
				order++;

				applicationLoadFileDao.saveOrUpdate(applicationLoadFile);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isEditable(String username, ApplicationLoadFile applicationLoadFile) {
		return applicationVersionManager.isEditable(username, applicationLoadFile.getApplicationVersion());
	}

	@Override
	public ApplicationLoadFile getByApplicationVersionAndLoadFile(ApplicationVersion applicationVersion, LoadFile loadFile) {
		try {
			return applicationLoadFileDao.getByApplicationVersionAndLoadFile(applicationVersion, loadFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(ApplicationLoadFile entity) {
		LoadFileVersion loadFileVersion = entity.getLoadFileVersion();
		loadFileVersion.removeApplicationLoadFile(entity);
		loadFileVersionManager.removeIfNotImport(loadFileVersion);

		applicationLoadFileDao.remove(entity);
	}
}
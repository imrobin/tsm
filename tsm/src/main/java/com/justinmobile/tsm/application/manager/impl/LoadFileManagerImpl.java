package com.justinmobile.tsm.application.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;
import com.justinmobile.tsm.application.manager.LoadModuleManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Service("loadFileManager")
public class LoadFileManagerImpl extends EntityManagerImpl<LoadFile, LoadFileDao> implements LoadFileManager {

	@Autowired
	private LoadFileDao loadFileDao;

	@Autowired
	private SpBaseInfoManager spManager;

	@Autowired
	private SecurityDomainManager sdManager;

	@Autowired
	private LoadFileVersionManager loadFileVersionManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private LoadModuleManager loadModuleManager;

	@Autowired
	private AppletManager appletManager;

	@Override
	public void createNewLoadFileForApplicationVersion(LoadFile loadFile, LoadFileVersion loadFileVersion, Map<String, String> params,
			String username) {
		try {
			createNewLoadFile(loadFile, loadFileVersion, params, LoadFile.FLAG_EXCLUSIVE, username);

			// 处理加载文件版本信息
			params.put("loadFileId", loadFile.getId().toString());
			loadFileVersionManager.createNewLoadFileVersionForApplicaitonVersion(loadFileVersion, params, username);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void createNewSharedLoadFile(LoadFile loadFile, LoadFileVersion loadFileVersion, Map<String, String> params, String username) {
		try {
			createNewLoadFile(loadFile, loadFileVersion, params, LoadFile.FLAG_SHARED, username);

			// 处理加载文件版本信息
			params.put("loadFileId", loadFile.getId().toString());
			loadFileVersionManager.createNewLoadFileVersion(loadFileVersion, params, username);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void createNewLoadFile(LoadFile loadFile, LoadFileVersion loadFileVersion, Map<String, String> params, Integer shareFlag,
			String username) {
			// 关联SP
			SpBaseInfo sp = spManager.getSpByNameOrMobileOrEmail(username);
			loadFile.setSp(sp);

			loadFile.validateAid();

			// 关联安全域
			SecurityDomain sd = null;
			if (SecurityDomain.MODEL_ISD == loadFile.getSdModel()) {
				sd = sdManager.getIsd();
			} else if ((SecurityDomain.MODEL_DAP == loadFile.getSdModel()) || (SecurityDomain.MODEL_TOKEN == loadFile.getSdModel())) {
				sd = sdManager.load(Long.parseLong(params.get("sdId")));
			}
			loadFile.setSd(sd);

			// 将加载文件设置为不共享
			loadFile.setShareFlag(shareFlag);

			// 字段格式化
			loadFile.fomateField();

			// 保存
			loadFileDao.saveOrUpdate(loadFile);
	}

	@Override
	public List<LoadFile> getExclusiveLoadFilesBySpAndApplicationVersion(Long applicationVersionId, String username) {
		try {

			SpBaseInfo sp = spManager.getSpByNameOrMobileOrEmail(username);
			return loadFileDao.getLoadFilesWhichExclusivAndBelongSpAndUnassociateWithApplicationVersion(sp, applicationVersionId);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LoadFile> getSharedLoadFilesWhichUnassociateWithApplicationVersion(Long applicationVersionId) {
		try {

			return loadFileDao.getSharedLoadFilesWhichUnassociateWithApplicationVersion(applicationVersionId);

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isEditable(String username, LoadFile loadFile) {
		try {
			SpBaseInfo requestSp = spManager.getSpByNameOrMobileOrEmail(username);
			SpBaseInfo ownerSp = loadFile.getSp();
			if (!ownerSp.equals(requestSp)) {
				return false;
			} else {
				return true;
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
	public List<LoadFile> getUndependentLoadFiles(LoadFileVersion loadFileVersion) {
		try {
			return loadFileDao.getThatIsSharedAndIsnotSelfAndIsnotDependent(loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public Page<LoadFile> loadByIds(Page<LoadFile> page, String loadFileIds) {
		try {
			return loadFileDao.loadByIds(page, loadFileIds);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void validateAid(String aid) {
		try {
			if (!applicationManager.isPropertyUnique("aid", aid, null)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_REDULICATE, "应用");
			}

			if (!loadFileDao.isPropertyUnique("aid", aid, null)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_REDULICATE, "其他加载文件");
			}

			if (!loadModuleManager.isPropertyUnique("aid", aid, null)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_REDULICATE, "模块");
			}

			if (!appletManager.isPropertyUnique("aid", aid, null)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_REDULICATE, "实例");
			}

			if (!sdManager.isAidUnique(aid)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_REDULICATE, "安全域");
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
	public LoadFile getByAid(String aid) {
		try {
			return loadFileDao.findUniqueByProperty("aid", aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(LoadFile loadFile, String username) throws PlatformException {
		try {
			if (!isEditable(username, loadFile)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
			}

			for (LoadFileVersion version : loadFile.getVersions()) {
				if (0 != version.getApplicationLoadFiles().size()) {
					throw new PlatformException(PlatformErrorCode.LOAD_FILE_USED);
				} else if (0 != version.getChildren().size()) {
					throw new PlatformException(PlatformErrorCode.LOAD_FILE_DEPENDED);
				}
			}

			super.remove(loadFile);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LoadFile> getUnusedByApplicationVersionAndType(ApplicationVersion applicationVersion, int fileType) {
		try {
			return loadFileDao.getUnusedByApplicationVersionAndType(applicationVersion, fileType);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void removeIfNotHasVersion(LoadFile loadFile) {
		if (0 == loadFile.getVersions().size()) {
			loadFileDao.remove(loadFile);
		}

	}
}
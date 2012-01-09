package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.LoadModuleDao;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;
import com.justinmobile.tsm.application.manager.LoadModuleManager;

@Service("loadModuleManager")
public class LoadModuleManagerImpl extends EntityManagerImpl<LoadModule, LoadModuleDao> implements LoadModuleManager {

	@Autowired
	private LoadModuleDao loadModuleDao;

	@Autowired
	private LoadFileVersionManager loadFileVersionManager;

	@Autowired
	private AppletManager appletManager;

	@Autowired
	private LoadFileManager loadFileManager;

	@Override
	public void createNewLoadModule(LoadModule loadModule, long loadFileVersionId, String username) {
		try {
			LoadFileVersion loadFileVersion = loadFileVersionManager.load(loadFileVersionId);

			// 格式化字段
			loadModule.fomateField();

			// 判断SP是否有权利创建该加载文件的模块
			if (!loadFileVersionManager.isEditable(username, loadFileVersion)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
			}

			// 判断模块AID在所属的加载文件版本范围内是否重复
			validateAidUsable(loadModule.getAid(), loadFileVersion);

			loadFileVersion.addLoadModule(loadModule);

			// 验证AID
			loadModule.validateAid();

			loadModuleDao.saveOrUpdate(loadModule);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void removeLoadModule(Long loadModuleId, String username) {
		LoadModule loadModule = loadModuleDao.load(loadModuleId);

		// 判断SP是否有权利创建删除模块
		if (!isEditable(username, loadModule)) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
		}

		int count = appletManager.getCountThatBelongLoadModule(loadModuleId);
		if (0 != count) {
			throw new PlatformException(PlatformErrorCode.LOAD_MODULE_DEFINED_APPLET);
		}

		loadModuleDao.remove(loadModule);
	}

	@Override
	public boolean isEditable(String username, LoadModule loadModule) {
		return loadFileVersionManager.isEditable(username, loadModule.getLoadFileVersion());
	}

	public void validateAidUsable(String aid, LoadFileVersion loadFileVersion) {
		try {
			if (!loadFileManager.isPropertyUnique("aid", aid, null)) {// 模块AID不能和文件AID相同
				throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_REDUPLICATE, "加载文件AID");
			}

			List<LoadModule> loadModules = getByAid(aid);
			for (LoadModule loadModule : loadModules) {
				if (loadFileVersion.equals(loadModule.getLoadFileVersion())) {// 模块AID不能和相同文件版本的模块AID相同
					throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_REDUPLICATE, "同一加载文件、同一版本的其他模块AID");
				} else if (!loadFileVersion.getLoadFile().equals(loadModule.getLoadFileVersion().getLoadFile())) {// 模块AID不能和不同文件的模块AID相同
					throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_REDUPLICATE, "不同加载文件的模块AID");
				}
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private List<LoadModule> getByAid(String aid) {
		try {
			return loadModuleDao.findByProperty("aid", aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}
}
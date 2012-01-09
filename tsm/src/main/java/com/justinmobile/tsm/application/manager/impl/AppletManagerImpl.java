package com.justinmobile.tsm.application.manager.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.LoadModuleManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;

@Service("appletManager")
public class AppletManagerImpl extends EntityManagerImpl<Applet, AppletDao> implements AppletManager {

	@Autowired
	private AppletDao appletDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private LoadFileManager loadFileManager;

	@Autowired
	private LoadModuleManager loadModuleManager;

	@Autowired
	private SecurityDomainManager sdManager;

	@Override
	public void createNewApplet(Applet applet, Long applicationVersionId, Long loadModuleId, String username) {
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			LoadModule loadModule = loadModuleManager.load(loadModuleId);

			// 字段格式化
			applet.formatFiled();

			// 判断SP是否有权利创建实例
			if (!applicationVersionManager.isEditable(username, applicationVersion)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_DEFINE_APPLET_SP_DISCARD);
			}

			validateAid(applet.getAid(), applicationVersion);

			applet.assignApplicationVersion(applicationVersion);
			applet.assignLoadModule(loadModule);

			// 验证AID
			applet.validateAid();

			appletDao.saveOrUpdate(applet);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void validateAid(String aid, ApplicationVersion applicationVersion) {
		try {
			if (!loadFileManager.isPropertyUnique("aid", aid, null)) {// 应用AID能与文件AID重复
				throw new PlatformException(PlatformErrorCode.APPLET_AID_REDUPLICATE, "加载文件");
			}

			if (!sdManager.isAidUnique(aid)) {// 应用AID能与安全域AID重复
				throw new PlatformException(PlatformErrorCode.APPLET_AID_REDUPLICATE, "安全域");
			}

			List<Applet> applets = getByAid(aid);
			for (Applet applet : applets) {
				if (applicationVersion.equals(applet.getApplicationVersion())) {// 应用AID能与相同应用版本的实例AID重复
					throw new PlatformException(PlatformErrorCode.APPLET_AID_REDUPLICATE, "相同应用版本的实例");
				} else if (!applicationVersion.getApplication().equals(applet.getApplicationVersion().getApplication())) {// 应用AID能与不同应用的实例AID重复
					throw new PlatformException(PlatformErrorCode.APPLET_AID_REDUPLICATE, "不同应用的实例");
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

	@Override
	public boolean isEditable(String username, Applet applet) {
		return applicationVersionManager.isEditable(username, applet.getApplicationVersion());
	}

	@Override
	public List<Applet> getInstallOrder(Long applicationVersionId) {
		try {
			return appletDao.getByInstallOrder(applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void setInstallOrder(Long appletId, Integer order, String username) {
		try {
			Applet applet = appletDao.load(appletId);

			// 判断SP是否有权利定义安装顺序
			if (!isEditable(username, applet)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_SET_INSTALL_ORDER_SP_DISCARD);
			}

			applet.setOrderNo(order);

			appletDao.saveOrUpdate(applet);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Applet> getAppletGeneratLoadFileVersionAndImportApplicationVersion(Long loadFileVersionId, Long applicationVersionId) {
		try {
			return appletDao.getByLoadFileVersionAndApplicationVersion(loadFileVersionId, applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void removeApplet(Long appletId, String username) {
		try {
			Applet applet = appletDao.load(appletId);

			// 判断SP是否有权利删除实例
			if (!isEditable(username, applet)) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_REMOVE_APPLET_SP_DISCARD);
			}

			appletDao.remove(applet);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public int getCountThatBelongLoadModule(long loadModuleId) {
		try {
			return appletDao.getCountThatBelongLoadModule(loadModuleId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Applet> getByAid(String aid) throws PlatformException {
		try {
			return appletDao.findByProperty("aid", aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Applet getByAidAndLoadModuleAndApplicationVersion(String aid, LoadModule loadModule, ApplicationVersion applicationVersion) {
		try {
			return appletDao.getByAidAndLoadModuleAndApplicationVersion(aid, loadModule, applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
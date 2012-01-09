package com.justinmobile.tsm.application.manager.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.dao.ApplicationClientInfoDao;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.manager.ApplicationClientInfoManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.utils.FileUtils;

@Service("applicationClientInfoManager")
public class ApplicationClientInfoManagerImpl extends
		EntityManagerImpl<ApplicationClientInfo, ApplicationClientInfoDao> implements ApplicationClientInfoManager {

	private static final Logger log = LoggerFactory.getLogger(ApplicationClientInfoManager.class);

	@Autowired
	private ApplicationClientInfoDao applicationClientInfoDao;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Override
	public void uploadApplicationClient(ApplicationClientInfo client, String tempFileAbsPath, String saveFileAbsDir,
			long applicationVersionId, String filename, String tempIconAbsPath) {
		try {
			String extension = FilenameUtils.getExtension(filename);
			client.setFileType(extension.toLowerCase());

			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			ApplicationClientInfo clientExsit = applicationClientInfoDao
					.getByApplicationVersionAndSysRequirmentAndFileTypeAndVersion(applicationVersion,
							client.getSysRequirment(), client.getFileType(), client.getVersion());
			if (null != clientExsit) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_CLIENT_EXSIT);
			}

			// 将临时文件拷贝到指定目录下
			String clientRalPath = FileUtils.generateApplicationCilentAbsPath(RandomStringUtils.randomAlphabetic(8)
					+ File.separator + filename);

			File src = new File(tempFileAbsPath);
			File dest = new File(saveFileAbsDir + clientRalPath);

			org.apache.commons.io.FileUtils.copyFile(src, dest);

			if (StringUtils.isBlank(tempIconAbsPath)) {
				client.setIcon(null);
			} else {
				client.setIcon(ConvertUtils.file2ByteArray(tempIconAbsPath));
			}

			// 设置字段
			String url = StringUtils.replace(clientRalPath, "\\", "/");
			log.debug("\n" + "absPath: " + dest.getAbsolutePath() + "\n");
			log.debug("\n" + "url: " + url + "\n");
			client.setFileUrl(url);
			client.setSize(dest.length());
			client.setBusiType(ApplicationClientInfo.BUSI_TYPE_APPLICATION_CLIENT);
			client.getApplicationVersions().add(applicationVersion);
			client.setStatus(ApplicationClientInfo.STATUS_RELEASE);
			client.setFilePath(dest.getAbsolutePath());

			applicationClientInfoDao.saveOrUpdate(client);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (IOException e) {
			throw new PlatformException(PlatformErrorCode.FILE_COPY_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public Page<ApplicationClientInfo> getByApplicationVersion(Page<ApplicationClientInfo> page,
			Long applicationVersionId) {
		try {
			return applicationClientInfoDao.getByApplicationVersion(page, applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationClientInfo> getByAidAndCardNo(String aid, String cardNo) {
		try {
			List<ApplicationClientInfo> applicationClientInfos = new ArrayList<ApplicationClientInfo>();
			CustomerCardInfo cci = customerCardInfoManager.getByCardNo(cardNo);
			CardApplication ca = cardApplicationManager.getAvailbleOrLockedByCardNoAid(cci.getCard().getCardNo(), aid);
			boolean hasSysRequirment = customerCardInfoManager.hasSysRequirment(cci, ca);
			if (!hasSysRequirment) {
				// mappedApplication.put("clientStatusStr",
				// PlatformErrorCode.NOT_DOWN_CLINET.getDefaultMessage());
			} else {
				MobileType mt = cci.getMobileType();
				Set<ApplicationClientInfo> acs = ca.getApplicationVersion().getClients();
				ApplicationClientInfo androidTemp = null;
				ApplicationClientInfo j2meacTemp = null;
				for (Iterator<ApplicationClientInfo> it = acs.iterator(); it.hasNext();) {
					ApplicationClientInfo ac = (ApplicationClientInfo) it.next();
					if (mt.getOriginalOsKey().equals(ac.getSysRequirment())) {
						// 应用详情-下载客户端：当同一手机型号对应了多个版本的Android客户端时
						// ，应该下载当前手机型号对应的最高版本的客户端，以版本号来判断，而不是上传时间。
						if (androidTemp == null || SpringMVCUtils.compareVersion(ac.getVersion(), androidTemp.getVersion())) {
							androidTemp = ac;
						}
					} else if (mt.getJ2meKey().equals(ac.getSysRequirment())) {
						if ((j2meacTemp == null || SpringMVCUtils.compareVersion(ac.getVersion(),j2meacTemp.getVersion()))){
							j2meacTemp = ac;
						}
					}
				}
				if (androidTemp != null){
					applicationClientInfos.add(androidTemp);
				}
				if (j2meacTemp != null){
					applicationClientInfos.add(j2meacTemp);
				}
			}
			return applicationClientInfos;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public ApplicationClientInfo getByApplicationVersionSysTypeSysRequirementFileType(ApplicationVersion appVer,
			String sysType, String sysRequirment, String fileType) {
		ApplicationClientInfo aci;
		try {
			aci = applicationClientInfoDao.getByApplicationVersionTypeVersionFileType(appVer, sysType, sysRequirment,
					fileType);
			return aci;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<ApplicationClientInfo> getAppManagerByTypeAndVersion(String sysType, String sysRequirment) {
		try {
			List<PropertyFilter> filters = Lists.newArrayList();
			filters.add(new PropertyFilter("status", MatchType.EQ, PropertyType.I, ApplicationClientInfo.STATUS_RELEASE
					.toString()));
			filters.add(new PropertyFilter("sysType", MatchType.EQ, PropertyType.S, sysType));
			filters.add(new PropertyFilter("sysRequirment", MatchType.EQ, PropertyType.S, sysRequirment));
			filters.add(new PropertyFilter("busiType", MatchType.EQ, PropertyType.I,
					ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER.toString()));
			return applicationClientInfoDao.find(filters, "version_desc");
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public ApplicationClientInfo getAppManagerByTypeAndReqAndVersion(String sysType, String sysRequirment,
			String clientVersion) {
		try {
			List<PropertyFilter> filters = Lists.newArrayList();
			filters.add(new PropertyFilter("sysType", MatchType.EQ, PropertyType.S, sysType));
			filters.add(new PropertyFilter("sysRequirment", MatchType.EQ, PropertyType.S, sysRequirment));
			filters.add(new PropertyFilter("busiType", MatchType.EQ, PropertyType.I,
					ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER.toString()));
			filters.add(new PropertyFilter("version", MatchType.EQ, PropertyType.S, clientVersion));
			List<ApplicationClientInfo> infos = applicationClientInfoDao.find(filters);
			if (CollectionUtils.isEmpty(infos)) {
				return null;
			}
			return infos.get(0);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<ApplicationClientInfo> getApplicationClientInfoForIndex(Page<ApplicationClientInfo> page,
			Map<String, Object> values) {
		try {
			return applicationClientInfoDao.getApplicationClientInfoForIndex(page, values);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(ApplicationClientInfo entity) throws PlatformException {
		try {
			if ((ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER.intValue() == entity.getBusiType().intValue())
					&& entity.getStatus().equals(ApplicationClientInfo.STATUS_RELEASE)) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_CLIENT_AREADY_RELEASE);
			}

			try {// 如果在删除文件和目录是有异常，忽略
				String absPath = entity.getFilePath();
				log.debug("\n" + "absPath: " + absPath + "\n");

				File file = new File(absPath);
				file.delete();

				String dirPath = FilenameUtils.getFullPath(absPath);
				File dir = new File(dirPath);
				String[] dirContent = dir.list();
				if (0 == dirContent.length) {
					dir.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			applicationClientInfoDao.remove(entity);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(Long id) throws PlatformException {
		try {
			ApplicationClientInfo entity = applicationClientInfoDao.load(id);
			remove(entity);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> getHistoryVersion(String sysType, String sysRequirment) {
		try {
			return applicationClientInfoDao.getHistoryVersion(sysType, sysRequirment);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Integer getMaxVersionCode(Integer busiType, String sysType, String sysRequirement) {
		try {
			return applicationClientInfoDao.getMaxVersionCode(busiType, sysType, sysRequirement);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Integer getMaxVersionCodeByAppVer(Integer busiType, String sysType, String sysRequirement, Long appVerId) {

		try {
			return applicationClientInfoDao.getMaxVersionCodeByAppVer(busiType, sysType, sysRequirement, appVerId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public String getMocamMaxVersion() {

		try {
			return applicationClientInfoDao.getMocamMaxVersion();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}
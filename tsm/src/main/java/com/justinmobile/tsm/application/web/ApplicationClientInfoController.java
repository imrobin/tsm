package com.justinmobile.tsm.application.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.MimeUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.manager.ApplicationClientInfoManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.MobileTypeManager;

@Controller("applicationClientInfoController")
@RequestMapping("/applicationClient/")
public class ApplicationClientInfoController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationClientInfoManager applicationClientInfoManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private MobileTypeManager mobileTypeManager;

	@RequestMapping
	public @ResponseBody
	JsonMessage uploadNewClient(HttpServletRequest request) {
		log.debug("\n" + "ApplicationClientInfoController.uploadCilent" + "\n");
		if (log.isDebugEnabled()) {
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
		}

		String tempDir = request.getParameter("tempDir");
		JsonMessage message = new JsonMessage();

		try {
			ApplicationClientInfo client = new ApplicationClientInfo();
			BindingResult result = SpringMVCUtils.bindObject(request, client);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String servletPath = request.getSession().getServletContext().getRealPath("/");
				long applicationVersionId = Long.parseLong(request.getParameter("applicationVersionId"));
				String tempFileAbsPath = request.getParameter("tempFileAbsPath");
				String filename = request.getParameter("fileName");
				String tempIconAbsPath = request.getParameter("tempIconAbsPath");

				applicationClientInfoManager.uploadApplicationClient(client, tempFileAbsPath, servletPath, applicationVersionId, filename,
						tempIconAbsPath);

				log.debug("\n" + "client.id: " + client.getId() + "\n");
				message.setMessage(client, "applicationVersions", "");

				// 删除存储临时文件的目录，此功能属于后台功能，即使有异常也不能通知前台
				try {
					log.debug(tempDir);
					FileUtils.deleteDirectory(new File(tempDir));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getByApplicationVersion(HttpServletRequest request, @RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationClientInfoController.getByApplicationVersion" + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");

		JsonResult result = new JsonResult();

		try {
			Page<ApplicationClientInfo> page = SpringMVCUtils.getPage(request);

			Page<ApplicationClientInfo> clients = applicationClientInfoManager.getByApplicationVersion(page, applicationVersionId);
			result.setPage(clients, "", "");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage removeClient(HttpServletRequest request, @RequestParam Long clientId) {
		log.debug("\n" + "ApplicationClientInfoController.removeClient" + "\n");
		log.debug("\n" + "clientId: " + clientId + "\n");

		JsonMessage message = new JsonMessage();

		try {
			applicationClientInfoManager.remove(clientId);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage add(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		ApplicationClientInfo aci = new ApplicationClientInfo();

		try {
			BindingResult result = SpringMVCUtils.bindObject(request, aci);
			aci.setBusiType(ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER);
			aci.setStatus(ApplicationClientInfo.STATUS_UNRELEASE);
			String fileUrl = aci.getFileUrl();
			String clientFileName = request.getParameter("clientFileName");
			String serveltPath = request.getSession().getServletContext().getRealPath("/");
			File file = new File(serveltPath + fileUrl);
			String newFilePath = StringUtils.substringBeforeLast(file.getAbsolutePath(), File.separator);
			file.renameTo(new File(newFilePath + File.separator + clientFileName));
			aci.setFileUrl(StringUtils.substringBeforeLast(fileUrl, "/") + "/" + clientFileName);
			aci.setFilePath(newFilePath + File.separator + clientFileName);
			String fileType = fileUrl.substring(fileUrl.lastIndexOf('.') + 1, fileUrl.length());
			aci.setFileType(fileType);

			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				ApplicationClientInfo info = applicationClientInfoManager.getAppManagerByTypeAndReqAndVersion(aci.getSysType(),
						aci.getSysRequirment(), aci.getVersion());
				if (info != null) {
					throw new PlatformException(PlatformErrorCode.APPLICATION_CLIENT_VERSION_REPEAT);
				}
				applicationClientInfoManager.saveOrUpdate(aci);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage checkVersionCode(HttpServletRequest request, @RequestParam String sysType, @RequestParam String sysRequirment,
			@RequestParam Integer versionCode) {
		JsonMessage message = new JsonMessage();
		try {
			Integer maxVersionCode;
			Long appVerId = ServletRequestUtils.getLongParameter(request, "applicationVersionId");
			if (appVerId != null) {
				maxVersionCode = applicationClientInfoManager.getMaxVersionCodeByAppVer(ApplicationClientInfo.BUSI_TYPE_APPLICATION_CLIENT,
						sysType, sysRequirment, appVerId);
			} else {
				maxVersionCode = applicationClientInfoManager.getMaxVersionCode(ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER,
						sysType, sysRequirment);
			}
			if (versionCode < maxVersionCode) {
				message.setSuccess(false);
				message.setMessage(maxVersionCode);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getAci(@RequestParam Long clientId) {
		log.debug("\n" + "ApplicationClientInfoController.getAci" + "\n");
		log.debug("\n" + "clientId: " + clientId + "\n");

		JsonMessage message = new JsonMessage();

		try {
			ApplicationClientInfo aci = applicationClientInfoManager.load(clientId);
			message.setMessage(aci.getStatus());
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage release(@RequestParam Long clientId) {
		log.debug("\n" + "ApplicationClientInfoController.release" + "\n");
		log.debug("\n" + "clientId: " + clientId + "\n");

		JsonMessage message = new JsonMessage();

		try {
			ApplicationClientInfo aci = applicationClientInfoManager.load(clientId);
			if (aci.getStatus().equals(ApplicationClientInfo.STATUS_RELEASE)) {
				throw new PlatformException(PlatformErrorCode.APPLICATION_CLIENT_RELEASE_TWICE);
			} else {
				aci.setStatus(ApplicationClientInfo.STATUS_RELEASE);
				applicationClientInfoManager.saveOrUpdate(aci);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getAllMobileWallet(HttpServletRequest request) {
		log.debug("\n" + "ApplicationClientInfoController.getAllMobileWallet" + "\n");

		JsonResult result = new JsonResult();
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			Page<ApplicationClientInfo> page = SpringMVCUtils.getPage(request);
			String name = ServletRequestUtils.getStringParameter(request, "search_LIKES_name", "");
			String status = ServletRequestUtils.getStringParameter(request, "search_EQI_status");
			values.put("name", name);
			values.put("status", status);
			values.put("busiType", ApplicationClientInfo.BUSI_TYPE_APPLICATION_MANAGER);
			page = applicationClientInfoManager.getApplicationClientInfoForIndex(page, values);
			result.setPage(page, null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setMessage(ex.getMessage());
			result.setSuccess(Boolean.FALSE);
		}
		return result;
	}

	@RequestMapping
	public void downloadJar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonMessage message = new JsonMessage();
		ApplicationClientInfo aci = null;
		OutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		try {
			String aid = ServletRequestUtils.getStringParameter(request, "aid");
			String versionNo = ServletRequestUtils.getStringParameter(request, "versionNo");
			String os = ServletRequestUtils.getStringParameter(request, "os");
			String sysRequirment = ServletRequestUtils.getStringParameter(request, "sysRequirment");
			ApplicationVersion appVer = applicationVersionManager.getAidAndVersionNo(aid, versionNo);
			aci = applicationClientInfoManager.getByApplicationVersionSysTypeSysRequirementFileType(appVer, os, sysRequirment,
					ApplicationClientInfo.FILE_TYPE_JAR);
			if (aci != null) {
				File stat = new File(request.getSession().getServletContext().getRealPath("/") + aci.getFileUrl());
				setHeader(request, response, aci);
				bis = new BufferedInputStream(new FileInputStream(stat));
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					out.write(buff, 0, bytesRead);
				}
				out.flush();
			}
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 
	 */
	@RequestMapping
	public void downloadAppManager(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonMessage message = new JsonMessage();
		OutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		try {
			Page<MobileType> page = SpringMVCUtils.getPage(request);
			String brand = ServletRequestUtils.getStringParameter(request, "brand").trim();
			// brand = new String(brand.getBytes("iso-8859-1"), "utf-8");
			String type = ServletRequestUtils.getStringParameter(request, "type").trim();
			MobileType mt = mobileTypeManager.getMobileByBrandAndType(page, brand, type).getResult().get(0);
			ApplicationClientInfo aci = genAci(mt);
			if (aci != null) {
				File stat = new File(request.getSession().getServletContext().getRealPath("/") + aci.getFileUrl());
				setHeader(request, response, aci);
				bis = new BufferedInputStream(new FileInputStream(stat));
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					out.write(buff, 0, bytesRead);
				}
				out.flush();
			}
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	@RequestMapping
	public void downloadCommonAppManager(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonMessage message = new JsonMessage();
		OutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		try {
			String sysType = ServletRequestUtils.getStringParameter(request, "sysType").trim();
			String sysRequirment = ServletRequestUtils.getStringParameter(request, "sysRequirment").trim();
			List<ApplicationClientInfo> acis = applicationClientInfoManager.getAppManagerByTypeAndVersion(sysType, sysRequirment);
			if (CollectionUtils.isNotEmpty(acis)) {
				ApplicationClientInfo aci = acis.get(0);// 取最新版
				File stat = new File(request.getSession().getServletContext().getRealPath("/") + aci.getFileUrl());
				setHeader(request, response, aci);
				bis = new BufferedInputStream(new FileInputStream(stat));
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					out.write(buff, 0, bytesRead);
				}
				out.flush();
			} else {
				throw new PlatformException(PlatformErrorCode.APPLICATION_CLIENT_VERSION_NOT_FOUND);
			}
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	@RequestMapping
	public void downloadByHref(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonMessage message = new JsonMessage();
		OutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		try {
			String href = ServletRequestUtils.getStringParameter(request, "href", "").trim();
			File stat = new File(request.getSession().getServletContext().getRealPath("/") + href);
			response.setContentType("application/jar;charset=UTF-8");
			String fileName = href.substring(href.lastIndexOf('/') + 1);
			fileName = MimeUtils.encodeFileName(request, fileName);
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			bis = new BufferedInputStream(new FileInputStream(stat));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				out.write(buff, 0, bytesRead);
			}
			out.flush();
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 获取应用客户端的历史版本
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getHistoryVersion(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		result.setSuccess(Boolean.TRUE);
		try {
			Page<MobileType> page = SpringMVCUtils.getPage(request);
			String brand = ServletRequestUtils.getStringParameter(request, "brand").trim();
			String type = ServletRequestUtils.getStringParameter(request, "type").trim();
			MobileType mt = mobileTypeManager.getMobileByBrandAndType(page, brand, type).getResult().get(0);
			String sysType = "";
			String sysRequirment = "";
			if (null != mt.getJ2meKey()) {
				if (null != mt.getOriginalOsKey()) {
					sysType = "os";
					sysRequirment = mt.getOriginalOsKey();
				} else {
					sysType = "j2me";
					sysRequirment = mt.getJ2meKey();
				}
			} else {
				if (null == mt.getOriginalOsKey()) {
					sysType = "undefine";
					sysRequirment = "undefine";
				} else {
					sysType = "os";
					sysRequirment = mt.getOriginalOsKey();
				}
			}
			List<Map<String, Object>> list = applicationClientInfoManager.getHistoryVersion(sysType, sysRequirment);
			result.setResult(list);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 通过应用管理器ID下载应用管理器
	 */
	@RequestMapping
	public void getAppManagerById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonMessage message = new JsonMessage();
		OutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		try {
			Long id = ServletRequestUtils.getLongParameter(request, "id");
			ApplicationClientInfo aci = applicationClientInfoManager.load(id);
			File stat = new File(request.getSession().getServletContext().getRealPath("/") + aci.getFileUrl());
			response.setContentType("application/" + aci.getFileType() + ";charset=UTF-8");
			String fileName = aci.getFileUrl().substring(aci.getFileUrl().lastIndexOf('/') + 1);
			fileName = MimeUtils.encodeFileName(request, fileName);
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			bis = new BufferedInputStream(new FileInputStream(stat));
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				out.write(buff, 0, bytesRead);
			}
			out.flush();
		} catch (PlatformException pe) {
			pe.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(pe.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
	}

	@RequestMapping
	public @ResponseBody
	JsonResult checkClient(HttpServletRequest request, HttpServletResponse response) {
		JsonResult result = new JsonResult();
		result.setSuccess(Boolean.FALSE);
		try {
			Page<MobileType> page = SpringMVCUtils.getPage(request);
			String brand = ServletRequestUtils.getStringParameter(request, "brand").trim();
			// 前后Post提交过来不需要转化字符编码
			// brand = new String(brand.getBytes("iso-8859-1"), "utf-8");
			String type = ServletRequestUtils.getStringParameter(request, "type").trim();
			MobileType mt = mobileTypeManager.getMobileByBrandAndType(page, brand, type).getResult().get(0);
			ApplicationClientInfo aci = genAci(mt);
			if (aci == null) {
				result.setSuccess(Boolean.TRUE);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult checkDownload(HttpServletRequest request, HttpServletResponse response) {
		JsonResult result = new JsonResult();
		result.setSuccess(Boolean.FALSE);
		try {
			String aid = ServletRequestUtils.getStringParameter(request, "aid");
			String cardNo = ServletRequestUtils.getStringParameter(request, "cardNo");
			String os = ServletRequestUtils.getStringParameter(request, "os");
			String sysRequirment = ServletRequestUtils.getStringParameter(request, "sysRequirment");
			CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, aid);
			ApplicationVersion appVer = cardApp.getApplicationVersion();
			ApplicationClientInfo aci = applicationClientInfoManager.getByApplicationVersionSysTypeSysRequirementFileType(appVer, os,
					sysRequirment, ApplicationClientInfo.FILE_TYPE_JAD);
			if (aci != null) {
				result.setSuccess(Boolean.TRUE);
				result.setMessage("/" + aci.getFileUrl());
			} else {
				result.setSuccess(Boolean.FALSE);
			}
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	private ApplicationClientInfo genAci(MobileType mt) {
		String sysType = "";
		String sysRequirment = "";
		if (null != mt.getJ2meKey()) {
			if (null != mt.getOriginalOsKey()) {
				sysType = "os";
				sysRequirment = mt.getOriginalOsKey();
			} else {
				sysType = "j2me";
				sysRequirment = mt.getJ2meKey();
			}
		} else {
			if (null == mt.getOriginalOsKey()) {
				sysType = "undefine";
				sysRequirment = "undefine";
			} else {
				sysType = "os";
				sysRequirment = mt.getOriginalOsKey();
			}
		}
		List<ApplicationClientInfo> infos = applicationClientInfoManager.getAppManagerByTypeAndVersion(sysType, sysRequirment);
		if (!CollectionUtils.isEmpty(infos)) {
			return infos.get(0);
		} else {
			return null;
		}
	}

	private void setHeader(HttpServletRequest request, HttpServletResponse response, ApplicationClientInfo aci) throws Exception {
		response.setContentType("application/jar;charset=UTF-8");
		String fileName = aci.getFileUrl().substring(aci.getFileUrl().lastIndexOf('/') + 1);
		fileName = MimeUtils.encodeFileName(request, fileName);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	}
}

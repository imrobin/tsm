package com.justinmobile.tsm.application.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;

@Controller("loadFileVersionController")
@RequestMapping("/loadFileVersion/")
public class LoadFileVersionController {

	private static final Logger log = LoggerFactory.getLogger(LoadFileVersionController.class);

	@Autowired
	private LoadFileVersionManager loadFileVersionManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@RequestMapping
	public @ResponseBody
	JsonResult getByApplicationVersionThatTypeCms2acFile(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileVersionController.getByApplicationVersion" + "\n");
		JsonResult result = new JsonResult();
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<LoadFileVersion> loadFileVersions = loadFileVersionManager.getWhichImportedByApplicationVersionAndType(applicationVersion,
					LoadFile.TYPE_CMS2AC_FILE);

			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : loadFileVersions) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("shareFlag", loadFileVersion.getLoadFile().getShareFlag());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());
				map.put("shareFlag", loadFileVersion.getLoadFile().getShareFlag());

				mappedList.add(map);
			}
			result.setResult(mappedList);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getByApplicationVersion(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileVersionController.getByApplicationVersion" + "\n");
		JsonResult result = new JsonResult();
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<LoadFileVersion> loadFileVersions = loadFileVersionManager.getWhichImportedByApplicationVersion(applicationVersion);

			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : loadFileVersions) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("shareFlag", loadFileVersion.getLoadFile().getShareFlag());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());
				map.put("shareFlag", loadFileVersion.getLoadFile().getShareFlag());

				mappedList.add(map);
			}
			result.setResult(mappedList);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	@Deprecated
	public @ResponseBody
	JsonResult getBySharedByApplicationVersion(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileVersionController.getByApplicationVersion" + "\n");
		JsonResult result = new JsonResult();
		try {
			List<LoadFileVersion> loadFileVersions = loadFileVersionManager.getWhichImportedByApplicationVersion(applicationVersionId,
					LoadFile.FLAG_SHARED);

			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : loadFileVersions) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());

				mappedList.add(map);
			}
			result.setResult(mappedList);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	@Deprecated
	public @ResponseBody
	JsonResult getByExclusiveByApplicationVersion(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileVersionController.getByApplicationVersion" + "\n");
		JsonResult result = new JsonResult();
		try {
			List<LoadFileVersion> loadFileVersions = loadFileVersionManager.getWhichImportedByApplicationVersion(applicationVersionId,
					LoadFile.FLAG_EXCLUSIVE);

			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : loadFileVersions) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());

				mappedList.add(map);
			}
			result.setResult(mappedList);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getByCriteria(HttpServletRequest request) {
		log.debug("\n" + "LoadFileVersion.getByCriteria" + "\n");
		if (log.isDebugEnabled()) {
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
		}

		JsonResult result = new JsonResult();

		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadFileVersion> page = SpringMVCUtils.getPage(request);

			page = loadFileVersionManager.findPage(page, filters);

			result.setPage(page, "capFileHex", "");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage createNewLoadFileVersion(HttpServletRequest request) {
		log.debug("\n" + "LoadFileVersion.createNewLoadFileVersion" + "\n");
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
			LoadFileVersion loadFileVersion = new LoadFileVersion();
			BindingResult result = SpringMVCUtils.bindObject(request, loadFileVersion);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tempDir", tempDir);
				params.put("tempFileAbsPath", request.getParameter("tempFileAbsPath"));
				params.put("loadFileId", request.getParameter("loadFileId"));
				params.put("applicationVersionId", request.getParameter("applicationVersionId"));

				String username = SpringSecurityUtils.getCurrentUserName();

				loadFileVersionManager.createNewLoadFileVersionForApplicaitonVersion(loadFileVersion, params, username);

				Map<String, Object> mappedLoadFileVersion = new HashMap<String, Object>();
				mappedLoadFileVersion.put("id", loadFileVersion.getId());
				mappedLoadFileVersion.put("name", loadFileVersion.getLoadFile().getName());
				mappedLoadFileVersion.put("aid", loadFileVersion.getLoadFile().getAid());
				mappedLoadFileVersion.put("comments", loadFileVersion.getLoadFile().getComments());
				mappedLoadFileVersion.put("versionNo", loadFileVersion.getVersionNo());
				mappedLoadFileVersion.put("loadParams", loadFileVersion.getLoadParams());
				mappedLoadFileVersion.put("fileSize", loadFileVersion.getFileSize());

				message.setMessage(mappedLoadFileVersion);

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
	JsonMessage createNewSharedLoadFileVersion(HttpServletRequest request) {
		log.debug("\n" + "LoadFileVersion.createNewLoadFileVersion" + "\n");
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
			LoadFileVersion loadFileVersion = new LoadFileVersion();
			BindingResult result = SpringMVCUtils.bindObject(request, loadFileVersion);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tempDir", tempDir);
				params.put("tempFileAbsPath", request.getParameter("tempFileAbsPath"));
				params.put("loadFileId", request.getParameter("loadFileId"));
				params.put("applicationVersionId", request.getParameter("applicationVersionId"));

				String username = SpringSecurityUtils.getCurrentUserName();

				loadFileVersionManager.createNewLoadFileVersion(loadFileVersion, params, username);

				message.setMessage(loadFileVersion.toMap("capFileHex", "loadFile.id"));

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
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadFileVersion> page = SpringMVCUtils.getPage(request);

			page = loadFileVersionManager.findPage(page, filters);
			result.setPage(page, "capFileHex", "loadFile.id loadFile.name");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getDependence(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadFileVersion> page = SpringMVCUtils.getPage(request);

			page = loadFileVersionManager.findPage(page, filters);

			List<Map<String, Object>> mappedResult = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : page.getResult()) {
				Map<String, Object> mappedEntity = convert2MapForDependence(loadFileVersion);
				mappedResult.add(mappedEntity);
			}
			result.setResult(mappedResult);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage addDependence(@RequestParam Long parentLoadFileVersionId, @RequestParam Long childLoadFileVersionId) {
		log.debug("\n" + "LoadFileVersionController.addDependence" + "\n");
		log.debug("\n" + "parentLoadFileVersionId: " + parentLoadFileVersionId + "\n");
		log.debug("\n" + "childLoadFileVersionId: " + childLoadFileVersionId + "\n");
		JsonMessage message = new JsonMessage();

		try {
			LoadFileVersion parent = loadFileVersionManager.load(parentLoadFileVersionId);
			LoadFileVersion child = loadFileVersionManager.load(childLoadFileVersionId);

			String username = SpringSecurityUtils.getCurrentUserName();
			loadFileVersionManager.addDependence(parent, child, username);

			Map<String, Object> mapped = convert2MapForDependence(parent);

			message.setMessage(mapped);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage removeDependence(@RequestParam Long parentLoadFileVersionId, @RequestParam Long childLoadFileVersionId) {
		log.debug("\n" + "LoadFileVersionController.removeDependence" + "\n");
		log.debug("\n" + "parentLoadFileVersionId: " + parentLoadFileVersionId + "\n");
		log.debug("\n" + "childLoadFileVersionId: " + childLoadFileVersionId + "\n");
		JsonMessage message = new JsonMessage();

		try {
			LoadFileVersion parent = loadFileVersionManager.load(parentLoadFileVersionId);
			LoadFileVersion child = loadFileVersionManager.load(childLoadFileVersionId);

			String username = SpringSecurityUtils.getCurrentUserName();
			loadFileVersionManager.removeDependence(parent, child, username);

			Map<String, Object> mapped = convert2MapForDependence(parent);

			message.setMessage(mapped);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	private Map<String, Object> convert2MapForDependence(LoadFileVersion loadFileVersion) {
		Map<String, Object> mapped = new HashMap<String, Object>();

		mapped.put("id", loadFileVersion.getId());
		mapped.put("name", loadFileVersion.getLoadFile().getName());
		mapped.put("aid", loadFileVersion.getLoadFile().getAid());
		mapped.put("versionNo", loadFileVersion.getVersionNo());
		mapped.put("loadFile_id", loadFileVersion.getLoadFile().getId());

		return mapped;
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult findUnLinkPage(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<LoadFileVersion> page = SpringMVCUtils.getPage(request);
			String cardBaseId = request.getParameter("cardBaseId");
			page = loadFileVersionManager.findUnLinkPage(page, cardBaseId);
			result.setPage(page, "capFileHex", "loadFile.name");
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getThatUsedCms2acFileByApplicationVersion(@RequestParam Long applicationVersionId, @RequestParam int fileType) {
		log.debug("\n" + "LoadFileVersionController.getByApplicationVersion" + "\n");
		JsonResult result = new JsonResult();
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<LoadFileVersion> loadFileVersions = loadFileVersionManager.getWhichImportedByApplicationVersionAndType(applicationVersion,
					fileType);

			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
			for (LoadFileVersion loadFileVersion : loadFileVersions) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());

				mappedList.add(map);
			}
			result.setResult(mappedList);
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}
}

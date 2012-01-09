package com.justinmobile.tsm.application.web;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadParams;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;
import com.justinmobile.tsm.commons.web.CommonsController;

@Controller("loadFileController")
@RequestMapping("/loadFile/")
public class LoadFileController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private CommonsController commonsController;

	@Autowired
	private LoadFileManager loadFileManager;

	@Autowired
	private LoadFileVersionManager loadFileVersionManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@RequestMapping
	public @ResponseBody
	JsonMessage createNewLoadFileForApplicationVersion(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("\n" + "createNewLoadFileForApplicationVersion" + "\n");
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
			String username = SpringSecurityUtils.getCurrentUserName();

			LoadFile loadFile = new LoadFile();
			BindingResult loadFileBindingResult = SpringMVCUtils.bindObject(request, loadFile);

			LoadFileVersion loadFileVersion = new LoadFileVersion();
			BindingResult loadFileVersionBindingResult = SpringMVCUtils.bindObject(request, loadFileVersion);

			if (loadFileBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileBindingResult);
			} else if (loadFileVersionBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileVersionBindingResult);
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tempDir", tempDir);
				params.put("tempFileAbsPath", request.getParameter("tempFileAbsPath"));
				params.put("applicationVersionId", request.getParameter("applicationVersionId"));
				params.put("sdId", request.getParameter("sdId"));

				loadFileManager.createNewLoadFileForApplicationVersion(loadFile, loadFileVersion, params, username);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());

				message.setMessage(map);

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
	JsonMessage createNewSharedLoadFile(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("\n" + "LoadFileController.createNewSharedLoadFile" + "\n");
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
			String username = SpringSecurityUtils.getCurrentUserName();

			LoadFile loadFile = new LoadFile();
			BindingResult loadFileBindingResult = SpringMVCUtils.bindObject(request, loadFile);

			LoadFileVersion loadFileVersion = new LoadFileVersion();
			BindingResult loadFileVersionBindingResult = SpringMVCUtils.bindObject(request, loadFileVersion);

			if (loadFileBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileBindingResult);
			} else if (loadFileVersionBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileVersionBindingResult);
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tempDir", tempDir);
				params.put("tempFileAbsPath", request.getParameter("tempFileAbsPath"));
				params.put("applicationVersionId", request.getParameter("applicationVersionId"));
				params.put("sdId", request.getParameter("sdId"));

				loadFileManager.createNewSharedLoadFile(loadFile, loadFileVersion, params, username);

				message.setMessage(loadFile, "", "sd.aid sd.sdName");

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
	JsonMessage createNewLoadFile(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("\n" + "createNewLoadFileForApplicationVersion" + "\n");
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
			String username = SpringSecurityUtils.getCurrentUserName();

			LoadFile loadFile = new LoadFile();
			BindingResult loadFileBindingResult = SpringMVCUtils.bindObject(request, loadFile);

			LoadFileVersion loadFileVersion = new LoadFileVersion();
			BindingResult loadFileVersionBindingResult = SpringMVCUtils.bindObject(request, loadFileVersion);

			if (loadFileBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileBindingResult);
			} else if (loadFileVersionBindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(loadFileVersionBindingResult);
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tempDir", tempDir);
				params.put("tempFileAbsPath", request.getParameter("tempFileAbsPath"));
				params.put("applicationVersionId", request.getParameter("applicationVersionId"));

				loadFileManager.createNewSharedLoadFile(loadFile, loadFileVersion, params, username);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", loadFileVersion.getId());
				map.put("name", loadFileVersion.getLoadFile().getName());
				map.put("aid", loadFileVersion.getLoadFile().getAid());
				map.put("comments", loadFileVersion.getLoadFile().getComments());
				map.put("versionNo", loadFileVersion.getVersionNo());
				map.put("loadParams", loadFileVersion.getLoadParams());
				map.put("fileSize", loadFileVersion.getFileSize());

				message.setMessage(map);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} finally {
			// 删除存储临时文件的目录，此功能属于后台功能，即使有异常也不能通知前台
			try {
				log.debug(tempDir);
				FileUtils.deleteDirectory(new File(tempDir));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage buildLoadParams(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("\n" + "LoadFileController.buildLoadParams" + "\n");
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
		}

		JsonMessage message = new JsonMessage();
		try {
			LoadParams params = new LoadParams();

			BindingResult result = SpringMVCUtils.bindObject(request, params);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String hexParams = params.build();

				log.debug("\n" + "hexParams: " + hexParams + "\n");

				Map<String, Object> messages = new HashMap<String, Object>(2);
				messages.put("hexLoadParams", hexParams);
				messages.put("jsonLoadParams", params);

				message.setMessage(messages);
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
	JsonMessage parseLoadParams(@RequestParam String hexLoadParams) {
		log.debug("\n" + "LoadFileController.parseLoadParams" + "\n");
		log.debug("\n" + "hexLoadParams: " + hexLoadParams + "\n");
		JsonMessage message = new JsonMessage();

		try {
			LoadParams loadParams = LoadParams.parse(hexLoadParams);
			message.setMessage(loadParams);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getExclusiveLoadFiles(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileController.getExclusiveLoadFiles" + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");

		JsonResult result = new JsonResult();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			List<LoadFile> loadFiles = loadFileManager.getExclusiveLoadFilesBySpAndApplicationVersion(applicationVersionId, username);
			result.setResult(loadFiles, "", "");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getUnusedLoadFiles(@RequestParam Long applicationVersionId, @RequestParam int fileType) {
		log.debug("\n" + "LoadFileController.getUnusedLoadFiles" + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");

		JsonResult result = new JsonResult();

		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<LoadFile> loadFiles = loadFileManager.getUnusedByApplicationVersionAndType(applicationVersion, fileType);
			result.setResult(loadFiles, "", "");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult getSharedLoadFiles(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "LoadFileController.getExclusiveLoadFiles" + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");

		JsonResult result = new JsonResult();

		try {
			List<LoadFile> loadFiles = loadFileManager.getSharedLoadFilesWhichUnassociateWithApplicationVersion(applicationVersionId);
			result.setResult(loadFiles, "", "");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<LoadFile> page = SpringMVCUtils.getPage(request);

			page = loadFileManager.findPage(page, filters);
			result.setPage(page, null, "sd.sdName sd.aid");
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
	JsonResult loadByIds(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<LoadFile> page = SpringMVCUtils.getPage(request);
			String loadFileIds = ServletRequestUtils.getStringParameter(request, "ids", "");
			if (loadFileIds.equals("")) {
				result.setSuccess(Boolean.FALSE);
				return result;
			}
			page = loadFileManager.loadByIds(page, loadFileIds);
			result.setPage(page, null, "sd.id sd.sdName");
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
	JsonResult getUndependentLoadFiles(@RequestParam Long loadFileVersionId) {
		log.debug("\n" + "LoadFileController.getUndependentLoadFiles" + "\n");
		log.debug("\n" + "loadFileVersionId: " + loadFileVersionId + "\n");
		JsonResult result = new JsonResult();
		try {
			LoadFileVersion loadFileVersion = loadFileVersionManager.load(loadFileVersionId);
			List<LoadFile> loadFiles = loadFileManager.getUndependentLoadFiles(loadFileVersion);

			result.setResult(loadFiles, "", "");
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
	JsonMessage exportConstant() {
		JsonMessage message = new JsonMessage();

		try {
			HashMap<String, Object> contants = new HashMap<String, Object>();

			contants.put("FLAG_SHARED", LoadFile.FLAG_SHARED);
			contants.put("FLAG_EXCLUSIVE", LoadFile.FLAG_EXCLUSIVE);

			message.setMessage(contants);
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
	JsonMessage validateAid(@RequestParam String aid) {
		JsonMessage message = new JsonMessage();

		try {
			loadFileManager.validateAid(aid.toUpperCase());
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
	JsonMessage remove(@RequestParam long loadFileId) {
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			LoadFile loadFile = loadFileManager.load(loadFileId);
			loadFileManager.remove(loadFile, username);
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
	public void uploadCap(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile file) {
		JsonMessage message = new JsonMessage();
		try {
			message = commonsController.upload(request, file);

			@SuppressWarnings("unchecked")
			String filePath = (String) ((Map<String, Object>) message.getMessage()).get("tempFileAbsPath");

			if (!StringUtils.endsWithIgnoreCase(filePath, ".cap")) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_NOT_CAP);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		commonsController.convertToJson(response, message);
	}
}

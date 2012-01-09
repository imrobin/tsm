package com.justinmobile.tsm.application.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;

@Controller("applicationLoadFileController")
@RequestMapping("/applicationLoadFile/")
public class ApplicationLoadFileController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@RequestMapping
	public @ResponseBody
	JsonMessage setDownloadOrder(@RequestParam Long loadFileVersionId, @RequestParam Long applicationVersionId, @RequestParam Integer order) {
		log.debug("\n" + "ApplicationLoadFileController.setDownloadOrder" + "\n");
		log.debug("\n" + "loadFileVersionId: " + loadFileVersionId + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");
		log.debug("\n" + "order: " + order + "\n");
		JsonMessage message = new JsonMessage();
		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			applicationLoadFileManager.setDownloadOrder(loadFileVersionId, applicationVersionId, order, username);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	@Deprecated
	public @ResponseBody
	JsonResult getDownloadOrder(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.getDownloadOrder" + "\n");
		JsonResult result = new JsonResult();
		try {
			List<ApplicationLoadFile> applicationLoadFiles = applicationLoadFileManager.getExclusiveByDownloadOrder(applicationVersionId);
			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>(applicationLoadFiles.size());
			for (int i = 0; i < applicationLoadFiles.size(); i++) {
				ApplicationLoadFile entity = applicationLoadFiles.get(i);

				log.debug("\n" + "ApplicationLoadFile.id: " + entity.getId() + "\n");

				Map<String, Object> mappedEntity = new HashMap<String, Object>();
				mappedEntity.put("loadFileVersionId", entity.getLoadFileVersion().getId());
				mappedEntity.put("name", entity.getLoadFileVersion().getLoadFile().getName());
				mappedEntity.put("aid", entity.getLoadFileVersion().getLoadFile().getAid());
				mappedEntity.put("order", entity.getDeleteOrder());
				mappedList.add(mappedEntity);
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
	JsonResult getAllByDownloadOrder(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.getDownloadOrder" + "\n");
		JsonResult result = new JsonResult();
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<ApplicationLoadFile> applicationLoadFiles = applicationLoadFileManager.getAllByDownloadOrder(applicationVersion);
			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>(applicationLoadFiles.size());
			for (int i = 0; i < applicationLoadFiles.size(); i++) {
				ApplicationLoadFile entity = applicationLoadFiles.get(i);

				log.debug("\n" + "ApplicationLoadFile.id: " + entity.getId() + "\n");

				Map<String, Object> mappedEntity = new HashMap<String, Object>();
				mappedEntity.put("loadFileVersionId", entity.getLoadFileVersion().getId());
				mappedEntity.put("name", entity.getLoadFileVersion().getLoadFile().getName());
				mappedEntity.put("aid", entity.getLoadFileVersion().getLoadFile().getAid());
				mappedEntity.put("order", entity.getDeleteOrder());
				mappedList.add(mappedEntity);
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
	JsonResult getDeleteOrder(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.getDeleteOrder" + "\n");
		JsonResult result = new JsonResult();
		try {
			List<ApplicationLoadFile> applicationLoadFiles = applicationLoadFileManager.getExclusiveByDeleteOrder(applicationVersionId);
			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>(applicationLoadFiles.size());
			for (int i = 0; i < applicationLoadFiles.size(); i++) {
				ApplicationLoadFile entity = applicationLoadFiles.get(i);

				log.debug("\n" + "ApplicationLoadFile.id: " + entity.getId() + "\n");

				Map<String, Object> mappedEntity = new HashMap<String, Object>();
				mappedEntity.put("loadFileVersionId", entity.getLoadFileVersion().getId());
				mappedEntity.put("name", entity.getLoadFileVersion().getLoadFile().getName());
				mappedEntity.put("aid", entity.getLoadFileVersion().getLoadFile().getAid());
				mappedEntity.put("order", entity.getDeleteOrder());
				mappedList.add(mappedEntity);
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
	JsonResult getAllByDeleteOrder(@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.getDeleteOrder" + "\n");
		JsonResult result = new JsonResult();
		try {
			ApplicationVersion applicationVersion = applicationVersionManager.load(applicationVersionId);
			List<ApplicationLoadFile> applicationLoadFiles = applicationLoadFileManager.getAllByDeleteOrder(applicationVersion);
			List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>(applicationLoadFiles.size());
			for (int i = 0; i < applicationLoadFiles.size(); i++) {
				ApplicationLoadFile entity = applicationLoadFiles.get(i);

				log.debug("\n" + "ApplicationLoadFile.id: " + entity.getId() + "\n");

				Map<String, Object> mappedEntity = new HashMap<String, Object>();
				mappedEntity.put("loadFileVersionId", entity.getLoadFileVersion().getId());
				mappedEntity.put("name", entity.getLoadFileVersion().getLoadFile().getName());
				mappedEntity.put("aid", entity.getLoadFileVersion().getLoadFile().getAid());
				mappedEntity.put("order", entity.getDeleteOrder());
				mappedList.add(mappedEntity);
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
	JsonMessage setDeleteOrder(@RequestParam Long loadFileVersionId, @RequestParam Long applicationVersionId, @RequestParam Integer order) {
		log.debug("\n" + "ApplicationLoadFileController.setDeleteOrder" + "\n");
		log.debug("\n" + "loadFileVersionId: " + loadFileVersionId + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");
		log.debug("\n" + "order: " + order + "\n");
		JsonMessage message = new JsonMessage();
		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			applicationLoadFileManager.setDeleteOrder(loadFileVersionId, applicationVersionId, order, username);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage removeImportBetweenLoadFileVersionAndApplicationVersion(@RequestParam Long loadFileVersionId,
			@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.removeImportBetweenLoadFileVersionAndApplicationVersion" + "\n");
		log.debug("\n" + "LoadFileVersionId: " + loadFileVersionId + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();
			applicationLoadFileManager.removeImportBetweenLoadFileVersionAndApplicationVersion(loadFileVersionId, applicationVersionId,
					username);

			message.setMessage(loadFileVersionId);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage buildImportBetweenLoadFileVersionAndApplicationVersion(@RequestParam Long loadFileVersionId,
			@RequestParam Long applicationVersionId) {
		log.debug("\n" + "ApplicationLoadFileController.buildImportBetweenLoadFileVersionAndApplicationVersion" + "\n");
		log.debug("\n" + "LoadFileVersionId: " + loadFileVersionId + "\n");
		log.debug("\n" + "applicationVersionId: " + applicationVersionId + "\n");
		JsonMessage message = new JsonMessage();

		try {
			String username = SpringSecurityUtils.getCurrentUserName();

			ApplicationLoadFile applicationLoadFile = applicationLoadFileManager.buildImportBetweenLoadFileVersionAndApplicationVersion(
					loadFileVersionId, applicationVersionId, username);

			LoadFileVersion loadFileVersion = applicationLoadFile.getLoadFileVersion();
			Map<String, Object> mappedLoadFileVersion = new HashMap<String, Object>();
			mappedLoadFileVersion.put("id", loadFileVersion.getId());
			mappedLoadFileVersion.put("name", loadFileVersion.getLoadFile().getName());
			mappedLoadFileVersion.put("aid", loadFileVersion.getLoadFile().getAid());
			mappedLoadFileVersion.put("comments", loadFileVersion.getLoadFile().getComments());
			mappedLoadFileVersion.put("versionNo", loadFileVersion.getVersionNo());
			mappedLoadFileVersion.put("loadParams", loadFileVersion.getLoadParams());
			mappedLoadFileVersion.put("fileSize", loadFileVersion.getFileSize());

			message.setMessage(mappedLoadFileVersion);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}
}

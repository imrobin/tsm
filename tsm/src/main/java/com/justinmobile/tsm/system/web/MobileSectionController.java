package com.justinmobile.tsm.system.web;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.encode.EncodeUtils;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.FileUploadUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.system.domain.MobileSection;
import com.justinmobile.tsm.system.manager.MobileSectionManager;

@Controller
@RequestMapping("/mobileSection/")
public class MobileSectionController {
	
	@Autowired
	private MobileSectionManager mobileSectionManager;

	@RequestMapping
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<MobileSection> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page = mobileSectionManager.findPage(page, filters);
			result.setPage(page, null, null);
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
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			MobileSection mobileSection = new MobileSection();
			BindingResult  result = SpringMVCUtils.bindObject(request, mobileSection);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				MobileSection ms = mobileSectionManager.getByParagraph(mobileSection.getParagraph());
				if (ms == null) {
					mobileSectionManager.saveOrUpdate(mobileSection);
				} else {
					throw new PlatformException(PlatformErrorCode.MOBILE_SECTION_REPEAT);
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
	public @ResponseBody JsonMessage remove(@RequestParam String msId) {
		JsonMessage message = new JsonMessage();
		try {
			mobileSectionManager.remove(msId.split(","));
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
	public void importFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("Filedata") MultipartFile file) {
		JsonMessage message = new JsonMessage();
		try {
			String servletPath = request.getSession().getServletContext().getRealPath("/");
			String path = FileUploadUtils.write(file, servletPath,  "mobilesection" + File.separator);
			String fileRealPath = servletPath + path;
			List<String> errors = mobileSectionManager.importExcelFile(fileRealPath);
			if (CollectionUtils.isNotEmpty(errors)) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage(errors);
			}
			File deFile = new File(fileRealPath);
			if (!deFile.delete()) {
				LoggerFactory.getLogger(this.getClass()).warn("File is not delete, please delete by hand!");
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
		String jsonString = JsonBinder.buildNormalBinder().toJson(message);
		ServletUtils.sendMessage(response, EncodeUtils.DEFAULT_URL_ENCODING, ServletUtils.HTML_TYPE, jsonString);
	}
}

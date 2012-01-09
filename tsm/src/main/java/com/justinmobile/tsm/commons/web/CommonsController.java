package com.justinmobile.tsm.commons.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.encode.EncodeUtils;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.FileUploadUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.Privilege;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.SecurityDomainApplyManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.utils.FileUtils;

@Controller("commonsController")
@RequestMapping("/commons/")
public class CommonsController {

	private static final Logger log = LoggerFactory.getLogger(CommonsController.class);

	@Autowired
	private SecurityDomainApplyManager securityDomainApplyManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private AppletManager appletManager;

	@Autowired
	private LoadFileManager loadFileManager;

	/**
	 * 上传文件，保存在临时文件夹
	 * 
	 * @param request
	 * @param file
	 *            上传的文件
	 * @return JsonMessage对象<br/>
	 *         如果上传成功<br/>
	 *         message.tempDir 表示临时目录在服务器的绝对路径，用于后续请求删除临时目录<br/>
	 *         message.tempFileRalPath
	 *         表示临时文件在服务器的相对路径路径，用于前台使用临时文件，不包括ServletContext<br/>
	 *         message.tempFileAbsPath 表示临时文件在服务器的相对路径路径，用于后续请求处理临时文件
	 *         message.fileName 表示临时文件在本地的文件名<br/>
	 *         message.fileSize 表示上传文件的大小，单位为byte<br/>
	 * 
	 */
	@RequestMapping
	public void upload(HttpServletRequest request, HttpServletResponse response, @RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = upload(request, file);

		convertToJson(response, result);
	}

	public void convertToJson(HttpServletResponse response, JsonMessage result) {
		String jsonString = JsonBinder.buildNormalBinder().toJson(result);
		if (log.isDebugEnabled()) {
			log.debug("\njsonString: " + jsonString + "\n");
		}

		ServletUtils.sendMessage(response, EncodeUtils.DEFAULT_URL_ENCODING, ServletUtils.HTML_TYPE, jsonString);
	}

	public JsonMessage upload(HttpServletRequest request, MultipartFile file) {
		log.debug("\nfileName: " + file.getName() + "\n");
		try {
			log.debug("\nfileSize: " + file.getBytes().length + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JsonMessage result = new JsonMessage();
		try {
			String servletPath = request.getSession().getServletContext().getRealPath("/");
			String tempDirRalPath = FileUtils.getTempDir();// 临时目录相对路径
			String tempDirAbsPath = servletPath + tempDirRalPath;// 临时目录绝对路径

			String tempFileRalPath = FileUploadUtils.write(file, servletPath, tempDirRalPath);// 临时文件相对路径
			String tempFileAbsPath = servletPath + tempFileRalPath;// 临时文件绝对路径
			tempFileRalPath = StringUtils.replace(tempFileRalPath, File.separator, "/");// 将临时文件相对路径分隔符转换为URL分隔符

			if (log.isDebugEnabled()) {
				log.debug("\nfileName: " + file.getOriginalFilename() + "\n");
				log.debug("\nservletPath: " + servletPath + "\n");
				log.debug("\ntempDir: " + tempDirAbsPath + "\n");
				log.debug("\ntempFilePath: " + tempFileRalPath + "\n");
				log.debug("\ntempFileAbsPath: " + tempFileAbsPath + "\n");
				log.debug("\nfileSize: " + file.getSize() + "\n");
			}

			Map<String, Object> message = new HashMap<String, Object>(2);
			message.put("fileName", file.getOriginalFilename());
			message.put("tempDir", tempDirAbsPath);
			message.put("tempRalFilePath", tempFileRalPath);
			message.put("tempFileAbsPath", tempFileAbsPath);
			message.put("fileSize", file.getSize());
			result.setMessage(message);
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 组建实例或安全域的权限
	 * 
	 * @param request
	 * @return JsonMessage对象<br/>
	 *         hexPrivilege 十六进制字符串形式的权限<br/>
	 *         jsonPrivilege json形式的权限，权限名与Privilege的字段名相同
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage buildPrivilege(HttpServletRequest request) {
		log.debug("\n" + "CommonsController.buildPrivilege" + "\n");
		if (log.isDebugEnabled()) {
			log.debug("\n" + "参数——开始" + "\n");
			@SuppressWarnings("unchecked")
			Enumeration<String> names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				log.debug("\n" + name + ": " + request.getParameter(name) + "\n");
			}
			log.debug("\n" + "参数——结束" + "\n");
		}

		JsonMessage message = new JsonMessage();
		try {
			Privilege privilege = new Privilege();
			BindingResult result = SpringMVCUtils.bindObject(request, privilege);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String hexPrivilege = ConvertUtils.int2HexString(privilege.biuld(), 2);
				log.debug("\n" + "hexPrivilege: " + hexPrivilege + "\n");

				Map<String, Object> messages = new HashMap<String, Object>(2);
				messages.put("hexPrivilege", hexPrivilege);
				messages.put("jsonPrivilege", privilege);

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
	JsonMessage parsePrivilege(@RequestParam String hexPrivilege) {
		log.debug("\n" + "CommonsController.parsePrivilege" + "\n");
		log.debug("\n" + "intPrivilege: " + hexPrivilege + "\n");
		JsonMessage message = new JsonMessage();
		try {
			Privilege privilege = Privilege.parse(ConvertUtils.hexString2Int(hexPrivilege));
			message.setMessage(privilege);
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

	/**
	 * 验证AID唯一性
	 * 
	 * @param newAid
	 * @param orgAid
	 * @return 可以返回true， 不可用返回false
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage validateAID(@RequestParam String newAid, @RequestParam String orgAid) {
		JsonMessage message = new JsonMessage();
		final String propertyName = "aid";
		try {
			if (StringUtils.isBlank(orgAid)) {
				orgAid = null;
			} else if (orgAid.equalsIgnoreCase("null")) {
				orgAid = null;
			}

			boolean sdApplyAid = this.securityDomainApplyManager.isPropertyUnique(propertyName, newAid, orgAid);
			boolean sdAid = this.securityDomainManager.isPropertyUnique(propertyName, newAid, orgAid);
			boolean appletAid = this.appletManager.isPropertyUnique(propertyName, newAid, orgAid);
			boolean loadFileAid = this.loadFileManager.isPropertyUnique(propertyName, newAid, orgAid);

			boolean bln = sdApplyAid && sdAid && appletAid && loadFileAid;
			message.setMessage(bln);

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
	JsonMessage exportEnum(@RequestParam String enumName, String exportMethodName) {
		JsonMessage message = new JsonMessage();
		try {
			Class<?> c = Class.forName(enumName);
			Method m = c.getMethod(exportMethodName, new Class[] {});
			Object result = m.invoke(c);

			message.setMessage(result);
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
}

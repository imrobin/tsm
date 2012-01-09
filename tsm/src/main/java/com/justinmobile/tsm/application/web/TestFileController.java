package com.justinmobile.tsm.application.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.FileUploadUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.TestFile;
import com.justinmobile.tsm.application.manager.TestFileManager;

@Controller("testFileController")
@RequestMapping("/testfile/")
public class TestFileController {

	@Autowired
	TestFileManager testFileManager;

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<TestFile> page = SpringMVCUtils.getPage(request);
			page = testFileManager.findPage(page, filters);
			result.setPage(page, null, "sp.name");
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
	JsonResult getTestFileList(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<TestFile> page = new Page<TestFile>();
			page = testFileManager.getTestFileList(page);
			result.setPage(page, null, "sp.name");
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
	public void upload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("Filedata") MultipartFile file) {
		JsonMessage result = new JsonMessage();
		try {
			if (file.getBytes().length > 5000000) {
				result.setSuccess(Boolean.FALSE);
				result.setMessage("上传的文件必须小于5M");
			} else {
				String servletPath = request.getSession().getServletContext().getRealPath("/");
				String tempDir = "temp" + File.separator;// 临时目录相对路径
				File tempDirF = new File(tempDir);
				if (!tempDirF.exists()) {
					try {
						tempDirF.createNewFile();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				String fileName = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmssSSS");
				String tempFileRalPath = FileUploadUtils.write(file, servletPath, tempDir, fileName);// 临时文件相对路径
				tempFileRalPath = StringUtils.replace(tempFileRalPath, File.separator, "/");// 将临时文件相对路径分隔符转换为URL分隔符
				Map<String, Object> message = new HashMap<String, Object>(2);
				message.put("tempRalFilePath", "/" + tempFileRalPath);
				message.put("filename", tempFileRalPath.substring(tempFileRalPath.indexOf("/") + 1));
				message.put("oldFileName", file.getOriginalFilename());
				result.setMessage(message);
			}
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception ex) {
			result.setMessage(Boolean.FALSE);
			result.setMessage(ex.getMessage());
		}
		String jsonString = JsonBinder.buildNormalBinder().toJson(result);
		ServletUtils.sendMessage(response, jsonString);
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage finishUpload(HttpServletRequest request,HttpServletResponse response) {
		JsonMessage result = new JsonMessage();
		try {
			String appVerId = request.getParameter("appverId");
			String fileComment = request.getParameter("comment");
			String tempFileName = request.getParameter("tempFileName");
			String orgFileName = request.getParameter("testFileOrgName");
			String servletPath = request.getSession().getServletContext().getRealPath("/");
			testFileManager.finishUpload(appVerId,orgFileName,fileComment,tempFileName,servletPath);
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception ex) {
			result.setMessage(Boolean.FALSE);
			result.setMessage(ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage delTestFile(HttpServletRequest request,@RequestParam Long tfId) {
		JsonMessage result = new JsonMessage();
		try {
			String servletPath = request.getSession().getServletContext().getRealPath("/");
			testFileManager.delTestFile(tfId,servletPath);
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception ex) {
			result.setMessage(Boolean.FALSE);
			result.setMessage(ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage getTestFileInfo(HttpServletRequest request,@RequestParam Long tfId) {
		JsonMessage result = new JsonMessage();
		try {
			TestFile tf = testFileManager.load(tfId);
			result.setMessage(tf.toMap(null, null));
		} catch (PlatformException e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception ex) {
			result.setMessage(Boolean.FALSE);
			result.setMessage(ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public void downFile(HttpServletRequest request,HttpServletResponse response,@RequestParam Long tfId) {
			    BufferedOutputStream bos = null;   
			    FileInputStream fis = null;   
			    String servletPath = request.getSession().getServletContext().getRealPath("/");
			    TestFile tf = testFileManager.load(tfId);
			    String path = servletPath + tf.getFilePath();
			    String fileName = tf.getOriginalName();
			    if (fileName != null && !"".equals(fileName)) {   
			        try {   
			        	ServletUtils.setFileDownloadHeader(response, fileName);
			            fis = new FileInputStream(path + tf.getFileName());   
			            bos = new BufferedOutputStream(response.getOutputStream());   
			            byte[] buffer = new byte[2048];   
			            while(fis.read(buffer) != -1){   
			                bos.write(buffer);   
			            }   
			        } catch (IOException e) {   
			            e.printStackTrace();   
			        }finally {   
			            if(fis != null){try {fis.close();} catch (IOException e) {}}   
			            if(bos != null){try {bos.close();} catch (IOException e) {}}   
			        }   
			    }
	}
}

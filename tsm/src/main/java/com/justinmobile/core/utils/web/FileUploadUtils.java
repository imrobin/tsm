package com.justinmobile.core.utils.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.springframework.web.multipart.MultipartFile;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.CalendarUtils;

/**
 * 上传
 * 
 * @author peak
 * 
 */
public class FileUploadUtils {

	public static String write(MultipartFile file, String saveRealPath) {
		return write(file, "", saveRealPath);
	}

	public static String write(MultipartFile file, String servletRealPath, String savePath) {
		String fileName = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmssSSS");
		return write(file, servletRealPath, savePath, fileName);
	}

	public static String write(MultipartFile file, String servletRealPath, String savePath, String fileName) {
		if (!file.isEmpty()) {
			String clientFileName = file.getOriginalFilename();
			int pointIndex = clientFileName.lastIndexOf(".");
			String suffixName = clientFileName.substring(pointIndex + 1);
			mkDir(servletRealPath + savePath);
			String pathName = savePath + fileName + "." + suffixName;
			File newFile = new File(servletRealPath + pathName);
			try {
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(newFile);
				out.write(file.getBytes());
				out.close();
			} catch (IOException e) {
				throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
			}
			return pathName;
		} else {
			return null;
		}
	}

	private static void mkDir(String pathName) {
		File path = new File(pathName);
		if (!path.exists()) {
			path.mkdirs();
		}
	}

}

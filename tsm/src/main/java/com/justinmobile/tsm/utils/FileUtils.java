package com.justinmobile.tsm.utils;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import com.justinmobile.core.utils.ConvertUtils;

public class FileUtils {

	/** 临时文件的根目录 */
	public static final String TEMP_ROOT = "temp" + File.separator;

	/** 客户端根目录 */
	public static final String CLIENT_ROOT = "client" + File.separator;

	/** 加载文件的扩展名 */
	public static final String EXTENSION_LOAD_FILE = "cap";

	/**
	 * 将指定路径、指定类型的文件转换为byte[]
	 * 
	 * @param path
	 *            路径信息，如果该路径是目录则将该目录(包括子目录)下的所有指定类型的文件转换为byte[]
	 * @param extension
	 *            指定文件类型的扩展名，如果该值为null，则不限定文件类型，扩展名忽略大小写
	 * @param files
	 *            Map，用于存储转换后的byte[]，key为文件的简单名，value为换后的byte[]
	 */
	public static void convertToByteArray(String path, String extension, Map<String, byte[]> files) {
		File file = new File(path);

		if (file.isFile()) {// 如果路径是文件
			// 获取文件名，以最后一个File.separator的字符串作为文件名
			String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
			// 获取文件的扩展名，以最后一个.的字符串作为扩展名
			String fileExtension = path.substring(path.lastIndexOf(".") + 1);// 获取最后一个.之后的字符串作为扩展名

			// 根据扩展名判断是否需要转换
			boolean isFileNeedConvert = false;// 默认为不需要转换
			if ((null == extension) || extension.toUpperCase().equals(fileExtension.toUpperCase())) {
				// 如果extension为null或者extension与文件的扩展名相同(忽略大小写)，则需要转换
				isFileNeedConvert = true;
			}

			// 转换文件为byte[]，并放入Map，key为文件名
			if (isFileNeedConvert) {
				byte[] fileBytes = ConvertUtils.file2ByteArray(file);
				files.put(fileName, fileBytes);
			}
		} else {// 如果路径是目录
			// 获取目录下的所有文件
			String[] paths = file.list();
			// 对每一个子文件进行转换
			for (String subPath : paths) {
				convertToByteArray(path + File.separator + subPath, extension, files);
			}
		}
	}

	/**
	 * 生成的随机目录，相对路径，不包括ServletContext
	 * 
	 * @return 随机目录路径
	 */
	public static String getTempDir() {
		return TEMP_ROOT + RandomStringUtils.randomAlphanumeric(16) + File.separator;
	}

	/**
	 * 生成应用客户端的绝对路径
	 * 
	 * @param ralPath
	 *            应用客户端的相对路径
	 * @return
	 */
	public static String generateApplicationCilentAbsPath(String ralPath) {
		return CLIENT_ROOT + ralPath;
	}

}

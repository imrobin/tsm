package com.justinmobile.tsm.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtils {

	private static final String SEPERATOR_UNIX = "/";

	public static void unZip(String zipFileName, String outputDirectory) throws Exception {
		InputStream in = null;
		FileOutputStream out = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			File output = new File(outputDirectory);
			// 输出目录不存在，创建目录
			if (!output.exists()) {
				output.mkdirs();
			}
			while (e.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				String fileName = zipEntry.getName();

				// 判断当前的项是否是目录
				if (zipEntry.isDirectory()) {// 是目录，则 创建目录
					fileName = fileName.substring(0, fileName.length() - 1);
					File f = new File(outputDirectory + File.separator + fileName);
					f.mkdir();
				} else {// 不是目录，则输出文件
					String subDirectory = fileName.substring(0, fileName.lastIndexOf(SEPERATOR_UNIX));
					// 确定输出目录
					File subFile = new File(outputDirectory + File.separator + subDirectory);
					// 输出目录不存在，创建目录
					if (!subFile.exists()) {
						subFile.mkdirs();
					}

					// 输出文件
					File file = new File(outputDirectory + File.separator + fileName);
					in = zipFile.getInputStream(zipEntry);
					out = new FileOutputStream(file);
					byte[] b = new byte[100000];
					int c;
					while ((c = in.read(b)) != -1) {
						out.write(b, 0, c);
					}
					out.flush();
				}

				// 关闭输入/输出流
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// 关闭所有I/O流
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

}

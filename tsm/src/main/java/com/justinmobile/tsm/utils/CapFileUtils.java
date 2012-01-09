package com.justinmobile.tsm.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;

public class CapFileUtils {

	private static final String[] CAP_FILE_LSIT = new String[] { "Header.cap", "Directory.cap", "Import.cap",
			"Applet.cap", "Class.cap", "Method.cap", "StaticField.cap", "Export.cap", "ConstantPool.cap",
			"RefLocation.cap" };

	private static final Logger log = LoggerFactory.getLogger(CapFileUtils.class);

	/**
	 * 解析加载文件
	 * 
	 * @param tempDir
	 *            处理的临时目录
	 * @param filePath
	 *            待解析的加载文件路径
	 * @return 解析后的byte[]
	 */
	public static byte[] paserLoadFile(String tempDir, String filePath) {
		try {
			// 解压
			String outDir = tempDir + File.separator + "out";
			ZipFileUtils.unZip(filePath, outDir);

			// 将目录下的所有.cap转换成byte[]数组并存在Map中
			Map<String, byte[]> caps = new HashMap<String, byte[]>();
			FileUtils.convertToByteArray(outDir, FileUtils.EXTENSION_LOAD_FILE, caps);

			// 将Map中的所有cap文件进行合并
			byte[] buf = new byte[] {};
			for (String cap : CAP_FILE_LSIT) {
				byte[] capBytes = caps.get(cap);
				if (null != capBytes) {// 如果指定的cap文件存在，则合并
					buf = ByteUtils.contactArray(buf, capBytes);
					if (log.isDebugEnabled()) {
						log.debug(cap + ": " + ConvertUtils.byteArray2HexString(capBytes));
					}
				}
			}
			return buf;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_PASER_ERROR, e);
		}
	}
}

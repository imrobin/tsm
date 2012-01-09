package com.em;

/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetConf {
	private Properties propertie;
	private FileInputStream fis;
	private FileOutputStream fos;
	private static Log logger = LogFactory.getLog("GetConf.class");

	/**
	 * 初始化Configuration类
	 */
	public GetConf() {
		propertie = new Properties();
	}

	/**
	 * 初始化Configuration类
	 * 
	 * @param filePath
	 *            要读取的配置文件的路径+名称
	 */
	public GetConf(String filePath) {
		propertie = new Properties();
		try {
			// System.out.println("filePath="+filePath);
			// File directory = new File(".");
			// File newPath = new
			// File(directory.getCanonicalPath()+"NewFolder");
			// System.out.println("Current path="+newPath.toString());

			fis = new FileInputStream(filePath);
			propertie.load(fis);
			fis.close();
		} catch (FileNotFoundException ex) {
			logger.error("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
			logger.error("错误信息：" + ex.getMessage());
		} catch (IOException ex) {
			logger.error("装载文件--->失败!");
			logger.error("错误信息：" + ex.getMessage());
		}
	}// end ReadConfigInfo(...)

	/**
	 * 重载函数，得到key的值
	 * 
	 * @param key
	 *            取得其值的键
	 * @return key的值
	 */
	public String getValue(String key) {
		if (propertie.containsKey(key)) {
			String value = propertie.getProperty(key);// 得到某一属性的值
			return value;
		} else
			return "";
	}

	/** */
	/**
	 * 重载函数，得到key的值
	 * 
	 * @param fileName
	 *            properties文件的路径+文件名
	 * @param key
	 *            取得其值的键
	 * @return key的值
	 */
	public String getValue(String fileName, String key) {
		try {
			String value = "";
			fis = new FileInputStream(fileName);
			propertie.load(fis);
			fis.close();
			if (propertie.containsKey(key)) {
				value = propertie.getProperty(key);
				return value;
			} else
				return value;
		} catch (FileNotFoundException e) {
			logger.error("未找到文件，或文件不存在！");
			return "";
		} catch (IOException e) {
			logger.error("文件异常！");
			return "";
		} catch (Exception ex) {
			logger.error("文件异常！");
			return "";
		}
	}

	/**
	 * 清除properties文件中所有的key和其值
	 */
	public void clear() {
		propertie.clear();
	}// end clear();

	/**
	 * 改变或添加一个key的值，当key存在于properties文件中时该key的值被value所代替， 当key不存在时，该key的值是value
	 * 
	 * @param key
	 *            要存入的键
	 * @param value
	 *            要存入的值
	 */
	public void setValue(String key, String value) {
		propertie.setProperty(key, value);
	}

	/**
	 * 将更改后的文件数据存入指定的文件中，该文件可以事先不存在。
	 * 
	 * @param fileName
	 *            文件路径+文件名称
	 * @param description
	 *            对该文件的描述
	 */
	public void saveFile(String fileName, String description) {
		try {
			fos = new FileOutputStream(fileName);
			propertie.store(fos, description);
			fos.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		}
	}
	/*
	 * public static void main(String[] args) { GetConf rc=new
	 * GetConf("config/emconfig.properties"); String host =
	 * rc.getValue("hsmip"); String port = rc.getValue("hsmport");
	 * 
	 * System.out.println("hsmip = " + host); System.out.println("hsmport = " +
	 * port); }//end main()
	 */
}

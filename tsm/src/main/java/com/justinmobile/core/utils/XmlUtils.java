package com.justinmobile.core.utils;

import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlUtils {

	public static String formatXml(String src) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(src);
		// 格式化输出格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter writer = new StringWriter();
		// 格式化输出流
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		// 将document写入到输出流
		xmlWriter.write(document);
		xmlWriter.close();
		return writer.toString();
	}
}

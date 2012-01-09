/**  
 * Filename:    MimeUtils.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-7-22 下午05:51:16  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-7-22     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;



public class MimeUtils {
	/**  
	 * @author crofton@citiz.net  
	 * @param fileName name of downloaded file without extention  
	 * @param extension extension of downloaded file with dot character  
	 * @return encoded fileName for IE  
	 * @throws UnsupportedEncodingException never happened  
	 */  
	public static String encodeFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {   
		   String agent = request.getHeader("USER-AGENT");   
		    if (null != agent && -1 != agent.indexOf("MSIE")) {   
		        return URLEncoder.encode(fileName, "UTF8");   
		    }else if (null != agent && -1 != agent.indexOf("Mozilla")) {   
		        return "=?UTF-8?B?"+(new String(Base64.encodeBase64(fileName.getBytes("UTF-8"))))+"?=";   
		    } else {   
		        return fileName;   
		    }   
		}  



}




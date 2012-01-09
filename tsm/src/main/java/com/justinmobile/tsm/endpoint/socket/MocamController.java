package com.justinmobile.tsm.endpoint.socket;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.justinmobile.core.utils.encode.EncodeUtils;
import com.justinmobile.core.utils.web.ServletUtils;

@Controller
@RequestMapping("/mocam/")
public class MocamController {

	@Autowired
	private MocamHandler mocamHandler;

	@RequestMapping
	public void handler(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedReader reader = request.getReader();
			StringBuilder buf = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			String text = buf.toString();
			if (StringUtils.isBlank(text)) {
				text = request.getParameter("xml");
			}
			if (StringUtils.isBlank(text)) {
				ServletUtils.sendMessage(response, EncodeUtils.DEFAULT_URL_ENCODING, ServletUtils.HTML_TYPE, "not request content");
			} else {
				String result = mocamHandler.messageReceived(text);
				ServletUtils.sendMessage(response, EncodeUtils.DEFAULT_URL_ENCODING, ServletUtils.XML_TYPE, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

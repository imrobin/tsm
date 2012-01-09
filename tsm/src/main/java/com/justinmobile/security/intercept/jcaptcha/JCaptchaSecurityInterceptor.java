package com.justinmobile.security.intercept.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.ServletUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.multitype.MultiTypeCaptchaService;

public class JCaptchaSecurityInterceptor implements Filter {

	private static Logger logger = LoggerFactory.getLogger(JCaptchaSecurityInterceptor.class);

	public static final String DEFAULT_CAPTCHA_GET_URL = "/j_captcha_get";

	public static final String DEFAULT_CAPTCHA_PARAMTER_NAME = "j_captcha_response";

	public static final String CAPTCHA_IMAGE_FORMAT = "jpeg";

	private MultiTypeCaptchaService captchaService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		captchaService = (MultiTypeCaptchaService) context.getBean("captchaService");
	}

	@Override
	public void doFilter(ServletRequest theRequest, ServletResponse theResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) theRequest;
		HttpServletResponse response = (HttpServletResponse) theResponse;
		String servletPath = request.getServletPath();
		if (StringUtils.startsWith(servletPath, DEFAULT_CAPTCHA_GET_URL)) {
			showImage(request, response);
		} else {
			String challengeResponse = request.getParameter(DEFAULT_CAPTCHA_PARAMTER_NAME);
			JsonMessage message = new JsonMessage();
			if (StringUtils.isNotBlank(challengeResponse)) {
				String captchaId = request.getSession().getId();
				boolean result = validateCaptchaChallenge(captchaId, challengeResponse);
				message.setSuccess(result);
				if (result) {
					chain.doFilter(request, response);
				} else {
					message.setMessage(PlatformMessage.CAPTCHA_ERROR.getMessage());
					ServletUtils.sendMessage(response, JsonBinder.buildNormalBinder().toJson(message));
				}
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	protected void showImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		byte[] captchaChallengeAsJpeg = null;
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			String captchaId = request.getSession().getId();
			BufferedImage challenge = captchaService.getImageChallengeForID(captchaId, request.getLocale());
			ImageIO.write(challenge, CAPTCHA_IMAGE_FORMAT, jpegOutputStream);
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		} catch (CaptchaServiceException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

		response.reset();
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	protected boolean validateCaptchaChallenge(final String captchaId, final String challengeResponse) {
		try {
			return captchaService.validateResponseForID(captchaId, challengeResponse);
		} catch (CaptchaServiceException e) {
			logger.debug(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void destroy() {

	}

}

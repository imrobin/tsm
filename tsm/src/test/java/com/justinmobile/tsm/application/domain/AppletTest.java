package com.justinmobile.tsm.application.domain;

import org.junit.Assert;
import org.junit.Test;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

public class AppletTest {

	@Test
	public void testValidateInstallParams() {
		Applet applet = new Applet();
		applet.setInstallParams("C900EF08C8020100C7020010");

		try {
			applet.validateInstallParams();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

	}

	@Test
	public void testValidateInstallParamsNoParams() {
		Applet applet = new Applet();

		try {
			applet.validateInstallParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
	
	@Test
	public void testValidateInstallParamsErrorFormat() {
		Applet applet = new Applet();
		applet.setInstallParams("C901EF04C8020100");

		try {
			applet.validateInstallParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateInstallParamsNoC7() {
		Applet applet = new Applet();
		applet.setInstallParams("C900EF04C8020100");

		try {
			applet.validateInstallParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateInstallParamsNoC8() {
		Applet applet = new Applet();
		applet.setInstallParams("C900EF04C7020010");

		try {
			applet.validateInstallParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}

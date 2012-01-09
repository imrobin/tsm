package com.justinmobile.tsm.application.domain;

import org.junit.Assert;
import org.junit.Test;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

public class LoadFileVersionTest {

	@Test
	public void testValidateLoadParams() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadParams("EF0CC6025DC9C702000AC8020800");

		try {
			lfVer.validateLoadParams();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
	
	@Test
	public void testValidateLoadParamsNoParams() {
		LoadFileVersion lfVer = new LoadFileVersion();

		try {
			lfVer.validateLoadParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateLoadParamsErrorFormat() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadParams("EF0CC6025DC9C702000AC802800");

		try {
			lfVer.validateLoadParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateLoadParamsNoC6() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadParams("EF08C702000AC8020800");

		try {
			lfVer.validateLoadParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
	
	@Test
	public void testValidateLoadParamsNoC7() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadParams("EF08C6025DC9C8020800");

		try {
			lfVer.validateLoadParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
	
	@Test
	public void testValidateLoadParamsNoC8() {
		LoadFileVersion lfVer = new LoadFileVersion();
		lfVer.setLoadParams("EF08C6025DC9C702000A");

		try {
			lfVer.validateLoadParams();
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			if (PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR != e.getErrorCode()) {
				Assert.fail("抛出其他异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}

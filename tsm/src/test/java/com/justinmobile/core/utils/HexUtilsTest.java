package com.justinmobile.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.justinmobile.core.exception.PlatformException;

public class HexUtilsTest {
	private final String hexStringUpperCase = "0123456789ABCDEF";
	private final String hexStringLowerCase = "0123456789abcdef";

	@Test
	public void testValidateUpperCase() {
		HexUtils.validate(hexStringUpperCase);
	}

	@Test
	public void testValidateLowerCase() {
		HexUtils.validate(hexStringLowerCase);
	}

	@Test
	public void testValidateException() {
		List<String> errorHexString = new ArrayList<String>();

		errorHexString.add(null);// 空指针
		errorHexString.add("1");// 长度错误
		errorHexString.add("你好");// 不支持的字符
		errorHexString.add("01234x");// 不支持的字符
		
		for(String hex:errorHexString){
			try{
				HexUtils.validate(hex);
				Assert.fail();
			}catch (PlatformException e) {
				
			}
		}
		

	}
}

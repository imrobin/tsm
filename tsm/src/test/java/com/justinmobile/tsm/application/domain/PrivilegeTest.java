package com.justinmobile.tsm.application.domain;

import junit.framework.Assert;

import org.junit.Test;

public class PrivilegeTest {

	/**
	 * 默认值
	 */
	@Test
	public void testBiuld01() {
		Privilege privilege = new Privilege();
		Assert.assertEquals(Integer.parseInt("00000000", 2), (int) privilege.biuld());

	}

	/**
	 * 普通安全域
	 */
	@Test
	public void testBiuld02() {
		Privilege privilege = new Privilege();
		privilege.setSd(true);
		Assert.assertEquals(Integer.parseInt("10000000", 2), (int) privilege.biuld());
	}

	/**
	 * 委托管理安全域
	 */
	@Test
	public void testBiuld03() {
		Privilege privilege = new Privilege();
		privilege.setSd(true);
		privilege.setToken(true);
		Assert.assertEquals(Integer.parseInt("10100000", 2), (int) privilege.biuld());
	}

	/**
	 * DAP安全域
	 */
	@Test
	public void testBiuld04() {
		Privilege privilege = new Privilege();
		privilege.setSd(true);
		privilege.setDap(true);
		Assert.assertEquals(Integer.parseInt("11000000", 2), (int) privilege.biuld());
	}

	/**
	 * 强制DAP安全域
	 */
	@Test
	public void testBiuld05() {
		Privilege privilege = new Privilege();
		privilege.setSd(true);
		privilege.setDapForce(true);
		Assert.assertEquals(Integer.parseInt("11000001", 2), (int) privilege.biuld());
	}

	/**
	 * 锁卡
	 */
	@Test
	public void testBiuld06() {
		Privilege privilege = new Privilege();
		privilege.setLockCard(true);
		Assert.assertEquals(Integer.parseInt("00010000", 2), (int) privilege.biuld());
	}

	/**
	 * 废卡
	 */
	@Test
	public void testBiuld07() {
		Privilege privilege = new Privilege();
		privilege.setAbandonCard(true);
		Assert.assertEquals(Integer.parseInt("00001000", 2), (int) privilege.biuld());
	}

	/**
	 * 默认选择
	 */
	@Test
	public void testBiuld8() {
		Privilege privilege = new Privilege();
		privilege.setDefaultSelect(true);
		Assert.assertEquals(Integer.parseInt("00000100", 2), (int) privilege.biuld());
	}

	/**
	 * CVM管理
	 */
	@Test
	public void testBiuld09() {
		Privilege privilege = new Privilege();
		privilege.setCvm(true);
		Assert.assertEquals(Integer.parseInt("00000010", 2), (int) privilege.biuld());
	}

	/**
	 * 委托管理安全域+锁卡+废卡+默认选择+CVM管理
	 */
	@Test
	public void testBiuld10() {
		Privilege privilege = new Privilege();
		privilege.setSd(true);
		privilege.setToken(true);
		privilege.setLockCard(true);
		privilege.setAbandonCard(true);
		privilege.setDefaultSelect(true);
		privilege.setCvm(true);
		Assert.assertEquals(Integer.parseInt("10111110", 2), (int) privilege.biuld());
	}

	/**
	 * 默认权限
	 */
	@Test
	public void testParse01() {
		Privilege privilege = Privilege.parse(Integer.parseInt("00000000", 2));
		Assert.assertTrue("安全域", !privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 安全域
	 */
	@Test
	public void testParse02() {
		Privilege privilege = Privilege.parse(Integer.parseInt("10000000", 2));
		Assert.assertTrue("安全域", privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 委托管理安全域
	 */
	@Test
	public void testParse03() {
		Privilege privilege = Privilege.parse(Integer.parseInt("10100000", 2));
		Assert.assertTrue("安全域", privilege.isSd());
		Assert.assertTrue("委托管理安全域", privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * DAP安全域
	 */
	@Test
	public void testParse04() {
		Privilege privilege = Privilege.parse(Integer.parseInt("11000000", 2));
		Assert.assertTrue("安全域", privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 强制DAP安全域
	 */
	@Test
	public void testParse05() {
		Privilege privilege = Privilege.parse(Integer.parseInt("11000001", 2));
		Assert.assertTrue("安全域", privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 锁卡
	 */
	@Test
	public void testParse06() {
		Privilege privilege = Privilege.parse(Integer.parseInt("00010000", 2));
		Assert.assertTrue("安全域", !privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 废卡
	 */
	@Test
	public void testParse07() {
		Privilege privilege = Privilege.parse(Integer.parseInt("00001000", 2));
		Assert.assertTrue("安全域", !privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * 默认选择
	 */
	@Test
	public void testParse08() {
		Privilege privilege = Privilege.parse(Integer.parseInt("00000100", 2));
		Assert.assertTrue("安全域", !privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", privilege.isDefaultSelect());
		Assert.assertTrue("CVM", !privilege.isCvm());
	}
	
	/**
	 * CVM
	 */
	@Test
	public void testParse09() {
		Privilege privilege = Privilege.parse(Integer.parseInt("00000010", 2));
		Assert.assertTrue("安全域", !privilege.isSd());
		Assert.assertTrue("委托管理安全域", !privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", !privilege.isLockCard());
		Assert.assertTrue("废卡", !privilege.isAbandonCard());
		Assert.assertTrue("默认选择", !privilege.isDefaultSelect());
		Assert.assertTrue("CVM", privilege.isCvm());
	}
	
	/**
	 * 委托管理安全域+锁卡+废卡+默认选择+CVM管理
	 */
	@Test
	public void testParse10() {
		Privilege privilege = Privilege.parse(Integer.parseInt("10111110", 2));
		Assert.assertTrue("安全域", privilege.isSd());
		Assert.assertTrue("委托管理安全域", privilege.isToken());
		Assert.assertTrue("DAP安全域", !privilege.isDap());
		Assert.assertTrue("强制DAP安全域", !privilege.isDapForce());
		Assert.assertTrue("锁卡", privilege.isLockCard());
		Assert.assertTrue("废卡", privilege.isAbandonCard());
		Assert.assertTrue("默认选择", privilege.isDefaultSelect());
		Assert.assertTrue("CVM", privilege.isCvm());
	}
}

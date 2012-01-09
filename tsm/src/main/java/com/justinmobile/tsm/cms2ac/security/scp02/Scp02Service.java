package com.justinmobile.tsm.cms2ac.security.scp02;

import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

public interface Scp02Service {

	byte[] computeMac(byte[] macSource, byte[] initVector, Cms2acParam cms2acParam, boolean isCmac);

	byte[] computeMacNextIcv(byte[] macSource, byte[] initVector, Cms2acParam cms2acParam, boolean isCmac);

	byte[] encryptData(byte[] encSource, Cms2acParam cms2acParam);

	byte[] encryptKey(byte[] encSource, Cms2acParam cms2acParam);

	byte[] generateKeyCheckValue(byte[] keyValue, Cms2acParam cms2acParam);

	byte[] generateRandom(int randomLength);

	byte[] exportKey(KeyProfile keyProfile, Cms2acParam cms2acParam);

	byte[] exportKey(KeyProfile keyProfile, String mobileNo);

	byte[] getKeyCheckValue(byte[] keyValue);

	byte[] acquireToken(byte[] src, Cms2acParam cms2acParam);

	public byte[] exportEncKeyAndCheckValue(KeyProfile keyProfile, Cms2acParam cms2acParam);

	/**
	 * 使用TK解密个人化指令
	 * 
	 * @param ciphertext
	 *            个人化指令密文
	 * @param application
	 *            需要对个人化指令进行传输解密的应用
	 * @return 个人化指令明文
	 */
	byte[] decryptPersoData(byte[] ciphertext, Application application);

	/**
	 * 转加密
	 * 
	 * @param ciphertext
	 *            初始的密文
	 * @param application
	 *            需要进行转加密的应用
	 * @param cms2acParam
	 * @return 转加密后的密文
	 */
	byte[] transformEncrypt(byte[] ciphertext, Application application, Cms2acParam cms2acParam);
}

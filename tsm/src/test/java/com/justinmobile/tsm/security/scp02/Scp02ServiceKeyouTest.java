package com.justinmobile.tsm.security.scp02;

import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.SecurityDomainUtils;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.engine.DisperseKeyHelper;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;
import com.justinmobile.tsm.cms2ac.manager.KeyProfileManager;
import com.justinmobile.tsm.cms2ac.security.scp02.AbstractScp02Service;
import com.justinmobile.tsm.cms2ac.security.scp02.EncryptorVendor;
import com.justinmobile.tsm.cms2ac.security.scp02.KeyouCryptoService;
import com.justinmobile.tsm.cms2ac.security.scp02.Scp02ServiceKeyou;
import com.justinmobile.tsm.cms2ac.security.scp02.Scp02ServiceLocal;
import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@TransactionConfiguration(defaultRollback = true)
public class Scp02ServiceKeyouTest extends BaseAbstractTest {

	private Scp02ServiceKeyou scp02ServiceKeyou;

	private Scp02ServiceLocal scp02ServiceLocal;

	private final String KEY_PLANITEXT_16 = "0123456789ABCDEF0123456789ABCDEF";

	private final String KEY_CIPHERTEXT_16 = "6A4F4C591884A4A0ED0CDB3B6AF947A9";

	private final int KEY_INDEX_16 = 16;

	private final Logger log = LogUtils.getL7dLogger(Scp02ServiceKeyouTest.class);

	@Autowired
	private HsmkeyConfigManager hsmKeyConfigManager;

	@Autowired
	private DisperseKeyHelper disperseKeyHelper;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private KeyProfileManager keyProfileManager;

	@Test
	public void testComputeMac() {
		log.info("*********************** beging HsmGenerateMAC ***************************");
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] macSource = ConvertUtils.hexString2ByteArray("0123456789ABCDEF");
		byte[] initVector = ConvertUtils.hexString2ByteArray("0000000000000000");
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localMac = scp02ServiceLocal.computeMac(macSource, initVector, cms2acParam, true);
		byte[] keyouMac = scp02ServiceKeyou.computeMac(macSource, initVector, cms2acParam, true);

		log.info("localMac");
		log.info(ConvertUtils.byteArray2HexString(localMac));
		log.info("keyouMac");
		log.info(ConvertUtils.byteArray2HexString(keyouMac));
		Assert.assertTrue(ArrayUtils.isEquals(localMac, keyouMac));

		scp02ServiceLocal.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		scp02ServiceKeyou.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);

		byte[] disperseLocalMac = scp02ServiceLocal.computeMac(macSource, initVector, cms2acParam, true);
		byte[] disperseKeyouMac = scp02ServiceKeyou.computeMac(macSource, initVector, cms2acParam, true);

		log.info("disperseLocalMac");
		log.info(ConvertUtils.byteArray2HexString(disperseLocalMac));
		log.info("disperseKeyouMac");
		log.info(ConvertUtils.byteArray2HexString(disperseKeyouMac));
		Assert.assertTrue(ArrayUtils.isEquals(disperseLocalMac, disperseKeyouMac));
		log.info("*********************** end HsmGenerateMAC ******************************");
	}

	@Test
	public void testComputeMacNextIcv() {
		log.info("*********************** beging HsmGenerateCMAC **************************");
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] macSource = ConvertUtils.hexString2ByteArray("0123456789ABCDEF");
		byte[] initVector = ConvertUtils.hexString2ByteArray("0000000000000000");
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localMacNextIcv = scp02ServiceLocal.computeMacNextIcv(macSource, initVector, cms2acParam, true);
		byte[] keyouMacNextIcv = scp02ServiceKeyou.computeMacNextIcv(macSource, initVector, cms2acParam, true);

		log.info("localMacNextIcv");
		log.info(ConvertUtils.byteArray2HexString(localMacNextIcv));
		log.info("keyouMacNextIcv");
		log.info(ConvertUtils.byteArray2HexString(keyouMacNextIcv));
		Assert.assertTrue(ArrayUtils.isEquals(localMacNextIcv, keyouMacNextIcv));

		scp02ServiceLocal.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		scp02ServiceKeyou.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);

		byte[] disperseLocalMacNextIcv = scp02ServiceLocal.computeMacNextIcv(macSource, initVector, cms2acParam, true);
		byte[] disperseKeyouMacNextIcv = scp02ServiceKeyou.computeMacNextIcv(macSource, initVector, cms2acParam, true);

		log.info("disperseLocalMacNextIcv");
		log.info(ConvertUtils.byteArray2HexString(disperseLocalMacNextIcv));
		log.info("disperseKeyouMacNextIcv");
		log.info(ConvertUtils.byteArray2HexString(disperseKeyouMacNextIcv));
		Assert.assertTrue(ArrayUtils.isEquals(disperseLocalMacNextIcv, disperseKeyouMacNextIcv));
		log.info("*********************** end HsmGenerateMAC ******************************");
	}

	@Test
	public void testEncryptData() {
		log.info("*********************** begin HsmDataEncrypt ****************************");
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] encSource = ConvertUtils.hexString2ByteArray("0123456789ABCDEF");
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localEncData = scp02ServiceLocal.encryptData(encSource, cms2acParam);
		byte[] keyouEncData = scp02ServiceKeyou.encryptData(encSource, cms2acParam);

		log.info("localEncData");
		log.info(ConvertUtils.byteArray2HexString(localEncData));
		log.info("keyouEncData");
		log.info(ConvertUtils.byteArray2HexString(keyouEncData));
		Assert.assertTrue(ArrayUtils.isEquals(localEncData, keyouEncData));

		scp02ServiceLocal.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		scp02ServiceKeyou.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);

		byte[] disperseLocalEncData = scp02ServiceLocal.encryptData(encSource, cms2acParam);
		byte[] disperseKeyouEncData = scp02ServiceKeyou.encryptData(encSource, cms2acParam);

		log.info("disperseLocalEncData");
		log.info(ConvertUtils.byteArray2HexString(disperseLocalEncData));
		log.info("disperseKeyouEncData");
		log.info(ConvertUtils.byteArray2HexString(disperseKeyouEncData));
		Assert.assertTrue(ArrayUtils.isEquals(disperseLocalEncData, disperseKeyouEncData));

		log.info("*********************** end HsmDataEncrypt ******************************");
	}

	@Test
	@Ignore
	public void testEncryptKey() {
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] encSource = ConvertUtils.hexString2ByteArray("0123456789ABCDEF");
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localEncKey = scp02ServiceLocal.encryptKey(encSource, cms2acParam);
		byte[] keyouEncKey = scp02ServiceKeyou.encryptKey(encSource, cms2acParam);

		log.info("localEncKey");
		log.info(ConvertUtils.byteArray2HexString(localEncKey));
		log.info("keyouEncKey");
		log.info(ConvertUtils.byteArray2HexString(keyouEncKey));
		Assert.assertTrue(ArrayUtils.isEquals(localEncKey, keyouEncKey));

		scp02ServiceLocal.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		scp02ServiceKeyou.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);

		byte[] disperseLocalEncKey = scp02ServiceLocal.encryptKey(encSource, cms2acParam);
		byte[] disperseKeyouEncKey = scp02ServiceKeyou.encryptKey(encSource, cms2acParam);

		log.info("disperseLocalEncKey");
		log.info(ConvertUtils.byteArray2HexString(disperseLocalEncKey));
		log.info("disperseKeyouEncKey");
		log.info(ConvertUtils.byteArray2HexString(disperseKeyouEncKey));
		Assert.assertTrue(ArrayUtils.isEquals(disperseLocalEncKey, disperseKeyouEncKey));
	}

	@Test
	@Ignore
	public void testGenerateKeyCheckValue() {
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] keyValue = ConvertUtils.hexString2ByteArray("11111111111111112222222222222222");
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localCheckValue = scp02ServiceLocal.generateKeyCheckValue(keyValue, cms2acParam);
		byte[] keyouCheckValue = scp02ServiceKeyou.generateKeyCheckValue(keyValue, cms2acParam);

		log.info("localCheckValue");
		log.info(ConvertUtils.byteArray2HexString(localCheckValue));
		log.info("keyouCheckValue");
		log.info(ConvertUtils.byteArray2HexString(keyouCheckValue));
		Assert.assertTrue(ArrayUtils.isEquals(localCheckValue, keyouCheckValue));
	}

	@Test
	@Ignore
	public void testExportKey() {
		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		scp02ServiceLocal.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		scp02ServiceKeyou.setDisperseLevel(AbstractScp02Service.THREE_LEVEL_DISPERSE);
		Cms2acParam cms2acParam = getCms2acParam();
		KeyProfile keyProfile = cms2acParam.getCurrentSecurityDomain().getEncKey();

		byte[] localExportKey = scp02ServiceLocal.exportKey(keyProfile, cms2acParam);
		byte[] keyouExportKey = scp02ServiceKeyou.exportKey(keyProfile, cms2acParam);

		log.info("localExportKey");
		log.info(ConvertUtils.byteArray2HexString(localExportKey));
		log.info("keyouExportKey");
		log.info(ConvertUtils.byteArray2HexString(keyouExportKey));
		// Assert.assertTrue(ArrayUtils.isEquals(localExportKey,
		// keyouExportKey));
	}

	@Test
	public void testGenerateRandom() {
		log.info("*********************** begin HsmGenarateRandom *************************");

		scp02ServiceLocal = getScp02ServiceLocal();
		scp02ServiceKeyou = getScp02ServiceKeyou();

		byte[] keyouRandom1 = scp02ServiceKeyou.generateRandom(8);
		byte[] keyouRandom2 = scp02ServiceKeyou.generateRandom(8);

		log.info("keyouRandom1");
		log.info(ConvertUtils.byteArray2HexString(keyouRandom1));
		log.info("keyouRandom2");
		log.info(ConvertUtils.byteArray2HexString(keyouRandom2));
		Assert.assertFalse(ArrayUtils.isEquals(keyouRandom1, keyouRandom2));

		log.info("*********************** end HsmGenarateRandom ***************************");
	}

	@Test
	public void testDecryptPersoData_3DES_ECB() {
		SecureAlgorithm secureAlgorithm = SecureAlgorithm.TRIPLE_DES_ECB;

		log.info("************** begin HsmDataDecrypt" + secureAlgorithm.getDescription() + "******");

		byte[] ciphertext = ConvertUtils.hexString2ByteArray("000102030405060708090A0B0C0D0E0F");

		ApplicationKeyProfile applicationKeyProfile = new ApplicationKeyProfile();
		applicationKeyProfile.setKeyValue(KEY_PLANITEXT_16);
		applicationKeyProfile.setKeyType(ApplicationKeyProfile.TYPE_TK);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);

		Application application = ApplicationUtils.createDefult();
		application.setPersoCmdTransferSecureAlgorithm(secureAlgorithm);

		application.addApplicationKeyProfile(applicationKeyProfile);
		applicationKeyProfile.addHsmkeyConfig(hsmkeyConfig);

		applicationManager.saveOrUpdate(application);

		byte[] localDecryptedDate = getScp02ServiceLocal().decryptPersoData(ciphertext, application);
		byte[] keyouDecryptedDate = getScp02ServiceKeyou().decryptPersoData(ciphertext, application);

		log.info("localDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(localDecryptedDate));
		log.info("keyouDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(keyouDecryptedDate));

		Assert.assertTrue(ArrayUtils.isEquals(localDecryptedDate, keyouDecryptedDate));

		log.info("************** end HsmDataDecrypt" + secureAlgorithm.getDescription() + "********");
	}

	@Test
	public void testDecryptPersoData_3DES_CBC() {
		SecureAlgorithm secureAlgorithm = SecureAlgorithm.TRIPLE_DES_CBC;
		log.info("************** begin HsmDataDecrypt" + secureAlgorithm.getDescription() + "******");

		byte[] ciphertext = ConvertUtils.hexString2ByteArray("000102030405060708090A0B0C0D0E0F");

		ApplicationKeyProfile applicationKeyProfile = new ApplicationKeyProfile();
		applicationKeyProfile.setKeyValue(KEY_PLANITEXT_16);
		applicationKeyProfile.setKeyType(ApplicationKeyProfile.TYPE_TK);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);

		Application application = ApplicationUtils.createDefult();
		application.setPersoCmdTransferSecureAlgorithm(secureAlgorithm);

		application.addApplicationKeyProfile(applicationKeyProfile);
		applicationKeyProfile.addHsmkeyConfig(hsmkeyConfig);

		applicationManager.saveOrUpdate(application);

		byte[] localDecryptedDate = getScp02ServiceLocal().decryptPersoData(ciphertext, application);
		byte[] keyouDecryptedDate = getScp02ServiceKeyou().decryptPersoData(ciphertext, application);

		log.info("localDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(localDecryptedDate));
		log.info("keyouDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(keyouDecryptedDate));

		Assert.assertTrue(ArrayUtils.isEquals(localDecryptedDate, keyouDecryptedDate));

		log.info("************** end HsmDataDecrypt" + secureAlgorithm.getDescription() + "********");
	}

	@Test
	public void testDecryptPersoData_DES_CBC() {
		SecureAlgorithm secureAlgorithm = SecureAlgorithm.DES_CBC;

		log.info("************** begin HsmDataDecrypt" + secureAlgorithm.getDescription() + "******");

		log.info(secureAlgorithm.getDescription());

		byte[] ciphertext = ConvertUtils.hexString2ByteArray("000102030405060708090A0B0C0D0E0F");

		ApplicationKeyProfile applicationKeyProfile = new ApplicationKeyProfile();
		applicationKeyProfile.setKeyValue(KEY_PLANITEXT_16);
		applicationKeyProfile.setKeyType(ApplicationKeyProfile.TYPE_TK);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);

		Application application = ApplicationUtils.createDefult();
		application.setPersoCmdTransferSecureAlgorithm(secureAlgorithm);

		application.addApplicationKeyProfile(applicationKeyProfile);
		applicationKeyProfile.addHsmkeyConfig(hsmkeyConfig);

		applicationManager.saveOrUpdate(application);

		byte[] localDecryptedDate = getScp02ServiceLocal().decryptPersoData(ciphertext, application);
		log.info("localDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(localDecryptedDate));

		byte[] keyouDecryptedDate = getScp02ServiceKeyou().decryptPersoData(ciphertext, application);
		log.info("keyouDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(keyouDecryptedDate));

		Assert.assertTrue(ArrayUtils.isEquals(localDecryptedDate, keyouDecryptedDate));

		log.info("************** end HsmDataDecrypt" + secureAlgorithm.getDescription() + "********");
	}

	@Test
	public void testDecryptPersoData_AES() {
		SecureAlgorithm secureAlgorithm = SecureAlgorithm.AES;

		log.info("************** begin HsmDataDecrypt" + secureAlgorithm.getDescription() + "******");

		log.info(secureAlgorithm.getDescription());

		byte[] ciphertext = ConvertUtils.hexString2ByteArray("000102030405060708090A0B0C0D0E0F");

		ApplicationKeyProfile applicationKeyProfile = new ApplicationKeyProfile();
		applicationKeyProfile.setKeyValue(KEY_PLANITEXT_16);
		applicationKeyProfile.setKeyType(ApplicationKeyProfile.TYPE_TK);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);

		Application application = ApplicationUtils.createDefult();
		application.setPersoCmdTransferSecureAlgorithm(secureAlgorithm);

		application.addApplicationKeyProfile(applicationKeyProfile);
		applicationKeyProfile.addHsmkeyConfig(hsmkeyConfig);

		applicationManager.saveOrUpdate(application);

		byte[] localDecryptedDate = getScp02ServiceLocal().decryptPersoData(ciphertext, application);
		byte[] keyouDecryptedDate = getScp02ServiceKeyou().decryptPersoData(ciphertext, application);

		log.info("localDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(localDecryptedDate));
		log.info("keyouDecryptedDate");
		log.info(ConvertUtils.byteArray2HexString(keyouDecryptedDate));

		Assert.assertTrue(ArrayUtils.isEquals(localDecryptedDate, keyouDecryptedDate));

		log.info("************** end HsmDataDecrypt" + secureAlgorithm.getDescription() + "********");
	}

	@Test
	public void testExportKeyAndCheckValue() {
		// 开始准备数据
		KeyProfile keyProfile = new KeyProfile();
		keyProfile.setValue(KEY_PLANITEXT_16);
		keyProfile.setType(KeyProfile.SCP02_DEK_TYPE);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);
		keyProfile.addHsmkeyConfig(hsmkeyConfig);

		keyProfileManager.saveOrUpdate(keyProfile);

		// 开始准备cms2ac
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localExportKey = getScp02ServiceLocal().exportEncKeyAndCheckValue(keyProfile, cms2acParam);
		byte[] keyouExportKey = getScp02ServiceKeyou().exportEncKeyAndCheckValue(keyProfile, cms2acParam);

		log.info("localExportKey");
		log.info(ConvertUtils.byteArray2HexString(localExportKey));
		log.info("keyouExportKey");
		log.info(ConvertUtils.byteArray2HexString(keyouExportKey));

		Assert.assertArrayEquals(localExportKey, keyouExportKey);
	}

	@Test
	public void testTransformEncrypt() {
		byte[] ciphertext = ConvertUtils.hexString2ByteArray("000102030405060708090A0B0C0D0E0F");

		// 开始准备KEK密钥
		ApplicationKeyProfile applicationKeyProfile = new ApplicationKeyProfile();
		applicationKeyProfile.setKeyValue(KEY_PLANITEXT_16);
		applicationKeyProfile.setKeyType(ApplicationKeyProfile.TYPE_KEK);

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmkeyConfig.setCiphertext(KEY_CIPHERTEXT_16);
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setVersion(0);

		Application application = ApplicationUtils.createDefult();
		application.setPersoCmdSensitiveDataSecureAlgorithm(SecureAlgorithm.TRIPLE_DES_CBC);

		application.addApplicationKeyProfile(applicationKeyProfile);
		applicationKeyProfile.addHsmkeyConfig(hsmkeyConfig);

		applicationManager.saveOrUpdate(application);

		// 开始准别Cms2acParam
		Cms2acParam cms2acParam = getCms2acParam();

		byte[] localTransformEncryptedData = getScp02ServiceLocal().transformEncrypt(ciphertext, application, cms2acParam);
		log.info("localTransformEncryptedData");
		log.info(ConvertUtils.byteArray2HexString(localTransformEncryptedData));

		byte[] keyouTransformEncryptedData = getScp02ServiceKeyou().transformEncrypt(ciphertext, application, cms2acParam);
		log.info("localTransformEncryptedData");
		log.info(ConvertUtils.byteArray2HexString(localTransformEncryptedData));

		Assert.assertTrue(ArrayUtils.isEquals(localTransformEncryptedData, keyouTransformEncryptedData));
	}

	private Cms2acParam getCms2acParam() {
		Cms2acParam cms2acParam = new Cms2acParam();
		SecurityDomain securityDomain = SecurityDomainUtils.createDefult();

		HsmkeyConfig hsmkeyConfig = new HsmkeyConfig();
		hsmkeyConfig.setVendor(EncryptorVendor.KEYOU.getValue());
		hsmkeyConfig.setIndex(KEY_INDEX_16);
		hsmKeyConfigManager.saveOrUpdate(hsmkeyConfig);
		{
			KeyProfile keyProfile = new KeyProfile();
			keyProfile.setIndex(KeyProfile.INDEX_MAC);
			keyProfile.setValue(KEY_PLANITEXT_16);

			securityDomain.addKeyProfile(keyProfile);
			keyProfile.addHsmkeyConfig(hsmkeyConfig);

			keyProfileManager.saveOrUpdate(keyProfile);
		}
		{
			KeyProfile keyProfile = new KeyProfile();
			keyProfile.setIndex(KeyProfile.INDEX_ENC);
			keyProfile.setValue(KEY_PLANITEXT_16);
			securityDomain.addKeyProfile(keyProfile);

			securityDomain.addKeyProfile(keyProfile);
			keyProfile.addHsmkeyConfig(hsmkeyConfig);

			keyProfileManager.saveOrUpdate(keyProfile);
		}
		{
			KeyProfile keyProfile = new KeyProfile();
			keyProfile.setIndex(KeyProfile.INDEX_DEK);
			keyProfile.setValue(KEY_PLANITEXT_16);

			securityDomain.addKeyProfile(keyProfile);
			keyProfile.addHsmkeyConfig(hsmkeyConfig);

			keyProfileManager.saveOrUpdate(keyProfile);
		}

		securityDomainManager.saveOrUpdate(securityDomain);
		cms2acParam.setCurrentSecurityDomain(securityDomain);
		cms2acParam.setScp02Counter(0x01);

		LocalTransaction localTransaction = new LocalTransaction();
		localTransaction.setCardNo("4906202893098760");
		cms2acParam.setLocalTransaction(localTransaction);

		cms2acParam.setKid(securityDomain.getEncKey());
		cms2acParam.setKic(securityDomain.getEncKey());
		cms2acParam.setDek(securityDomain.getDekKey());
		return cms2acParam;
	}

	private Scp02ServiceKeyou getScp02ServiceKeyou() {
		Scp02ServiceKeyou scp02ServiceKeyou = new Scp02ServiceKeyou();

		KeyouCryptoService keyouCryptoService = new KeyouCryptoService();
		scp02ServiceKeyou.setKeyouCryptoService(keyouCryptoService);

		scp02ServiceKeyou.setHsmKeyConfigManager(this.hsmKeyConfigManager);
		scp02ServiceKeyou.setDisperseKeyHelper(this.disperseKeyHelper);
		return scp02ServiceKeyou;
	}

	private Scp02ServiceLocal getScp02ServiceLocal() {
		Scp02ServiceLocal scp02ServiceLocal = new Scp02ServiceLocal();
		scp02ServiceLocal.setDisperseKeyHelper(this.disperseKeyHelper);
		return scp02ServiceLocal;
	}

}

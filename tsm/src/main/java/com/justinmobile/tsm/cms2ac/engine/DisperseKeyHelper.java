package com.justinmobile.tsm.cms2ac.engine;

import static com.justinmobile.core.utils.ByteUtils.arrayXOR;
import static com.justinmobile.core.utils.ByteUtils.bitComplement;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.tsm.cms2ac.engine.SecureUtil.fixedPadding;
import static com.justinmobile.tsm.cms2ac.engine.SecureUtil.scp02Padding;

import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Service("disperseKeyHelper")
public class DisperseKeyHelper {

	private static final String SCP02_CMAC_SALT = "0101";

	private static final String SCP02_RMAC_SALT = "0102";

	private static final String SCP02_ENC_SALT = "0182";

	private static final String SCP02_DEK_SALT = "0181";

	// @Autowired
	// @Qualifier("mobileSectionManager")
	// private MobileSectionManager mobileSectionManager;

	// @Autowired
	// private CardInfoManager cardInfoManager;

	// @Autowired
	// private CustomerCardInfoManager customerCardInfoManager;

	// @Autowired
	// private CardSecurityDomainManager cardSecurityDomainManager;

	public byte[] buildScp02FullDisperseFactor(Cms2acParam cms2acParam, int keyType) {
		byte[] scp02FullDisperseFactor = new byte[0];

		byte[] firstSalt = getScp02FirstSalt(cms2acParam);
		scp02FullDisperseFactor = contactArray(scp02FullDisperseFactor, firstSalt);

		byte[] secondSalt = getScp02SecondSalt(cms2acParam);
		scp02FullDisperseFactor = contactArray(scp02FullDisperseFactor, secondSalt);

		byte[] ThirdSalt = getScp02ThirdSalt(cms2acParam, keyType);
		scp02FullDisperseFactor = contactArray(scp02FullDisperseFactor, ThirdSalt);

		return scp02FullDisperseFactor;
	}

	public byte[] getScp02FirstSalt(Cms2acParam cms2acParam) {
		// TODO 需要修改数据库，先写死
		// String mobileNo = cms2acParam.getLocalTransaction().getMobileNo();
		// String provinceNo =
		// mobileSectionManager.getProvinceByMobile(mobileNo);

		String provinceNo = "31";
		return getFirstSalt(provinceNo);
	}

	private byte[] getFirstSalt(String provinceNo) {
		byte[] provinceNoBytes = intToHexBytes(Integer.parseInt(provinceNo), 1);
		byte[] left = scp02DispersePadding(provinceNoBytes);
		byte[] right = bitComplement(left);
		return contactArray(left, right);
	}

	public byte[] getScp02SecondSalt(Cms2acParam cms2acParam) {
		SecurityDomain securityDomain = cms2acParam.getCurrentSecurityDomain();
		return getScp02SecondSalt(securityDomain);
	}

	public byte[] getScp02ThirdSalt(Cms2acParam cms2acParam, int keyType) {
		String cardNo = cms2acParam.getLocalTransaction().getCardNo();
		SecurityDomain securityDomain = cms2acParam.getCurrentSecurityDomain();

		return getScp02ThirdSalt(keyType, cardNo, securityDomain);
	}

	/** **************************专用于密钥导出******************************* */
	public byte[] buildScp02FullDisperseFactor(KeyProfile keyProfile, String mobileNo) {
		byte[] scp02FullDisperseFactor = new byte[0];

		byte[] firstSalt = getScp02FirstSalt(mobileNo);
		scp02FullDisperseFactor = contactArray(scp02FullDisperseFactor, firstSalt);

		SecurityDomain securityDomain = keyProfile.getSecurityDomain();
		byte[] secondSalt = getScp02SecondSalt(securityDomain);
		scp02FullDisperseFactor = contactArray(scp02FullDisperseFactor, secondSalt);
		// TODO 此方法巩佳知说未使用
		/*
		 * CardInfo cardInfo =
		 * customerCardInfoManager.getByMobileNo(mobileNo).getCard(); byte[]
		 * ThirdSalt = getScp02ThirdSalt(keyProfile.getType(),
		 * cardInfo.getCardNo(), securityDomain); scp02FullDisperseFactor =
		 * contactArray(scp02FullDisperseFactor, ThirdSalt);
		 */

		return scp02FullDisperseFactor;
	}

	public byte[] getScp02FirstSalt(String mobileNo) {
		// TODO 需要修改数据库，先写死

		String provinceNo = "31";
		return getFirstSalt(provinceNo);
	}

	public byte[] getScp02SecondSalt(SecurityDomain securityDomain) {
		byte[] aidBytes = ConvertUtils.hexString2ByteArray(securityDomain.getAid());
		byte[] paddedAidBytes = scp02DispersePadding(aidBytes);
		if (aidBytes.length < 8) {
			byte[] tail = ConvertUtils.hexString2ByteArray("0000000000000000");
			paddedAidBytes = contactArray(paddedAidBytes, tail);
		}
		return paddedAidBytes;
	}

	private byte[] getScp02ThirdSalt(int keyType, String cardNo, SecurityDomain securityDomain) {
		byte[] xorLeft = ConvertUtils.hexString2ByteArray(cardNo);

		// CardSecurityDomain cardSecurityDomain =
		// cardSecurityDomainManager.getByCardNoAid(cardNo,
		// securityDomain.getAid());
		int keyVersion = securityDomain.getEncKey().getIndex();
		byte[] keyVersionBytes = intToHexBytes(keyVersion, 1);

		byte[] keyTypeBytes = intToHexBytes(keyType, 1);

		byte[] xorRight = contactArray(keyVersionBytes, keyTypeBytes);
		xorRight = scp02DispersePadding(xorRight);

		byte[] left = arrayXOR(xorLeft, xorRight);
		byte[] right = bitComplement(left);

		return contactArray(left, right);
	}

	private byte[] scp02DispersePadding(byte[] src) {
		if (src.length == 16) {
			return src;
		} else {
			return scp02Padding(src);
		}
	}

	// public byte[] buildScp80FullDisperseFactor(Cms2acParam cms2acParam, int
	// keyType) {
	// byte[] firstSalt = getScp80FirstSalt(cms2acParam);
	// byte[] secondSalt = getScp80SecondSalt(cms2acParam, keyType);
	// return contactArray(firstSalt, secondSalt);
	// }

	public byte[] getScp02MacSessionSalt(Cms2acParam cms2acParam, boolean isCMac) {
		if (isCMac) {
			return getScp02SessionSalt(cms2acParam, SCP02_CMAC_SALT);
		} else {
			return getScp02SessionSalt(cms2acParam, SCP02_RMAC_SALT);
		}
	}

	public byte[] getScp02EncSessionSalt(Cms2acParam cms2acParam) {
		return getScp02SessionSalt(cms2acParam, SCP02_ENC_SALT);
	}

	public byte[] getScp02DekSessionSalt(Cms2acParam cms2acParam) {
		return getScp02SessionSalt(cms2acParam, SCP02_DEK_SALT);
	}

	// private byte[] getScp80FirstSalt(Cms2acParam cms2acParam) {
	// String mobileNo = cms2acParam.getLocalTransaction().getMobileNo();
	// String provinceNo = mobileSectionManager.getProvinceByMobile(mobileNo);
	//
	// byte[] provinceNoBytes = intToHexBytes(Integer.parseInt(provinceNo), 4);
	// byte[] left = fixedPadding(provinceNoBytes, 8, (byte) 0x00, false);
	// byte[] right = bitComplement(left);
	// return contactArray(left, right);
	// }

	// private byte[] getScp80SecondSalt(Cms2acParam cms2acParam, int keyType) {
	// try {
	// byte[] ismiBytes =
	// ConvertUtils.hexString2ByteArray(cms2acParam.getLocalTransaction().getCardNo());
	//
	// byte[] keyVersionBytes = getKeyVersionBytes(cms2acParam, keyType);
	// byte[] keyIndexBytes = getKeyIndexBytes(cms2acParam, keyType);
	// byte[] keyTypeBytes = new byte[] { 0x0C };
	// byte[] saltBytes = contactArray(contactArray(keyTypeBytes,
	// keyVersionBytes), keyIndexBytes);
	// byte[] paddedSaltBytes = padding(saltBytes, (byte) 0x00, false);
	//
	// byte[] sha1Src = contactArray(ismiBytes, paddedSaltBytes);
	// MessageDigest digest = MessageDigest.getInstance("SHA-1");
	// byte[] sha1Result = digest.digest(sha1Src);
	//
	// byte[] left = subArray(sha1Result, 0, 8);
	// byte[] right = bitComplement(left);
	// return contactArray(left, right);
	// } catch (NoSuchAlgorithmException e) {
	// throw new IllegalArgumentException(e);
	// }
	// }
	//
	// private byte[] getKeyIndexBytes(Cms2acParam cms2acParam, int keyType) {
	// KeyProfile keyProfile = null;
	// if (keyType == KeyProfile.SCP80_KID_TYPE) {
	// keyProfile = cms2acParam.getKid();
	// } else {
	// keyProfile = cms2acParam.getKic();
	// }
	// return intToHexBytes(scp80KeyIndex(keyProfile), 1);
	// }
	//
	// private byte[] getKeyVersionBytes(Cms2acParam cms2acParam, int keyType) {
	// KeyProfile keyProfile = null;
	// if (keyType == KeyProfile.SCP80_KID_TYPE) {
	// keyProfile = cms2acParam.getKid();
	// } else {
	// keyProfile = cms2acParam.getKic();
	// }
	// return intToHexBytes(keyProfile.getVersion(), 1);
	// }
	//
	// private Integer scp80KeyIndex(KeyProfile keyProfile) {
	// int keyId = keyProfile.getIndex();
	// int lo5bit = (0x1F & keyId);
	// int hi3bit = 0x80;
	// return hi3bit | lo5bit;
	// }

	private byte[] getScp02SessionSalt(Cms2acParam cms2acParam, String constantSalt) {
		byte[] constBytes = ConvertUtils.hexString2ByteArray(constantSalt);
		byte[] scp02Counter = intToHexBytes(cms2acParam.getScp02Counter(), 2);
		byte[] paddingSource = contactArray(constBytes, scp02Counter);
		byte[] salt = fixedPadding(paddingSource, 16, (byte) 0x00, true);
		return salt;
	}
}

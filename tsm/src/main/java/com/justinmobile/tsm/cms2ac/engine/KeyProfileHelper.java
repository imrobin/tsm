package com.justinmobile.tsm.cms2ac.engine;

import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.toHexString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardSdScp02Key;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardSdScp02KeyManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.domain.ParamDomainKey;
import com.justinmobile.tsm.cms2ac.security.scp02.Scp02Service;

@Service("keyProfileHelper")
public class KeyProfileHelper {

	@Autowired
	private Scp02Service scp02Service;

	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	protected CardSdScp02KeyManager cardSdScp02KeyManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	public byte[] getPutKeyData(Cms2acParam cms2acParam, SecurityDomain updateSd) {
		int keyVersion = updateSd.getCurrentKeyVersion();
		byte[] keyData = intToHexBytes(keyVersion, 1);

		List<KeyProfile> keys = new ArrayList<KeyProfile>(3);
		keys.add(updateSd.getEncKey());
		keys.add(updateSd.getMacKey());
		keys.add(updateSd.getDekKey());
		byte[] formatedKeysData = generateKeyData(keys, cms2acParam);
		return contactArray(keyData, formatedKeysData);
	}

	private byte[] generateKeyData(List<KeyProfile> scp02UpdateKeys, Cms2acParam cms2acParam) {
		// LocalTransaction localTransaction =
		// cms2acParam.getLocalTransaction();
		// String procedureName = localTransaction.getProcedureName();
		// if
		// (LocalTransaction.PROCEDURE_NAME_UPDATE_SD_KEY.equals(procedureName))
		// {
		// keyValue = getKeyFromProcess(scp02UpdateKeys, cms2acParam);
		// } else if
		// (LocalTransaction.PROCEDURE_NAME_DOWNLOAD_APP.equals(procedureName))
		// {
		// } else {
		// throw new TransactionException(localTransaction,
		// TransErrorCode.INVALID_TRANS_TYPE);
		// }
		byte[] keyValue = null;
		keyValue = getKeyFromCryptoService(scp02UpdateKeys, cms2acParam);
		return keyValue;
	}

	public byte[] getKeyFromProcess(List<KeyProfile> scp02UpdateKeys, Cms2acParam cms2acParam) {
		System.out.println("getKeyFromProcess:");
		List<ParamDomainKey> paramDomainKeys = getParamDomainKeys(cms2acParam);
		byte[] keyData = new byte[0];
		for (KeyProfile updateKey : scp02UpdateKeys) {
			for (ParamDomainKey paramDomainKey : paramDomainKeys) {
				if (isEqual(paramDomainKey, updateKey)) {
					byte[] keyValue = ConvertUtils.hexString2ByteArray(paramDomainKey.getKeyValue());
					byte[] checkValue = ConvertUtils.hexString2ByteArray(paramDomainKey.getKeyCheck());

					System.out.println("keyValue=" + toHexString(keyValue) + ", checkValue=" + toHexString(checkValue));
					byte[] scp02KeyData = generateKeyData(keyValue, checkValue, cms2acParam);
					keyData = contactArray(keyData, scp02KeyData);
				}
			}
		}
		return keyData;
	}

	private boolean isEqual(ParamDomainKey paramDomainKey, KeyProfile keyProfile) {
		return StringUtils.equals(paramDomainKey.getKeyID(), keyProfile.getId().toString())
				&& StringUtils.equals(paramDomainKey.getKeyType(), keyProfile.getType().toString());
	}

	private List<ParamDomainKey> getParamDomainKeys(Cms2acParam cms2acParam) {
		List<ParamDomainKey> paramDomainKeys = null;
		// LocalTransaction localTransaction =
		// cms2acParam.getLocalTransaction();
		//
		// if
		// (LocalTransaction.LAUNCHER_CENTER.equals(localTransaction.getLauncher()))
		// {
		// CentralOtaProcess centralOtaProcess =
		// localTransaction.getLastCentralOtaProcess();
		// paramDomainKeys = centralOtaProcess.getParam().getParamDomainKeys();
		// } else {
		// ProviderProcess providerProcess =
		// localTransaction.getLastProviderProcess();
		// paramDomainKeys = providerProcess.getParam().getParamDomainKeys();
		// }

		return paramDomainKeys;
	}

	private byte[] getKeyFromCryptoService(List<KeyProfile> scp02UpdateKeys, Cms2acParam cms2acParam) {
		System.out.println("getKeyFromCryptoService:");
		byte[] keyData = new byte[0];
		for (KeyProfile updateKey : scp02UpdateKeys) {
			byte[] encKeyAndCheckValue = scp02Service.exportEncKeyAndCheckValue(updateKey, cms2acParam);

			byte[] keyValue = ByteUtils.subArray(encKeyAndCheckValue, 0, 16);
			// = scp02Service.exportKey(updateKey, cms2acParam);
			byte[] checkValue = ByteUtils.subArray(encKeyAndCheckValue, 16, 19);
			// = scp02Service.generateKeyCheckValue(keyValue, cms2acParam);

			System.out.println("keyValue=" + toHexString(keyValue) + ", checkValue=" + toHexString(checkValue));
			// byte[] scp02KeyData = generateKeyData(keyValue, checkValue,
			// cms2acParam);
			byte[] scp02KeyData = generateKeyData(keyValue, checkValue);
			keyData = contactArray(keyData, scp02KeyData);
		}
		return keyData;
	}

	public byte[] generateKeyData(byte[] keyValue, byte[] checkValue, Cms2acParam cms2acParam) {
		byte[] keyData = intToHexBytes(0x80, 1);

		byte[] keyLengthBytes = intToHexBytes(0x10, 1);
		keyData = contactArray(keyData, keyLengthBytes);

		byte[] encryptedKeyValue = scp02Service.encryptKey(keyValue, cms2acParam);
		keyData = contactArray(keyData, encryptedKeyValue);

		byte[] checkLengthBytes = intToHexBytes(0x03, 1);
		keyData = contactArray(keyData, checkLengthBytes);

		keyData = contactArray(keyData, checkValue);

		return keyData;
	}

	public byte[] generateKeyData(byte[] encKeyValue, byte[] checkValue) {
		byte[] keyData = intToHexBytes(0x80, 1);

		byte[] keyLengthBytes = intToHexBytes(0x10, 1);
		keyData = contactArray(keyData, keyLengthBytes);

		keyData = contactArray(keyData, encKeyValue);

		byte[] checkLengthBytes = intToHexBytes(0x03, 1);
		keyData = contactArray(keyData, checkLengthBytes);

		keyData = contactArray(keyData, checkValue);

		return keyData;
	}

	/**
	 * @deprecated
	 * @param cms2acParam
	 */
	public void updateCardSdKey(Cms2acParam cms2acParam) {
		String cardNo = cms2acParam.getLocalTransaction().getCardNo();
		SecurityDomain updateSd = securityDomainManager.getByAid(cms2acParam.getLocalTransaction().getAid());
		String aid = updateSd.getAid();
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, aid);
		List<CardSdScp02Key> oldValidCardSdKeys = cardSecurityDomain.getValidCardSdScp02Keys();

		List<KeyProfile> scp02UpdateKeys = updateSd.getScp02UpdateKeys();
		for (CardSdScp02Key oldCardSdScp02Key : oldValidCardSdKeys) {
			oldCardSdScp02Key.setStatus(CardSdScp02Key.INVALID_STATUS);
		}
		for (KeyProfile updateKey : scp02UpdateKeys) {
			CardSdScp02Key newCardSdScp02Key = new CardSdScp02Key();
			newCardSdScp02Key.setCardSecurityDomain(cardSecurityDomain);
			newCardSdScp02Key.setKeyProfile(updateKey);
			newCardSdScp02Key.setStatus(CardSdScp02Key.VALID_STATUS);
			newCardSdScp02Key.setGenerateTime(Calendar.getInstance());

			cardSecurityDomain.getCardSdScp02Keys().add(newCardSdScp02Key);
		}
		cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);
	}

	public void contactCardSdScp02Key(CardSecurityDomain cardSecurityDomain, KeyProfile keyProfile) {
		CardSdScp02Key cardSdScp02Key = cardSdScp02KeyManager.getByCardSdKeyProfile(cardSecurityDomain, keyProfile);
		if (cardSdScp02Key == null) {
			cardSdScp02Key = new CardSdScp02Key();
			cardSdScp02Key.setCardSecurityDomain(cardSecurityDomain);
			cardSdScp02Key.setKeyProfile(keyProfile);
			cardSdScp02Key.setStatus(CardSdScp02Key.VALID_STATUS);
			cardSdScp02Key.setGenerateTime(Calendar.getInstance());

			cardSecurityDomain.getCardSdScp02Keys().add(cardSdScp02Key);
		}
	}

	public void destroyCardSdScp02Key(CardSecurityDomain cardSecurityDomain, KeyProfile keyProfile) {
		CardSdScp02Key cardSdScp02Key = cardSdScp02KeyManager.getByCardSdKeyProfile(cardSecurityDomain, keyProfile);
		if (cardSdScp02Key != null) {
			cardSdScp02Key.setStatus(CardSdScp02Key.INVALID_STATUS);
		}
	}
}

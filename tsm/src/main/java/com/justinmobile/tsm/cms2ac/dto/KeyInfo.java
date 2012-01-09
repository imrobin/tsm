package com.justinmobile.tsm.cms2ac.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.justinmobile.core.utils.ConvertUtils;

public class KeyInfo {

	/** 密钥版本 */
	private Integer keyVersion;

	/** 密钥索引 <br/> */
	private Integer keyIndex;

	/** 密钥类型 */
	private Integer keyType;

	/** 密钥标识 */
	private Integer keyId;

	/** 算法标识 */
	private Integer agl;

	/** 分散因子 */
	private List<byte[]> dispatchFactors = new ArrayList<byte[]>(3);

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(String keyVersion) {
		this.keyVersion = ConvertUtils.hexString2Int(keyVersion);
	}

	public Integer getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(String keyIndex) {
		this.keyIndex = ConvertUtils.hexString2Int(keyIndex);
	}

	public Integer getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = ConvertUtils.hexString2Int(keyType);
	}

	public Integer getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = ConvertUtils.hexString2Int(keyId);
	}

	public Integer getAgl() {
		return agl;
	}

	public void setAgl(String agl) {
		this.agl = ConvertUtils.hexString2Int(agl);
	}

	public void addDispatchFactor(String dispatchFactor) {
		dispatchFactors.add(ConvertUtils.hexString2ByteArray(StringUtils.leftPad(dispatchFactor, 16, '0')));
	}
}
package com.justinmobile.tsm.cms2ac.domain;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.subArray;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.utils.ConvertUtils;

@Entity
@Table(name = "APDU_COMMAND")
public class ApduCommand extends AbstractEntity {

	private static final long serialVersionUID = -1363790325L;

	public static final int CMD_TYPE_ONE = 1;

	public static final int CMD_TYPE_TWO = 2;

	public static final int CMD_TYPE_THREE = 3;

	public static final int CMD_TYPE_FOUR = 4;

	public static final int CMD_TYPE_FIVE = 5;

	public static final int CMD_TYPE_SIX = 6;

	/** 主键 */
	private Long id;

	/**  */
	private Cms2acParam cms2acParam;

	/** 批次号 */
	private Integer batchNo;

	/** 索引 */
	private Integer apduIndex;

	/** 报文体 */
	private String rawHex;

	/** 安全报文 */
	private String securityHex;

	/** 初始向量 */
	private String icv;

	/** 是否是第一对 */
	private Boolean firstPair = false;

	/** 标识command和result */
	private String flag;

	/** CMAC */
	private String cmac;

	private byte cla;

	private byte ins;

	private byte p1;

	private byte p2;

	private byte lc;

	private byte[] data = new byte[0];

	private byte le;

	private int type;

	public ApduCommand() {

	}

	public byte[] toByteArray() {
		if (StringUtils.isNotEmpty(this.securityHex)) {
			return ConvertUtils.hexString2ByteArray(this.securityHex);
		}
		if (StringUtils.isNotEmpty(this.rawHex) && this.cla == 0) {
			return ConvertUtils.hexString2ByteArray(this.rawHex);
		}

		byte[] cmdHeader = new byte[] { cla, ins, p1, p2 };
		byte[] cmdBody = null;
		switch (type) {
		case CMD_TYPE_ONE:
			cmdBody = new byte[] { 0x00 };
			break;
		case CMD_TYPE_TWO:
			cmdBody = new byte[] { le };
			break;
		case CMD_TYPE_THREE:
			cmdBody = contactArray(new byte[] { lc }, data);
			break;
		case CMD_TYPE_FOUR:
			cmdBody = contactArray(new byte[] { lc }, data);
			cmdBody = contactArray(cmdBody, new byte[] { le });
			break;
		case CMD_TYPE_FIVE:
			// le为00，但不下发
			cmdBody = contactArray(new byte[] { lc }, data);
			break;
		case CMD_TYPE_SIX:
			// le为00，但不下发
			cmdBody = new byte[] { (byte) 0x00 };
			break;
		default:
			throw new IllegalStateException();
		}
		return contactArray(cmdHeader, cmdBody);
	}

	public byte[] noLeByteArray() {
		byte[] cmdHeader = new byte[] { cla, ins, p1, p2 };
		byte[] cmdBody = null;
		switch (type) {
		case CMD_TYPE_ONE:
		case CMD_TYPE_TWO:
			cmdBody = new byte[0];
			break;
		case CMD_TYPE_THREE:
		case CMD_TYPE_FOUR:
		case CMD_TYPE_FIVE:
			cmdBody = contactArray(new byte[] { lc }, data);
			break;
		default:
			throw new IllegalStateException();
		}
		return contactArray(cmdHeader, cmdBody);
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APDU_COMMAND") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "CM2AC_PARAM_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Cms2acParam getCms2acParam() {
		return cms2acParam;
	}

	public void setCms2acParam(Cms2acParam cms2acParam) {
		this.cms2acParam = cms2acParam;
	}

	public Integer getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	public Integer getApduIndex() {
		return apduIndex;
	}

	public void setApduIndex(Integer apduIndex) {
		this.apduIndex = apduIndex;
	}

	@Transient
	public Integer getIndex() {
		return apduIndex;
	}

	public void setIndex(Integer apduIndex) {
		this.apduIndex = apduIndex;
	}

	public String getRawHex() {
		return rawHex;
	}

	public void setRawHex(String rawHex) {
		this.rawHex = rawHex;
	}

	public String getSecurityHex() {
		return securityHex;
	}

	public void setSecurityHex(String securityHex) {
		this.securityHex = securityHex;
	}

	public String getIcv() {
		return icv;
	}

	public void setIcv(String icv) {
		this.icv = icv;
	}

	@Column(name = "IS_FIRST_PAIR")
	public Boolean getFirstPair() {
		return firstPair;
	}

	public void setFirstPair(Boolean firstPair) {
		this.firstPair = firstPair;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCmac() {
		return cmac;
	}

	public void setCmac(String cmac) {
		this.cmac = cmac;
	}

	@Transient
	public byte getCla() {
		return cla;
	}

	public void setCla(byte cla) {
		this.cla = cla;
	}

	@Transient
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Transient
	public byte getIns() {
		return ins;
	}

	public void setIns(byte ins) {
		this.ins = ins;
	}

	@Transient
	public byte getLc() {
		return lc;
	}

	public void setLc(byte lc) {
		this.lc = lc;
	}

	@Transient
	public byte getLe() {
		return le;
	}

	public void setLe(byte le) {
		this.le = le;
	}

	@Transient
	public byte getP1() {
		return p1;
	}

	public void setP1(byte p1) {
		this.p1 = p1;
	}

	@Transient
	public byte getP2() {
		return p2;
	}

	public void setP2(byte p2) {
		this.p2 = p2;
	}

	@Transient
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Transient
	public int getLength() {
		return this.toByteArray().length;
	}

	@Transient
	public byte[] getRawData() {
		if (StringUtils.isEmpty(this.rawHex)) {
			throw new IllegalStateException("rawHex should not be emty now!");
		} else {
			byte[] rawApduCmd = ConvertUtils.hexString2ByteArray(this.rawHex);
			if (rawApduCmd.length < 4) {
				throw new IllegalStateException("rawHex should not be longger than 4 byte now!");
			} else if (rawApduCmd.length < 6) {
				return new byte[0];
			} else {
				byte[] rawLcBytes = subArray(rawApduCmd, 4, 5);
				int rawLc = binaryToInt(rawLcBytes);
				return subArray(rawApduCmd, 5, 5 + rawLc);
			}

		}
	}
}
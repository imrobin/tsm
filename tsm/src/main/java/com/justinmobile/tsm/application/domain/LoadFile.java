package com.justinmobile.tsm.application.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.HexUtils;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "LOAD_FILE")
public class LoadFile extends AbstractEntity {

	private static final long serialVersionUID = 1909829538L;

	/** 1：共享的 */
	public static final int FLAG_SHARED = 1;

	/** 0：不共享的 */
	public static final int FLAG_EXCLUSIVE = 0;

	/** 文件类型：1-CMS2AC加载文件 */
	public static final int TYPE_CMS2AC_FILE = 1;

	/** 文件类型：2-CMS2AC扩展文件 */
	public static final int TYPE_CMS2AC_EXTENSION = 2;

	/** 文件类型：3-MIFARE文件 */
	public static final int TYPE_MIFARE_FILE = 3;

	/** 主键 */
	private Long id;

	/** 加载文件AID */
	private String aid;

	/** 加载文件名 */
	private String name;

	/** 备注 */
	private String comments;

	/** 加载文件所属SP */
	private SpBaseInfo sp;

	/**
	 * 加载文件所属安全域
	 */
	private SecurityDomain sd;

	/**
	 * 加载文件所属安全域模式 <br/>
	 * 
	 * @see SecurityDomain#model
	 */
	@ResourcesFormat(key = "sd.type")
	private Integer sdModel;

	/**
	 * 加载文件是否共享 <br/>
	 * 0-不共享 <br/>
	 * 1-共享
	 */
	private Integer shareFlag;

	/**
	 * 文件的类型<br/>
	 * 1-cms2ac的加载文件<br/>
	 * 2-cms2ac的扩展文件<br/>
	 * 3-mifare文件
	 */
	private Integer type;

	/** 版本信息 */
	private List<LoadFileVersion> versions = new ArrayList<LoadFileVersion>();

	// TODO 以下字段不建议使用
	/** 厂商 */
	private String manufacturer;

	private String currentVersion;

	@OneToMany(mappedBy = "loadFile", fetch = FetchType.LAZY)
	@OrderBy("versionNo DESC")
	@Cascade({ CascadeType.ALL })
	public List<LoadFileVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<LoadFileVersion> versions) {
		this.versions = versions;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SP_ID")
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SD_ID")
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	public SecurityDomain getSd() {
		return sd;
	}

	public void setSd(SecurityDomain sd) {
		this.sd = sd;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_LOAD_FILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "LOAD_FILE_AID")
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	@Column(name = "FILE_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getSdModel() {
		return sdModel;
	}

	public void setSdModel(Integer sdModel) {
		this.sdModel = sdModel;
	}

	public Integer getShareFlag() {
		return shareFlag;
	}

	public void setShareFlag(Integer shareFlag) {
		this.shareFlag = shareFlag;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 为当前加载文件添加一个版本信息，完成双向关联
	 * 
	 * @param version
	 *            版本信息
	 */
	public void addLoadFileVersion(LoadFileVersion version) {
		versions.add(version);
		version.setLoadFile(this);
	}

	public LoadFileVersion createNewVersion(LoadFile loadFile, String versionNo) {
		if (StringUtils.isBlank(versionNo)) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_NO_BLANK);
		}

		LoadFileVersion version = new LoadFileVersion();
		version.setVersionNo(versionNo);

		versions.add(version);
		version.setLoadFile(this);

		return version;
	}

	/**
	 * 验证aid是否符合规范要求
	 * 
	 * @throws PlatformErrorCode.APPLICATION_AID_NOT_EXIST
	 *             AID不存在
	 * @throws PlatformErrorCode.APPLICATION_AID_SHORTER
	 *             AID长度小于规范要求
	 * @throws PlatformErrorCode.APPLICATION_AID_LONGER
	 *             AID长度大于规范要求
	 * @throws PlatformErrorCode.SP_RID_DISCARD
	 *             RID不匹配
	 */
	public void validateAid() {
		if (null == this.aid) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_NOT_EXIST);
		}

		HexUtils.validate(this.aid);

		if (10 > this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_SHORTER);
		}

		if (32 < this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_AID_LONGER);
		}

		String rid = this.aid.substring(0, 10);
		if (!rid.equals(this.sp.getRid())) {
			throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
		}
	}

	public void fomateField() {
		this.aid = aid.toUpperCase();
	}

	public void removeLoadFileVersion(LoadFileVersion loadFileVersion) {
		this.versions.remove(loadFileVersion);
	}
}
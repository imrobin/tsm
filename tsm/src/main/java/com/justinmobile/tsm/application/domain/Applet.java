package com.justinmobile.tsm.application.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.HexUtils;

@Entity
@Table(name = "APPLET")
public class Applet extends AbstractEntity {

	private static final long serialVersionUID = 1967772794L;

	/** 主键 */
	private Long id;

	/** APPLET AID */
	private String aid;

	/** APPlET的名称 */
	private String name;

	/** 安装参数 */
	private String installParams;

	/** 安装次序 */
	private Integer orderNo;

	/** 权限 */
	private Integer privilege;

	/** 应用占用的不可变空间 */
	private Long noneVolatileSpace;

	/** 应用占用的可变空间 */
	private Integer volatileSpace;

	/** 所属模块 */
	private LoadModule loadModule;

	/** 所属应用 */
	private ApplicationVersion applicationVersion;

	// TODO 以下字段不建议使用
	private String mainAppletAid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public ApplicationVersion getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAD_MODULE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public LoadModule getLoadModule() {
		return loadModule;
	}

	public void setLoadModule(LoadModule loadModule) {
		this.loadModule = loadModule;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLET") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "APPLET_AID")
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	@Column(name = "APPLET_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstallParams() {
		return installParams;
	}

	public void setInstallParams(String installParams) {
		this.installParams = installParams;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Integer privilege) {
		this.privilege = privilege;
	}

	public Long getNoneVolatileSpace() {
		return noneVolatileSpace;
	}

	public void setNoneVolatileSpace(Long noneVolatileSpace) {
		this.noneVolatileSpace = noneVolatileSpace;
	}

	public Integer getVolatileSpace() {
		return volatileSpace;
	}

	public void setVolatileSpace(Integer volatileSpace) {
		this.volatileSpace = volatileSpace;
	}

	public String getMainAppletAid() {
		return mainAppletAid;
	}

	public void setMainAppletAid(String mainAppletAid) {
		this.mainAppletAid = mainAppletAid;
	}

	/**
	 * 为实例指派所属的应用版本，完成双向关联
	 * 
	 * @param applicationVersion
	 *            所属的应用版本
	 */
	public void assignApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
		if (null != applicationVersion) {
			applicationVersion.addApplet(this);
		}
	}

	/**
	 * 为实例指派所属的模块，完成双向关联
	 * 
	 * @param applicationVersion
	 *            所属的应用版本
	 */
	public void assignLoadModule(LoadModule loadModule) {
		this.loadModule = loadModule;
		if (null != loadModule) {
			loadModule.addApplet(this);
		}
	}

	/**
	 * 移除应用版本对实例的使用关系
	 */
	public void unassignApplicationVersion() {
		if (null != this.applicationVersion) {
			this.applicationVersion.removeApplet(this);
		}
	}

	/**
	 * 移除模块对实例的关系
	 */
	public void unassignLoadModule() {
		if (null != loadModule) {
			this.loadModule.removeApplet(this);
		}
	}

	@Transient
	public Space getSpaceInfo() {
		Space spaceInfo = new Space();

		if (StringUtils.isNotBlank(installParams)) {
			AppletInstallParams installParams = AppletInstallParams.parse(this.installParams);

			long nvm = installParams.getNonVolatileDateSpace();
			int ram = installParams.getVolatileDateSpace();

			spaceInfo.setNvm(nvm);
			spaceInfo.setRam(ram);
		}

		return spaceInfo;
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
			throw new PlatformException(PlatformErrorCode.APPLET_AID_NOT_EXIST);
		}

		HexUtils.validate(this.aid);

		if (10 > this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.APPLET_AID_SHORTER);
		}

		if (32 < this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.APPLET_AID_LONGER);
		}

		String rid = this.aid.substring(0, 10);
		if (!rid.equals(this.applicationVersion.getApplication().getSp().getRid())) {
			throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
		}
	}

	public void formatFiled() {
		this.aid = this.aid.toUpperCase();
		this.installParams = this.installParams.toUpperCase();
	}

	/**
	 * 验证安装参数
	 * 
	 * @throws APPLET_INSTALL_PARAMS_ERROR
	 *             安装参数不存在或不符合cms2ac规范
	 */
	public void validateInstallParams() {
		if (StringUtils.isBlank(this.installParams)) {
			throw new PlatformException(PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR);
		}
		try {
			this.getSpaceInfo();
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.APPLET_INSTALL_PARAMS_ERROR);
		}
	}

	/**
	 * 获取实例所属安全域
	 * 
	 * @return 实例所属安全域
	 */
	@Transient
	public SecurityDomain getSd() {
		return applicationVersion.getApplication().getSd();
	}
}
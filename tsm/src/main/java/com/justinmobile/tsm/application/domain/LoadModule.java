package com.justinmobile.tsm.application.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.HexUtils;

@Entity
@Table(name = "LOAD_MODULE")
public class LoadModule extends AbstractEntity {

	private static final long serialVersionUID = 1600876306L;

	/** 主键 */
	private Long id;

	/** 模块AID */
	private String aid;

	/** 模块名 */
	private String name;

	/** 所对应的加载文件版本 */
	private LoadFileVersion loadFileVersion;

	/** 备注 */
	private String comments;

	/** 从模块创建的实例 */
	private Set<Applet> applets = new HashSet<Applet>();

	// TODO 以下字段不建议使用
	/** 状态 0:有效 1:无效 */
	private Long status;

	private Long mainModule;

	private Long appSign;

	private Long noneVolatileSpace;

	private Long volatileSpace;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAD_MODULE_ID")
	@Cascade(value = { CascadeType.ALL })
	public Set<Applet> getApplets() {
		return applets;
	}

	public void setApplets(Set<Applet> applets) {
		this.applets = applets;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAD_FILE_VERSION_ID")
	@Cascade(value = { CascadeType.SAVE_UPDATE })
	public LoadFileVersion getLoadFileVersion() {
		return loadFileVersion;
	}

	public void setLoadFileVersion(LoadFileVersion loadFileVersion) {
		this.loadFileVersion = loadFileVersion;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_LOAD_MODULE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "LOAD_MODULE_AID")
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	@Column(name = "LOAD_MODULE_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getMainModule() {
		return mainModule;
	}

	public void setMainModule(Long mainModule) {
		this.mainModule = mainModule;
	}

	public Long getAppSign() {
		return appSign;
	}

	public void setAppSign(Long appSign) {
		this.appSign = appSign;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getNoneVolatileSpace() {
		return noneVolatileSpace;
	}

	public void setNoneVolatileSpace(Long noneVolatileSpace) {
		this.noneVolatileSpace = noneVolatileSpace;
	}

	public Long getVolatileSpace() {
		return volatileSpace;
	}

	public void setVolatileSpace(Long volatileSpace) {
		this.volatileSpace = volatileSpace;
	}

	/**
	 * 添加一个从模块生成的实例，完成双向关联
	 * 
	 * @param applet
	 *            从模块生成的实例
	 */
	public void addApplet(Applet applet) {
		this.applets.add(applet);
		applet.setLoadModule(this);
	}

	/**
	 * 移除一个从模块生成的实例
	 * 
	 * @param applet
	 *            待移出的实例
	 */
	public void removeApplet(Applet applet) {
		this.applets.remove(applet);
	}

	/**
	 * 指派模块所属的加载文件版本，完成双向关联
	 * 
	 * @param loadFileVersion
	 *            被指派的加载文件版本
	 */
	public void assignLoadFileVersion(LoadFileVersion loadFileVersion) {
		if (null != loadFileVersion) {
			loadFileVersion.addLoadModule(this);
			this.loadFileVersion = loadFileVersion;
		}
	}

	/**
	 * 解除模块对加载文件版本的所属关系
	 */
	public void unassignLoadFileVersion() {
		if (null != this.loadFileVersion)
			loadFileVersion.removeLoadModule(this);
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
			throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_NOT_EXIST);
		}

		HexUtils.validate(this.aid);

		if (10 > this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_SHORTER);
		}

		if (32 < this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.LOAD_MODULE_AID_LONGER);
		}

		String rid = this.aid.substring(0, 10);
		if (!rid.equals(this.loadFileVersion.getLoadFile().getSp().getRid())) {
			throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
		}
	}

	public void fomateField() {
		this.aid = aid.toUpperCase();
	}
}
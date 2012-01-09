package com.justinmobile.tsm.application.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.card.domain.CardApplication;

@Entity
@Table(name = "APPLICATION_VERSION")
public class ApplicationVersion extends AbstractEntity {

	private static final long serialVersionUID = -1525952120L;

	/** 0-初始化 */
	public static final Integer STATUS_INIT = 0;

	/** 1-已上传，所有文件上传完成、实例定义完成、顺序设置完成 */
	public static final Integer STATUS_UPLOADED = 1;

	/** 2-已测试，测试完成，提交发布审核 */
	public static final Integer STATUS_TESTED = 2;

	/** 5-已审核，发布申请审核通过，可以进行卡批次关联和发布 */
	public static final Integer STATUS_AUDITED = 5;

	/** 3-已发布 ，应用可以被用户下载 */
	public static final Integer STATUS_PULISHED = 3;

	/** 4-已归档，归档申请审核通过 */
	public static final Integer STATUS_ARCHIVE = 4;

	/** 1-已定义 */
	@Deprecated
	public static final Integer STATUS_DEFINED = 1;

	/** 5-发布待审核 */
	@Deprecated
	public static final Integer STATUS_TO_BE_AUDITED = 5;

	// /** 6-发布已审核 */
	// @Deprecated
	// public static final Integer STATUS_AUDITED = 6;

	/** 主键 */
	private Long id;

	/** 所属应用 */
	private Application application;

	/** 版本号 */
	private String versionNo;

	/** 应用占用的不可变空间 */
	private Long nonVolatileSpace;

	/** 应用占用的可变空间 */
	private Integer volatileSpace;

	/**
	 * 应用版本对应的可用手机号
	 */
	private Set<SpecialMobile> speicalMobiles = Sets.newHashSet();
	/**
	 * 状态<br/>
	 * 0-初始化<br/>
	 * 1-已定义<br/>
	 * 2-已测试<br/>
	 * 3-已发布 <br/>
	 * 4-已归档<br/>
	 * 5-发布待审核<br/>
	 * 6-发布已审核
	 */
	@ResourcesFormat(key = "appver.status")
	private Integer status = STATUS_INIT;

	/** 发布时间 */
	@DateFormat
	private Calendar publishDate;

	/** 归档时间 */
	private Calendar archiveDate;

	/** 安装顺序 */
	private List<Applet> installOrder = Lists.newArrayList();

	/** 当前应用版本所使用的加载文件 */
	private Set<ApplicationLoadFile> applicationLoadFiles = new HashSet<ApplicationLoadFile>();

	private List<CardApplication> cardApplication = Lists.newArrayList();

	private Set<ApplicationClientInfo> clients = new HashSet<ApplicationClientInfo>();

	/** 应用所使用的实例 */
	private Set<Applet> applets = new HashSet<Applet>();

	/** 已下载该应用的用户数量 */
	private int downloadUserAmount;

	/** 未下载该应用的用户数量 */
	private int undownloadUserAmount;

	@ManyToMany(mappedBy = "applicationVersions")
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<ApplicationClientInfo> getClients() {
		return clients;
	}

	public void setClients(Set<ApplicationClientInfo> clients) {
		this.clients = clients;
	}

	@OneToMany
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<Applet> getApplets() {
		return applets;
	}

	public void setApplets(Set<Applet> applets) {
		this.applets = applets;
	}

	@OneToMany
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade({ CascadeType.ALL })
	@LazyToOne(LazyToOneOption.PROXY)
	public List<CardApplication> getCardApplication() {
		return cardApplication;
	}

	public void setCardApplication(List<CardApplication> cardApplication) {
		this.cardApplication = cardApplication;
	}

	@OneToMany
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<ApplicationLoadFile> getApplicationLoadFiles() {
		return applicationLoadFiles;
	}

	public void setApplicationLoadFiles(Set<ApplicationLoadFile> applicationLoadFiles) {
		this.applicationLoadFiles = applicationLoadFiles;
	}

	@OneToMany(mappedBy = "applicationVersion")
	@Cascade(value = CascadeType.ALL)
	@LazyToOne(LazyToOneOption.PROXY)
	@OrderBy("orderNo ASC")
	public List<Applet> getInstallOrder() {
		return installOrder;
	}

	public void setInstallOrder(List<Applet> installOrder) {
		this.installOrder = installOrder;
	}

	@ManyToOne
	@JoinColumn(name = "APPLICATION_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_VERSION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public Long getNonVolatileSpace() {
		return nonVolatileSpace;
	}

	public void setNonVolatileSpace(Long nonVolatileSpace) {
		this.nonVolatileSpace = nonVolatileSpace;
	}

	public Integer getVolatileSpace() {
		return volatileSpace;
	}

	public void setVolatileSpace(Integer volatileSpace) {
		this.volatileSpace = volatileSpace;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

	public Calendar getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Calendar archiveDate) {
		this.archiveDate = archiveDate;
	}

	/**
	 * 为当前应用版本添加一个所使用的实例，并完成双向关联
	 * 
	 * @param applet
	 *            所使用的实例
	 */
	public void addApplet(Applet applet) {
		this.applets.add(applet);
		applet.setApplicationVersion(this);
	}

	/**
	 * 为当前应用版本移出一个所使用的实例
	 * 
	 * @param applet
	 *            被移出的实例
	 */
	public void removeApplet(Applet applet) {
		this.applets.remove(applet);
	}

	/**
	 * 设置应用所使用的空间
	 * 
	 * @param spaceInfo
	 *            空间信息
	 */
	public void setSpaceInfo(Space spaceInfo) {
		this.nonVolatileSpace = spaceInfo.getNvm();
		this.volatileSpace = spaceInfo.getRam();
	}

	/**
	 * 计算应用版本占用的空间。<br/>
	 * 计算完成后修改对象的nonVolatileSpace和volatileSpace字段并返回计算结果
	 * 
	 * @return 计算应用版本占用的空信息
	 */
	public Space calcSpaceInfo() {
		Space space = new Space();

		// 计算加载文件占用的空间
		for (ApplicationLoadFile appLf : this.applicationLoadFiles) {
			space.plus(appLf.getLoadFileVersion().getSpaceInfo());
		}

		// 计算实例占用的空间
		for (Applet applet : this.applets) {
			space.plus(applet.getSpaceInfo());
		}

		setSpaceInfo(space);

		return space;
	}

	@Transient
	public Applet getApplet(int index) {
		Set<Applet> applets = this.getApplets();
		if (CollectionUtils.isNotEmpty(applets)) {
			for (Applet applet : applets) {
				if (index == applet.getOrderNo()) {
					return applet;
				}
			}
		}
		return null;
	}

	/**
	 * 指派一个所属应用，建立双向关联
	 * 
	 * @param application
	 *            所属应用
	 */
	public void assignApplication(Application application) {
		application.addVersion(this);
	}

	/**
	 * 获取当前应用版本所占用的空间信息
	 * 
	 * @return 空间信息
	 */
	@Transient
	public Space getSpaceInfo() {
		Space spaceInfo = new Space();
		spaceInfo.setNvm(nonVolatileSpace);
		spaceInfo.setRam(volatileSpace);

		return spaceInfo;
	}

	/**
	 * 获取当前应用版本的加载文件所占用的空间信息
	 * 
	 * @return 空间信息
	 */
	@Transient
	public Space getLoadFileSpaceInfo() {
		Space spaceInfo = new Space();

		for (ApplicationLoadFile applicationLoadFile : this.applicationLoadFiles) {
			spaceInfo.plus(applicationLoadFile.getLoadFileVersion().getSpaceInfo());
		}

		return spaceInfo;
	}

	/**
	 * 获取当前应用版本的实例所占用的空间信息
	 * 
	 * @return 空间信息
	 */
	@Transient
	public Space getAppletSpaceInfo() {
		Space spaceInfo = new Space();

		for (Applet applet : applets) {
			spaceInfo.plus(applet.getSpaceInfo());
		}

		return spaceInfo;
	}

	@ManyToMany(mappedBy = "applicationVersions", targetEntity = SpecialMobile.class)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SpecialMobile> getSpeicalMobiles() {
		return speicalMobiles;
	}

	public void setSpeicalMobiles(Set<SpecialMobile> speicalMobiles) {
		this.speicalMobiles = speicalMobiles;
	}

	/**
	 * 手机号是否在应用版本的允许使用的范围内？<br/>
	 * 如果应用版本没有可使用手机号的限制，则所有手机号可以使用<br/>
	 * 否则，手机号必须在允许的手机号列表中才能使用
	 * 
	 * @param mobileNo
	 *            手机号
	 * @return true-在允许使用<br/>
	 *         false-不允许使用
	 */
	@Transient
	public boolean isMobileNoLimite(String mobileNo) {
		if (0 != this.speicalMobiles.size()) {
			for (SpecialMobile specialMobile : this.speicalMobiles) {
				if (specialMobile.getMobileNo().equals(mobileNo)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}

	}

	@Transient
	public ApplicationClientInfo getClient(String sysRequirment) {
		Set<ApplicationClientInfo> clients = this.getClients();
		if (CollectionUtils.isNotEmpty(clients)) {
			for (ApplicationClientInfo applicationClientInfo : clients) {
				if (StringUtils.startsWithIgnoreCase(sysRequirment, applicationClientInfo.getSysRequirment())
						&& applicationClientInfo.getStatus() == ApplicationClientInfo.STATUS_RELEASE.intValue()) {
					return applicationClientInfo;
				}
			}
		}
		return null;
	}

	@Transient
	public int getDownloadUserAmount() {
		return downloadUserAmount;
	}

	public void setDownloadUserAmount(int downloadUserAmount) {
		this.downloadUserAmount = downloadUserAmount;
	}

	@Transient
	public int getUndownloadUserAmount() {
		return undownloadUserAmount;
	}

	public void setUndownloadUserAmount(int undownloadUserAmount) {
		this.undownloadUserAmount = undownloadUserAmount;
	}

	/**
	 * 获取应用版本在使用了的安全域上的空间分布情况<br/>
	 * 如果有多个卡上实体使用了相同的安全域，所获得的空间是所有实体所需空间的和
	 * 
	 * @return
	 */
	@Transient
	public Map<SecurityDomain, Space> getSpaceDistributionOnSecurityDomain() {
		Map<SecurityDomain, Space> distribution = new HashMap<SecurityDomain, Space>();

		// 添加实例所占用的空间
		distribution.put(application.getSd(), getAppletSpaceInfo());

		// 添加文件的空间分布情况
		for (ApplicationLoadFile applicationLoadFile : applicationLoadFiles) {
			LoadFileVersion loadFileVersion = applicationLoadFile.getLoadFileVersion();
			SecurityDomain securityDomain = loadFileVersion.getSd();// 当前文件版本所属安全域
			Space needSpace = loadFileVersion.getSpaceInfo();// 当前文件版本所需要的空间

			Space usedSpace = distribution.get(securityDomain);// 当前文件版本已使用安全域的空间
			if (null == usedSpace) {// 当前文件版本所属安全域已使用的空间为null表示当前文件版本是第一个使用该安全域的卡上实体，所以需要的安全域空间即为当前文件版本所需要的空间
				distribution.put(securityDomain, needSpace);
			} else {// 否则，应用版本已有其他卡上实体试用了当前文件版本所属安全域，所以需要的安全域空间应为当前文件版本已使用安全域的空间加上当前文件版本所需要的空间
				distribution.put(securityDomain, usedSpace.plus(needSpace));
			}
		}

		return distribution;
	}
}
package com.justinmobile.tsm.application.domain;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.card.domain.CardClient;

@Entity
@Table(name = "APPLICATION_CLIENT_INFO")
public class ApplicationClientInfo extends AbstractEntity {

	private static final long serialVersionUID = 66960489L;

	/** 业务类型-1，应用管理器 */
	public static final Integer BUSI_TYPE_APPLICATION_MANAGER = 1;

	/** 业务类型-2，应用客户端 */
	public static final Integer BUSI_TYPE_APPLICATION_CLIENT = 2;

	/** 文件类型-jad */
	public static final String FILE_TYPE_JAD = "jad";

	/** 文件类型-jar */
	public static final String FILE_TYPE_JAR = "jar";

	/** 文件类型-apk */
	public static final String FILE_TYPE_APK = "apk";

	/** 未发布状态 */
	public static final Integer STATUS_UNRELEASE = 1;

	/** 已发布状态 */
	public static final Integer STATUS_RELEASE = 2;

	/** 基于Android系统 */
	public static final String SYS_TYPE_Android = "Android";

	/** 基于iOS系统 */
	public static final String SYS_TYPE_IOS = "iOS";

	/** 基于WindowsMobile系统 */
	public static final String SYS_TYPE_WindowsPhone = "WP";

	/** 基于J2ME */
	public static final String SYS_TYPE_J2ME = "j2me";

	/** 主键 */
	private Long id;

	/** 客户端名称 */
	private String name;

	/**
	 * 业务类型 <br/>
	 * 1，应用管理器<br/>
	 * 2，应用客户端
	 */
	private Integer busiType;

	/**
	 * 系统类型<br/>
	 * OS，基于手机操作系统<br/>
	 * J2ME，基于J2ME
	 */
	private String sysType;

	/** 客户端运行的最低J2ME版本或操作系统版本 */
	private String sysRequirment;

	/** 客户端版本 */
	private String version;

	/** 客户端URL */
	private String fileUrl;

	/** 客户端大小 */
	private Long size;

	/** 客户端入口类名称 */
	private String clientClassName;

	/** 客户端包名 */
	private String clientPackageName;

	/** 开发版本 */
	private Integer versionCode;

	/** 创建时间 */
	@DateFormat
	private Calendar createDate = Calendar.getInstance();

	/** 客户端所支持的应用版本 */
	private Set<ApplicationVersion> applicationVersions = new HashSet<ApplicationVersion>();
	
	private Set<CardClient> cardClients = new HashSet<CardClient>();

	/** 客户端的文件类型，使用文件的扩展名 */
	private String fileType;

	/** 文件在本地的绝对路径 */
	private String filePath;

	/** 图标 */
	private byte[] icon;

	/** 状态 */
	@ResourcesFormat(key = "applicationClient.status")
	private Integer status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CLIENT_APPLICTION_VERSION", joinColumns = { @JoinColumn(name = "CLIENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICTION_VERSION_ID") })
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public Set<ApplicationVersion> getApplicationVersions() {
		return applicationVersions;
	}

	public void setApplicationVersions(Set<ApplicationVersion> applicationVersions) {
		this.applicationVersions = applicationVersions;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_CLIENT_INFO") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBusiType() {
		return busiType;
	}

	public void setBusiType(Integer busiType) {
		this.busiType = busiType;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getSysRequirment() {
		return sysRequirment;
	}

	public void setSysRequirment(String sysRequirment) {
		this.sysRequirment = sysRequirment;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Column(name = "FILE_SIZE")
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getClientClassName() {
		return clientClassName;
	}

	public void setClientClassName(String clientClassName) {
		this.clientClassName = clientClassName;
	}

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public String getClientPackageName() {
		return clientPackageName;
	}

	public void setClientPackageName(String clientPackageName) {
		this.clientPackageName = clientPackageName;
	}

	public void addApplicationVersion(ApplicationVersion applicationVersion) {
		applicationVersions.add(applicationVersion);
	}

	@OneToMany(mappedBy = "client", orphanRemoval = true)
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<CardClient> getCardClients() {
		return cardClients;
	}

	public void setCardClients(Set<CardClient> cardClients) {
		this.cardClients = cardClients;
	}
	
	
}
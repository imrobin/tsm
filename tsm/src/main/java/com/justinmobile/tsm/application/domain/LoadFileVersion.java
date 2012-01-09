package com.justinmobile.tsm.application.domain;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.utils.CapFileUtils;

@Entity
@Table(name = "LOAD_FILE_VERSION")
public class LoadFileVersion extends AbstractEntity {

	private static final Logger log = LoggerFactory.getLogger(LoadFileVersion.class);

	private static final long serialVersionUID = -421295402L;

	private Long id;

	/** 所属加载文件 */
	private LoadFile loadFile;

	/** 软件版本号 */
	private String versionNo;

	/** HASH值 */
	private String hash;

	/** 加载参数 */
	private String loadParams;

	/** CAP文件 */
	private String capFileHex;

	/** CAP文件大小 */
	private Integer fileSize;

	/** 创建时间 */
	@DateFormat
	private Calendar createDate = Calendar.getInstance();

	private Set<LoadModule> loadModules = new HashSet<LoadModule>();

	/** 使用本加载文件的应用 */
	private Set<ApplicationLoadFile> applicationLoadFiles = new HashSet<ApplicationLoadFile>();

	/** 本加载文件直接依赖的加载文件 */
	private Set<LoadFileVersion> parents = new HashSet<LoadFileVersion>(0);

	/** 直接依赖于本加载文件的加载文件 */
	private Set<LoadFileVersion> children = new HashSet<LoadFileVersion>(0);

	// TODO 以下字段不建议使用
	/** 支持的java版本 */
	private String javaVersion;

	/** 支持的GP版本 */
	private String gpVersion;

	/** 支持的cms2ac版本 */
	private String cmsacVersion;

	@OneToMany(mappedBy = "loadFileVersion", fetch = FetchType.LAZY, orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	public Set<LoadModule> getLoadModules() {
		return loadModules;
	}

	public void setLoadModules(Set<LoadModule> loadModules) {
		this.loadModules = loadModules;
	}

	@ManyToMany
	@JoinTable(name = "LOAD_FILE_VERSION_DEPENDENCE", joinColumns = @JoinColumn(name = "CHILD_LOAD_FILE_VERSION_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PARENT_LOAD_FILE_VERSION_ID", referencedColumnName = "ID"))
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<LoadFileVersion> getParents() {
		return parents;
	}

	public void setParents(Set<LoadFileVersion> parents) {
		this.parents = parents;
	}

	@ManyToMany(mappedBy = "parents")
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<LoadFileVersion> getChildren() {
		return children;
	}

	public void setChildren(Set<LoadFileVersion> children) {
		this.children = children;
	}

	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "LOAD_FILE_VERSION_ID")
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<ApplicationLoadFile> getApplicationLoadFiles() {
		return applicationLoadFiles;
	}

	public void setApplicationLoadFiles(Set<ApplicationLoadFile> applicationLoadFiles) {
		this.applicationLoadFiles = applicationLoadFiles;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAD_FILE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public LoadFile getLoadFile() {
		return loadFile;
	}

	public void setLoadFile(LoadFile loadFile) {
		this.loadFile = loadFile;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_LOAD_FILE_VERSION") })
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getLoadParams() {
		return loadParams;
	}

	public void setLoadParams(String loadParams) {
		this.loadParams = loadParams;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getCapFileHex() {
		return capFileHex;
	}

	public void setCapFileHex(String capFileHex) {
		this.capFileHex = capFileHex;
	}

	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getGpVersion() {
		return gpVersion;
	}

	public void setGpVersion(String gpVersion) {
		this.gpVersion = gpVersion;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getCmsacVersion() {
		return cmsacVersion;
	}

	public void setCmsacVersion(String cmsacVersion) {
		this.cmsacVersion = cmsacVersion;
	}

	/**
	 * 添加使用此版本加载文件的应用版本
	 * 
	 * @param applicationVersion
	 *            应用版本
	 */
	public void addApplictionVersion(ApplicationVersion applicationVersion) {
		ApplicationLoadFile appLoadFile = new ApplicationLoadFile();
		appLoadFile.assignApplicationVersionAndLoadFileVersion(applicationVersion, this);
	}

	public void parseLoadFile(String tempDir, String tempFilePath) {
		byte[] bytes = CapFileUtils.paserLoadFile(tempDir, tempFilePath);
		this.fileSize = bytes.length;
		this.capFileHex = ConvertUtils.byteArray2HexString(bytes);
	}

	/**
	 * 添加一个模块，完成双向关联
	 * 
	 * @param loadModule
	 */
	public void addLoadModule(LoadModule loadModule) {
		this.loadModules.add(loadModule);
		loadModule.setLoadFileVersion(this);
	}

	/**
	 * 移除一个应用版本对加载文件版本的引入关系
	 * 
	 * @param applicationLoadFile
	 *            被移出的引入关系
	 */
	public void removeApplicationLoadFile(ApplicationLoadFile applicationLoadFile) {
		log.debug("\n" + "remove前: " + this.applicationLoadFiles.size() + "\n");
		this.applicationLoadFiles.remove(applicationLoadFile);
		log.debug("\n" + "remove后: " + this.applicationLoadFiles.size() + "\n");
	}

	public void replaceSet() {
		Set<ApplicationLoadFile> applicationLoadFiles = new HashSet<ApplicationLoadFile>(this.applicationLoadFiles.size());
		for (ApplicationLoadFile applicationLoadFile : this.applicationLoadFiles) {
			applicationLoadFiles.add(applicationLoadFile);
		}
		this.applicationLoadFiles = applicationLoadFiles;

		Set<LoadModule> loadModules = new HashSet<LoadModule>(this.loadModules.size());
		for (LoadModule loadModule : loadModules) {
			loadModules.add(loadModule);
		}
		this.loadModules = loadModules;
	}

	/**
	 * 移除一个模块
	 * 
	 * @param loadModule
	 *            被移除的模块
	 */
	public void removeLoadModule(LoadModule loadModule) {
		if (null != loadModule) {
			this.loadModules.remove(loadModule);
		}

	}

	/**
	 * 获取加载文件的空间信息
	 * 
	 * @return 空间信息
	 */
	@Transient
	public Space getSpaceInfo() {
		Space spaceInfo = new Space();

		if (StringUtils.isNotBlank(loadParams)) {
			LoadParams loadParams = LoadParams.parse(this.loadParams);

			long nvm = loadParams.getNonVolatileCodeSpace() + loadParams.getNonVolatileDateSpace();
			int ram = loadParams.getVolatileDateSpace();

			spaceInfo.setNvm(nvm);
			spaceInfo.setRam(ram);
		}

		return spaceInfo;
	}

	/**
	 * 为当前加载文件版本添加一个依赖加载文件版本，建立双向关联
	 * 
	 * @param parent
	 *            被依赖的加载文件版本
	 */
	public void addDenepency(LoadFileVersion parent) {
		this.parents.add(parent);
		parent.children.add(this);
	}

	/**
	 * 为当前加载文件版本移除一个依赖加载文件版本，移除双向关联
	 * 
	 * @param parent
	 *            被移除依赖的加载文件版本
	 */
	public void removeDenepency(LoadFileVersion parent) {
		this.parents.remove(parent);
		parent.children.remove(this);
	}

	/**
	 * 验证加载参数的合法性
	 * 
	 * @throws LOAD_FILE_LOAD_PARAMS_ERROR
	 *             加载参数不符合cms2ac规范要求
	 */
	public void validateLoadParams() {
		if (StringUtils.isBlank(this.loadParams)) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR);
		}
		try {
			this.getSpaceInfo();
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_LOAD_PARAMS_ERROR);
		}
	}

	/**
	 * 获取加载文件版本所属安全域
	 * 
	 * @return 加载文件版本所属安全域
	 */
	@Transient
	public SecurityDomain getSd() {
		return loadFile.getSd();
	}
}
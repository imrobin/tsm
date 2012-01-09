package com.justinmobile.tsm.application.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "APPLICATION_LOAD_FILE")
public class ApplicationLoadFile extends AbstractEntity {

	private static final long serialVersionUID = -524604206L;

	/** 主键 */
	private Long id;

	/** 下载次序 */
	private Integer downloadOrder;

	/** 删除次序 */
	private Integer deleteOrder;

	/** 所属应用版本 */
	private ApplicationVersion applicationVersion;

	/** 对应加载文件版本 */
	private LoadFileVersion loadFileVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public ApplicationVersion getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOAD_FILE_VERSION_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public LoadFileVersion getLoadFileVersion() {
		return loadFileVersion;
	}

	public void setLoadFileVersion(LoadFileVersion loadFileVersion) {
		this.loadFileVersion = loadFileVersion;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_LOAD_FILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDownloadOrder() {
		return downloadOrder;
	}

	public void setDownloadOrder(Integer downloadOrder) {
		this.downloadOrder = downloadOrder;
	}

	public Integer getDeleteOrder() {
		return deleteOrder;
	}

	public void setDeleteOrder(Integer deleteOrder) {
		this.deleteOrder = deleteOrder;
	}

	/**
	 * 对当前对象指派应用版本和加载文件版本，完成双向关联
	 * 
	 * @param applicationVersion
	 *            应用版本
	 * @param loadFileVersion
	 *            加载文件版本
	 */
	public void assignApplicationVersionAndLoadFileVersion(ApplicationVersion applicationVersion, LoadFileVersion loadFileVersion) {
		this.applicationVersion = applicationVersion;
		applicationVersion.getApplicationLoadFiles().add(this);

		this.loadFileVersion = loadFileVersion;
		loadFileVersion.getApplicationLoadFiles().add(this);
	}

	/**
	 * 解除应用版本和加载文件版本对此对象的关联
	 */
	public void unassignApplicationVersionAndLoadFileVersion() {
		if (null != this.applicationVersion) {
			this.applicationVersion.getApplicationLoadFiles().remove(this);
		}

		if (null != this.loadFileVersion) {
			this.loadFileVersion.getApplicationLoadFiles().remove(this);
		}
	}
}
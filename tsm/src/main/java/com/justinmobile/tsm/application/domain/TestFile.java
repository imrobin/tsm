package com.justinmobile.tsm.application.domain;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "APP_TEST_FILES")
public class TestFile extends AbstractEntity {
	
	/**  */
	private static final long serialVersionUID = -8541969888527746573L;
	
	private Long id;
	/** 文件名 */
	private String fileName;
	/** 文件路径(文件名) */
	private String filePath;
	/** 原始文件名 */
	private String originalName;
	/** 上传时间 */
	@DateFormat()
	private Calendar uploadDate;
	/** 序列号 */
	private Integer seqNum;
	/** 文件类型 */
	private Integer fileType;
	/**  所属的应用版本*/
	private ApplicationVersion appVer;
	/** 上传人 */
	private SpBaseInfo sp;
	/** 文件说明 */
	private String comments;
	
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence",parameters = {@Parameter(name = "sequence", value = "SEQ_TEST_APP_FILES")})
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Calendar getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Calendar uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Integer getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}
	public Integer getFileType() {
		return fileType;
	}
	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}
	
	@ManyToOne
	@JoinColumn(name = "APPVER_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationVersion getAppVer() {
		return appVer;
	}
	public void setAppVer(ApplicationVersion appVer) {
		this.appVer = appVer;
	}
	
	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}
	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	
}

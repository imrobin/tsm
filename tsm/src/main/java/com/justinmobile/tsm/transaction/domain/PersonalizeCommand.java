package com.justinmobile.tsm.transaction.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;

@Entity
@Table(name = "PERSONALIZE_COMMAND")
public class PersonalizeCommand extends AbstractEntity {

	private static final long serialVersionUID = -5929793281602883012L;

	public static final int END_FLAG_NOT_LAST = 1;

	public static final int END_FLAG_LAST = 0;

	private Long id;

	private String appAid;

	/**
	 * 1-写，2-读，3-删
	 */
	private Integer type;

	private Integer batch;

	private Integer cmdIndex;

	private Integer cmdLength;

	private String cmd;

	private String appletAid;

	private String cmdTemp;

	private Integer personalType;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PERSONALIZE_COMMAND") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppAid() {
		return appAid;
	}

	public void setAppAid(String appAid) {
		this.appAid = appAid;
	}

	public Integer getBatch() {
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}

	public Integer getCmdIndex() {
		return cmdIndex;
	}

	public void setCmdIndex(Integer cmdIndex) {
		this.cmdIndex = cmdIndex;
	}

	public Integer getCmdLength() {
		return cmdLength;
	}

	public void setCmdLength(Integer cmdLength) {
		this.cmdLength = cmdLength;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAppletAid() {
		return appletAid;
	}

	public void setAppletAid(String appletAid) {
		this.appletAid = appletAid;
	}

	@Column(name = "CMD_TMEP")
	public String getCmdTemp() {
		return cmdTemp;
	}

	public void setCmdTemp(String cmdTemp) {
		this.cmdTemp = cmdTemp;
	}

	public Personalization convertToWebDto() {
		Personalization personalization = new Personalization();
		personalization.setAid(appletAid);
		personalization.setFileContent(cmdTemp);
		return personalization;
	}

	public Integer getPersonalType() {
		return personalType;
	}

	public void setPersonalType(Integer personalType) {
		this.personalType = personalType;
	}
}

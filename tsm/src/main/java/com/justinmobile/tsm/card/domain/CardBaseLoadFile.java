package com.justinmobile.tsm.card.domain;

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
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Entity
@Table(name = "CARD_BASE_LOAD_FILE")
public class CardBaseLoadFile extends AbstractEntity{

	private static final long serialVersionUID = -376811325L;

	private Long id;

	private CardBaseInfo cardBaseInfo;

	private LoadFileVersion loadFileVersion;

	@ManyToOne
	@JoinColumn(name = "card_Base_Id")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public CardBaseInfo getCardBaseInfo() {
		return cardBaseInfo;
	}

	public void setCardBaseInfo(CardBaseInfo cardBaseInfo) {
		this.cardBaseInfo = cardBaseInfo;
	}

	@ManyToOne
	@JoinColumn(name = "load_File_Versionid_Id")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public LoadFileVersion getLoadFileVersion() {
		return loadFileVersion;
	}

	public void setLoadFileVersion(LoadFileVersion loadFileVersion) {
		this.loadFileVersion = loadFileVersion;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_BASE_LOAD_FILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
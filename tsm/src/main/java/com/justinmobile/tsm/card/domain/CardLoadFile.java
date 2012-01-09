package com.justinmobile.tsm.card.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.application.domain.LoadFileVersion;

@Entity
@Table(name = "CARD_LOAD_FILE")
public class CardLoadFile extends AbstractEntity {

	private static final long serialVersionUID = -1821675822L;

	/** 主键 */
	private Long id;

	/** 卡信息 */
	private CardInfo card;

	/** 加载文件版本 */
	private LoadFileVersion loadFileVersion;

	/** 是否已下载 */
	private Boolean downloaded;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "CARD_ID")
	public CardInfo getCard() {
		return card;
	}

	public void setCard(CardInfo card) {
		this.card = card;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "LOAD_FILE_VERSION_ID")
	public LoadFileVersion getLoadFileVersion() {
		return loadFileVersion;
	}

	public void setLoadFileVersion(LoadFileVersion loadFileVersion) {
		this.loadFileVersion = loadFileVersion;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_LOAD_FILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Boolean downloaded) {
		this.downloaded = downloaded;
	}

}
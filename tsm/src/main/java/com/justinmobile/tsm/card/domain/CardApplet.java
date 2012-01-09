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
import com.justinmobile.tsm.application.domain.Applet;

@Entity
@Table(name = "CARD_APPLET")
public class CardApplet extends AbstractEntity {

	private static final long serialVersionUID = 2124783530L;

	/** 主键 */
	private Long id;

	/** 卡信息 */
	private CardInfo card;

	/** applet */
	private Applet applet;

	/** 是否已安装 */
	private Integer installed;

	@ManyToOne
	@JoinColumn(name = "CARD_ID")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public CardInfo getCard() {
		return card;
	}

	public void setCard(CardInfo card) {
		this.card = card;
	}

	@ManyToOne
	@JoinColumn(name = "APPLET_ID")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public Applet getApplet() {
		return applet;
	}

	public void setApplet(Applet applet) {
		this.applet = applet;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_APPLET") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getInstalled() {
		return installed;
	}

	public void setInstalled(Integer installed) {
		this.installed = installed;
	}

}
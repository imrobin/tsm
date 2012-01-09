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
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;

@Entity
@Table(name = "CARD_CLIENT")
public class CardClient extends AbstractEntity {

	private static final long serialVersionUID = -503546936847501925L;

	/** 主键 */
	private Long id;

	/** 卡信息 */
	private CardInfo card;

	/** client */
	private ApplicationClientInfo client;

	@ManyToOne
	@JoinColumn(name = "CARD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardInfo getCard() {
		return card;
	}

	public void setCard(CardInfo card) {
		this.card = card;
	}

	@ManyToOne
	@JoinColumn(name = "CLIENT_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationClientInfo getClient() {
		return client;
	}

	public void setClient(ApplicationClientInfo client) {
		this.client = client;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_CLIENT") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}

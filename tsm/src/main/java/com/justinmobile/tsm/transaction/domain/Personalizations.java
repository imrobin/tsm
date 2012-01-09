package com.justinmobile.tsm.transaction.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "PERSONALIZATIONS")
public class Personalizations extends AbstractEntity {

	private static final long serialVersionUID = 7078988312085181281L;

	private Long id;

	private LocalTransaction localTransaction;

	private Integer currentPersonalizationIndex = 0;

	private List<Personalization> personalizations = new ArrayList<Personalization>();

	@ManyToOne
	@JoinColumn(name = "LOCAL_TRANSACTION_ID", referencedColumnName = "ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public LocalTransaction getLocalTransaction() {
		return localTransaction;
	}

	public void setLocalTransaction(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}

	@OneToMany(mappedBy = "personalizations")
	@Cascade(value = { CascadeType.ALL })
	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<Personalization> getPersonalizations() {
		return personalizations;
	}

	public void setPersonalizations(List<Personalization> personalizations) {
		this.personalizations = personalizations;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PERSONALIZATIONS") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCurrentPersonlizationIndex() {
		return currentPersonalizationIndex;
	}

	public void setCurrentPersonlizationIndex(Integer currentPersonlizationIndex) {
		this.currentPersonalizationIndex = currentPersonlizationIndex;
	}

	/**
	 * 向个人化指令列表中添加一个指令项，建立双向关联
	 * 
	 * @param personalization
	 *            个人化指令项
	 */
	public void addPersonalization(Personalization personalization) {
		personalizations.add(personalization);
		personalization.setPersonalizations(this);
	}

	/**
	 * 当前流程是否有待下发的个人化指令？
	 * 
	 * @return true-有个人化指令<br/>
	 *         false-没有个人化指令
	 */
	public boolean hasPersonalizationToExecute() {
		if (currentPersonalizationIndex > personalizations.size()) {
			throw new IllegalArgumentException("currentPersonlizationIndex great than personalizations.size()");
		}
		return currentPersonalizationIndex < personalizations.size();
	}

	/**
	 * 获取当前待下发的的个人化指令
	 * 
	 * @return 当前待下发的的个人化指令<br/>
	 *         null-如果没有待下发的的个人化指令
	 */
	@Transient
	public Personalization getCurrentPersonalization() {
		if (hasPersonalizationToExecute()) {
			return personalizations.get(currentPersonalizationIndex);
		} else {
			return null;
		}
	}

	public void increaseCurrentPersonalizationIndex() {
		currentPersonalizationIndex++;
	}
}

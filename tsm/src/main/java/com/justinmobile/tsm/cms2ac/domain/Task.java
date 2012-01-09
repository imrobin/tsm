package com.justinmobile.tsm.cms2ac.domain;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Lists;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Entity
@Table(name = "TASK")
public class Task extends AbstractEntity {

	private static final long serialVersionUID = -6751104838333288214L;

	private Long id;

	/**
	 * 任务中事务的总数
	 */
	private Integer transCount;

	/**
	 * 任务是否已经执行完成
	 */
	private Boolean finished;

	/**
	 * 任务中执行成功的事务数
	 */
	private Integer succTransCount;

	/**
	 * 任务中执行失败的事务数
	 */
	private Integer failTransCount;

	/**
	 * 任务开始时间
	 */
	private Calendar beginTime;

	/**
	 * 任务结束时间
	 */
	private Calendar endTime;

	/**
	 * 任务执行时当前的事务索引
	 */
	private Integer currentTransIndex;

	private List<LocalTransaction> localTransactions = Lists.newArrayList();

	@OneToMany(mappedBy = "task")
	@Cascade(CascadeType.ALL)
	@OrderBy("id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<LocalTransaction> getLocalTransactions() {
		return localTransactions;
	}

	public void setLocalTransactions(List<LocalTransaction> localTransactions) {
		this.localTransactions = localTransactions;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_TASK") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTransCount() {
		return transCount;
	}

	public void setTransCount(Integer transCount) {
		this.transCount = transCount;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Integer getSuccTransCount() {
		return succTransCount;
	}

	public void setSuccTransCount(Integer succTransCount) {
		this.succTransCount = succTransCount;
	}

	public Integer getFailTransCount() {
		return failTransCount;
	}

	public void setFailTransCount(Integer failTransCount) {
		this.failTransCount = failTransCount;
	}

	public Calendar getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Calendar beginTime) {
		this.beginTime = beginTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Integer getCurrentTransIndex() {
		return currentTransIndex;
	}

	public void setCurrentTransIndex(Integer currentTransIndex) {
		this.currentTransIndex = currentTransIndex;
	}

	public void increaseCurrentTransIndex() {
		if (this.currentTransIndex == null) {
			this.currentTransIndex = 0;
		}
		this.currentTransIndex++;
	}

	public void increaseSuccTransCount() {
		if (this.succTransCount == null) {
			this.succTransCount = 0;
		}
		this.succTransCount++;
	}

	public void increaseFailTransCount() {
		if (this.failTransCount == null) {
			this.failTransCount = 0;
		}
		this.failTransCount++;
	}

	/**
	 * 获取当前待执行的流程
	 * 
	 * @return 当前待执行的流程
	 */
	@Transient
	public LocalTransaction getCurrentTransaction() {
		return localTransactions.get(currentTransIndex - 1);
	}

	/**
	 * 是否还有流程需要执行？
	 * 
	 * @return true-还有流程需要执行<br/>
	 *         false-没有流程需要执行
	 */
	@Transient
	public boolean hasTrancationToExecut() {
		return currentTransIndex <= localTransactions.size();
	}

	/**
	 * 为当前任务添加一个事务，建立双向关联
	 * 
	 * @param localTransaction
	 *            待添加的事务
	 */
	public void addTransaction(LocalTransaction localTransaction) {
		this.localTransactions.add(localTransaction);
		localTransaction.setTask(this);
	}
}

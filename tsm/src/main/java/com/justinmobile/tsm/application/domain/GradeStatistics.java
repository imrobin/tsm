package com.justinmobile.tsm.application.domain;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "GRADE_STATISTICS")
public class GradeStatistics extends AbstractEntity {

	private static final long serialVersionUID = 286352442L;

	private Long id;

	private Integer gradeFiveCount;

	private Integer gradeFourCount;

	private Integer gradeThreeCount;

	private Integer gradeTwoCount;

	private Integer gradeOneCount;

	private Integer gradeZeroCount;

	private Application application;

	public GradeStatistics() {
		this.gradeFiveCount = 0;
		this.gradeFourCount = 0;
		this.gradeThreeCount = 0;
		this.gradeTwoCount = 0;
		this.gradeOneCount = 0;
		this.gradeZeroCount = 0;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Id
	@GeneratedValue(generator = "pkGenerator")
	@GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "application"))
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGradeFiveCount() {
		return gradeFiveCount;
	}

	public void setGradeFiveCount(Integer gradeFiveCount) {
		this.gradeFiveCount = gradeFiveCount;
	}

	public Integer getGradeFourCount() {
		return gradeFourCount;
	}

	public void setGradeFourCount(Integer gradeFourCount) {
		this.gradeFourCount = gradeFourCount;
	}

	public Integer getGradeThreeCount() {
		return gradeThreeCount;
	}

	public void setGradeThreeCount(Integer gradeThreeCount) {
		this.gradeThreeCount = gradeThreeCount;
	}

	public Integer getGradeTwoCount() {
		return gradeTwoCount;
	}

	public void setGradeTwoCount(Integer gradeTwoCount) {
		this.gradeTwoCount = gradeTwoCount;
	}

	public Integer getGradeOneCount() {
		return gradeOneCount;
	}

	public void setGradeOneCount(Integer gradeOneCount) {
		this.gradeOneCount = gradeOneCount;
	}

	public Integer getGradeZeroCount() {
		return gradeZeroCount;
	}
	
	public void countGrade(int star, boolean isAdd) {
		switch (star) {
		case 0:
			if (isAdd)
				this.setGradeZeroCount((this.getGradeZeroCount() == null ? 0 : this.getGradeZeroCount()) + 1);
			else
				this.setGradeZeroCount((this.getGradeZeroCount() == null ? 0 : this.getGradeZeroCount()) - 1);

			break;
		case 1:
			if (isAdd)
				this.setGradeOneCount((this.getGradeOneCount() == null ? 0 : this.getGradeOneCount()) + 1);
			else
				this.setGradeOneCount((this.getGradeOneCount() == null ? 0 : this.getGradeOneCount()) - 1);

				break;
		case 2:
			if (isAdd)
				this.setGradeTwoCount((this.getGradeTwoCount() == null ? 0 : this.getGradeTwoCount()) + 1);
			else
				this.setGradeTwoCount((this.getGradeTwoCount() == null ? 0 : this.getGradeTwoCount()) - 1);

			break;
		case 3:
			if (isAdd)
				this.setGradeThreeCount((this.getGradeThreeCount() == null ? 0 : this.getGradeThreeCount()) + 1);
			else
				this.setGradeThreeCount((this.getGradeThreeCount() == null ? 0 : this.getGradeThreeCount()) - 1);

			break;
		case 4:
			if (isAdd)
				this.setGradeFourCount((this.getGradeFourCount() == null ? 0 : this.getGradeFourCount()) + 1);
			else
				this.setGradeFourCount((this.getGradeFourCount() == null ? 0 : this.getGradeFourCount()) - 1);

			break;
		case 5:
			if (isAdd)
				this.setGradeFiveCount((this.getGradeFiveCount() == null ? 0 : this.getGradeFiveCount()) + 1);
			else
				this.setGradeFiveCount((this.getGradeFiveCount() == null ? 0 : this.getGradeFiveCount()) - 1);

			break;
		default:
			break;
		}
	}

	public void setGradeZeroCount(Integer gradeZeroCount) {
		this.gradeZeroCount = gradeZeroCount;
	}

	/**
	 * 计算平均分, 6段总分除以总人数
	 */
	@Transient
	public int getAvgNumber() {
		int fiveCount = (this.getGradeFiveCount() == null ? 0 : this.getGradeFiveCount().intValue());
		int fourCount = (this.getGradeFourCount() == null ? 0 : this.getGradeFourCount().intValue());
		int threeCount = (this.getGradeThreeCount() == null ? 0 : this.getGradeThreeCount().intValue());
		int twoCount = (this.getGradeTwoCount() == null ? 0 : this.getGradeTwoCount().intValue());
		int oneCount = (this.getGradeOneCount() == null ? 0 : this.getGradeOneCount().intValue());
		int zeroCount = (this.getGradeZeroCount() == null ? 0 : this.getGradeZeroCount().intValue());
		if ((fiveCount + fourCount + threeCount + twoCount + oneCount + zeroCount) == 0) {
			return 0;
		} else {
			return (fiveCount * 5 + fourCount * 4 + threeCount * 3 + twoCount * 2 + oneCount)
					/ (fiveCount + fourCount + threeCount + twoCount + oneCount + zeroCount);
		}
	}

	/**
	 * 计算平均分, 满分10分保留一位小数
	 */
	@Transient
	public String getAvgNumberInTen() {
		double fiveCount = (this.getGradeFiveCount() == null ? 0 : this.getGradeFiveCount().intValue());
		double fourCount = (this.getGradeFourCount() == null ? 0 : this.getGradeFourCount().intValue());
		double threeCount = (this.getGradeThreeCount() == null ? 0 : this.getGradeThreeCount().intValue());
		double twoCount = (this.getGradeTwoCount() == null ? 0 : this.getGradeTwoCount().intValue());
		double oneCount = (this.getGradeOneCount() == null ? 0 : this.getGradeOneCount().intValue());
		double zeroCount = (this.getGradeZeroCount() == null ? 0 : this.getGradeZeroCount().intValue());
		if ((fiveCount + fourCount + threeCount + twoCount + oneCount + zeroCount) == 0) {
			return "暂无评价";
		} else {
			double avg = 2 * ((fiveCount * 5 + fourCount * 4 + threeCount * 3 + twoCount * 2 + oneCount) / (fiveCount
					+ fourCount + threeCount + twoCount + oneCount + zeroCount));
			DecimalFormat df = new DecimalFormat("#.#");
			String avgStr = df.format(avg);
			return avgStr;
		}
	}

}
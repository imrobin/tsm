package com.justinmobile.tsm.application.domain;

import java.text.DecimalFormat;

import javax.persistence.Transient;

/**
 * 非持久化领域对象，用户装载可变空间和不可变空间信息，提供加法和减法运算
 * 
 * @author JazGung
 * 
 */
public class Space {

	/** 不可变空间 */
	private long nvm;

	/** 可变空间 */
	private int ram;

	public long getNvm() {
		return nvm;
	}

	public void setNvm(long nvm) {
		this.nvm = nvm;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	/**
	 * 加法运算，当前对象作为被加数，参数作为加数
	 * 
	 * @param addend
	 *            加数
	 * @return 计算结果
	 */
	public Space plus(Space addend) {
		this.nvm += addend.nvm;
		this.ram += addend.ram;

		return this;
	}

	/**
	 * 减法运算，当前对象作为被减数，参数作为减数
	 * 
	 * @param addend
	 *            减数
	 * @return 计算结果
	 */
	public Space minus(Space subtrahend) {
		this.nvm -= subtrahend.nvm;
		this.ram -= subtrahend.ram;

		return this;
	}

	/**
	 * 直接使用的byte没有根据实际数据换算成K/M（即如果<1024byte则显示为byte；
	 * 如果>=1024byte且<1048576byte,则显示为KB；如果>=1048576byte则换算显示成MB）
	 * 
	 * @param space
	 * @return
	 */
	public String spaceToString(long space) {
		DecimalFormat df = new DecimalFormat("#.##");

		if (space >= 1024 && 1048576 > space) {
			double div = 1024;
			String avgStr = df.format(space / div);
			return avgStr + "KB";
		} else if (space >= 1048576) {
			double div = 1048576;
			String avgStr = df.format(space / div);
			return avgStr + "MB";
		} else
			return space + "byte";
	}

	/**
	 * 空间是否够用？
	 * 
	 * @param needSpace
	 *            需要的空间
	 * @return true-够用<br/>
	 *         flase-不够用
	 */
	@Transient
	public boolean isSuffice(Space needSpace) {
		return this.nvm >= needSpace.nvm && this.ram >= needSpace.ram;
	}

	/**
	 * 当前空间是否>=目标空间？
	 * 
	 * @param targetSpace
	 *            目标空间
	 * @return true-是，当前的可变空间和不可变空间分别大于等于目标的可变空间和不可变空间<br/>
	 *         flase-否，当前的可变空间或/和不可变空间分别小于目标的可变空间和不可变空间
	 */
	@Transient
	public boolean isGreaterOrEqual(Space targetSpace) {
		return this.nvm >= targetSpace.nvm && this.ram >= targetSpace.ram;
	}
}

/**
 * Copyright (c) 2005-20010 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: PropertyFilter.java,v 1.3 2011/06/28 07:30:28 gaofeng Exp $
 */
package com.justinmobile.core.dao.support;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.justinmobile.core.utils.reflection.ConvertUtils;

/**
 * 与具体ORM实现无关的属性过滤条件封装类, 主要记录页面中简单的搜索过滤条件.
 * 
 * @author peak
 */
public class PropertyFilter {

	/** 多个属性间OR关系的分隔符. */
	public static final String OR_SEPARATOR = "_OR_";

	public static final String ALIAS = "ALIAS";

	/** 属性比较类型. */
	public enum MatchType {
		EQ("="), LIKE("like"), LT("<"), GT(">"), LE("<="), GE(">="), IN("in"), BETWEEN("between"), NOTIN("not in"), NE("<>"), 
		NULL("is null"), NOTNULL("is not null"), EMPTY("is empty"), NOTEMPTY("is not empty");

		private String option;

		MatchType(String option) {
			this.option = option;
		}

		public String getOption() {
			return this.option;
		}
	}

	public enum JoinType {
		F(4), L(1), I(0);

		private int ordinal;

		JoinType(int ordinal) {
			this.ordinal = ordinal;
		}

		public int getValue() {
			return this.ordinal;
		}
	}

	/** 属性数据类型. */
	public enum PropertyType {
		S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(Date.class), B(Boolean.class), C(Calendar.class);

		private Class<?> clazz;

		private PropertyType(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Class<?> getValue() {
			return clazz;
		}
	}

	private String aliasName = null;

	private MatchType matchType = null;
	private Object matchValue = null;

	private Class<?> propertyClass = null;
	private String[] propertyNames = null;

	private int joinType = JoinType.I.getValue();

	public PropertyFilter() {
	}

	/**
	 * @param filterName
	 *            比较属性字符串,含待比较的比较类型、属性值类型及属性列表. eg.
	 *            ALIAS_usersL_LIKES_NAME_OR_LOGIN_NAME,
	 *            LIKES_NAME_OR_LOGIN_NAME
	 * @param value
	 *            待比较的值.
	 */
	public PropertyFilter(String filterName, final String value) {
		buildFilterName(filterName);
		this.matchValue = ConvertUtils.convertStringToObject(value, propertyClass);
	}

	/**
	 * 外部进行类型转换
	 * 
	 * @param filterName
	 * @param value
	 */
	public PropertyFilter(String filterName, final Object value) {
		buildFilterName(filterName);
		this.matchValue = value;
	}

	private void buildFilterName(String filterName) {
		// 取开头，判断是否是需要级联查询
		String aliasPart = StringUtils.substringBefore(filterName, "_");
		if (ALIAS.equals(aliasPart)) {
			String allPart = StringUtils.substringAfter(filterName, "_");
			String firstPart = StringUtils.substringBefore(allPart, "_");
			this.aliasName = StringUtils.substring(firstPart, 0, firstPart.length() - 1);
			String joinTypeCode = StringUtils.substring(firstPart, firstPart.length() - 1, firstPart.length());
			this.joinType = Enum.valueOf(JoinType.class, joinTypeCode).getValue();
			filterName = StringUtils.substringAfter(allPart, "_");
		}
		// 取查询语句和查询参数的类型，要符合matchType和propertyTypeCode
		String firstPart = StringUtils.substringBefore(filterName, "_");
		String matchTypeCode = StringUtils.substring(firstPart, 0, firstPart.length() - 1);
		String propertyTypeCode = StringUtils.substring(firstPart, firstPart.length() - 1, firstPart.length());
		try {
			matchType = Enum.valueOf(MatchType.class, matchTypeCode);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e);
		}

		try {
			propertyClass = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性值类型.", e);
		}
		// 取传入的值，判断是否有OR的情况
		String propertyNameStr = StringUtils.substringAfter(filterName, "_");
		Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter名称" + filterName + "没有按规则编写,无法得到属性名称.");
		propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr, PropertyFilter.OR_SEPARATOR);
	}

	public PropertyFilter(final String filterName, final MatchType matchType, final PropertyType propertyType, final String value) {
		this.matchType = matchType;
		this.propertyClass = propertyType.getValue();
		this.propertyNames = StringUtils.splitByWholeSeparator(filterName, PropertyFilter.OR_SEPARATOR);
		this.matchValue = ConvertUtils.convertStringToObject(value, propertyClass);
	}

	/**
	 * 支持一级关联查询
	 * 
	 * @param aliasName
	 * @param filterName
	 * @param matchType
	 * @param propertyType
	 * @param value
	 */
	public PropertyFilter(final String aliasName, final JoinType joinType, final String filterName, final MatchType matchType,
			final PropertyType propertyType, final String value) {
		this.aliasName = aliasName;
		this.joinType = joinType.getValue();
		this.matchType = matchType;
		this.propertyClass = propertyType.getValue();
		this.propertyNames = StringUtils.splitByWholeSeparator(filterName, PropertyFilter.OR_SEPARATOR);
		this.matchValue = ConvertUtils.convertStringToObject(value, propertyClass);
	}

	/**
	 * 获取比较值的类型.
	 */
	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	/**
	 * 获取比较方式.
	 */
	public MatchType getMatchType() {
		return matchType;
	}

	/**
	 * 获取比较值.
	 */
	public Object getMatchValue() {
		return matchValue;
	}

	/**
	 * 获取比较属性名称列表.
	 */
	public String[] getPropertyNames() {
		return propertyNames;
	}

	/**
	 * 获取唯一的比较属性名称.
	 */
	public String getPropertyName() {
		Assert.isTrue(propertyNames.length == 1, "There are not only one property in this filter.");
		return propertyNames[0];
	}

	/**
	 * 是否比较多个属性.
	 */
	public boolean hasMultiProperties() {
		return (propertyNames.length > 1);
	}

	/**
	 * 是否需要关联查询
	 * 
	 * @return
	 */
	public boolean isAlias() {
		return StringUtils.isNotBlank(this.aliasName);
	}

	public String getAliasName() {
		return aliasName;
	}

	public int getJoinType() {
		return joinType;
	}
}

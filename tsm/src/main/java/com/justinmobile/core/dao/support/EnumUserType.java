package com.justinmobile.core.dao.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.EnumType;

public class EnumUserType extends EnumType {

	private static final long serialVersionUID = -4505759018423191229L;

	public static final String NAME = "com.justinmobile.core.dao.support.EnumUserType";

	private static final int SQL_TYPE = Types.INTEGER;

	private transient Object[] enumValues;

	private void initEnumValues() {
		if (enumValues == null) {
			this.enumValues = returnedClass().getEnumConstants();
			if (enumValues == null) {
				throw new NullPointerException("Failed to init enumValues");
			}
		}
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { SQL_TYPE };
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		Object object = rs.getObject(names[0]);
		if (rs.wasNull()) {
			return null;
		}

		initEnumValues();
		int persistentValue = ((Number) object).intValue();
		for (Object enumValue : enumValues) {
			if (persistentValue == ((EnumPersistentable) enumValue).getValue()) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException("Unknown ordinal value for enum " + returnedClass() + ": " + persistentValue);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		if (null == value) {
			st.setNull(index, SQL_TYPE);
		} else {
			st.setObject(index, ((EnumPersistentable) value).getValue(), SQL_TYPE);
		}
	}
}

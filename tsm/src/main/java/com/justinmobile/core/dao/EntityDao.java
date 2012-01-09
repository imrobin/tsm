package com.justinmobile.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;

/**
 * 针对单个Entity对象的操作定义. 不依赖于具体ORM实现方案.
 * 
 * @author peak
 */
public interface EntityDao<T, PK extends Serializable> {

	List<T> get(final Collection<PK> ids);

	T load(final PK id);

	List<T> getAll();

	List<T> getAll(final String propertyName, boolean isAsc);

	void saveOrUpdate(final T entity);

	void remove(final T entity);

	void remove(final PK id);

	List<T> find(List<PropertyFilter> filters, String... orderBy);

	Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters);

	<X> List<X> find(final String hql, final Object... values);

	List<T> findByProperty(final String propertyName, final Object value);

	<X> X findUniqueEntity(final String hql, final Object... values);

	T findUniqueByProperty(final String propertyName, final Object value);

	boolean isPropertyUnique(final String propertyName, final Object newValue, final Object orgValue);

	Page<T> findPage(final Page<T> page, final String hql, final Object... values);

	Page<T> findPage(final Page<T> page, final String hql, final Map<String, ?> values);
	
	Page<T> findPage(Page<T> page, Map<String, ?> filters, String... joinEntitys);

}

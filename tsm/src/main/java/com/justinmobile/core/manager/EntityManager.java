package com.justinmobile.core.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformException;

/**
 * 业务处理时一些简单的对象处理方法
 * @author peak
 *
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface EntityManager<T> {
	
	@Transactional(readOnly = true)
	List<T> get(final Collection<Long> ids) throws PlatformException;

	@Transactional(readOnly = true)
	T load(Long id) throws PlatformException;

	@Transactional(readOnly = true)
	List<T> getAll() throws PlatformException;
	
	@Transactional(readOnly = true)
	List<T> getAll(final String propertyName, boolean isAsc);

	void saveOrUpdate(T entity) throws PlatformException;

	void remove(T entity) throws PlatformException;

	void remove(Long id) throws PlatformException;
	
	@Transactional(readOnly = true)
	Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters) throws PlatformException;
	
	@Transactional(readOnly = true)
	Page<T> findPage(final Page<T> page, final Map<String, ?> filters, final String... joinEntitys) throws PlatformException;

	@Transactional(readOnly = true)
	List<T> find(final List<PropertyFilter> filters) throws PlatformException;

	boolean isPropertyUnique(String propertyName, Object newValue, Object orgValue);
}
package com.justinmobile.core.manager;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.util.Assert;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.reflection.ReflectionUtils;

/**
 * 反射子类的对象和Dao调用方法
 * 
 * @author peak
 * 
 */
public class EntityManagerImpl<T, M extends EntityDao<T, Long>> implements EntityManager<T> {

	private M entityDao;

	protected Class<T> getEnityClass() {
		return ReflectionUtils.getSuperClassGenricType(getClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected M getEntityDao() throws PlatformException {
		if (entityDao != null) {
			return entityDao;
		}
		// 得到泛型的第二个参数的类型
		Class superClassGenricType = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
		// 根据类型得到所有声明的Field
		List<Field> fields = ReflectionUtils.getFieldsByType(this, superClassGenricType);
		// 取第一个满足泛型类型的Filed进行获取
		entityDao = (M) ReflectionUtils.getFieldValue(this, fields.get(0).getName());
		Assert.notNull(entityDao, "Dao未能成功初始化");
		return entityDao;
	}

	@Override
	public List<T> get(Collection<Long> ids) throws PlatformException {
		try {
			return getEntityDao().get(ids);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public T load(Long id) throws PlatformException {
		try {
			return getEntityDao().load(id);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<T> getAll() throws PlatformException {
		try {
			return getEntityDao().getAll();
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<T> getAll(String propertyName, boolean isAsc) {
		try {
			return getEntityDao().getAll(propertyName, isAsc);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void saveOrUpdate(T entity) throws PlatformException {
		try {
			getEntityDao().saveOrUpdate(entity);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(T entity) throws PlatformException {
		try {
			getEntityDao().remove(entity);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void remove(Long id) throws PlatformException {
		try {
			getEntityDao().remove(id);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<T> findPage(Page<T> page, List<PropertyFilter> filters) throws PlatformException {
		try {
			return getEntityDao().findPage(page, filters);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<T> findPage(Page<T> page, Map<String, ?> filters, String... joinEntitys) throws PlatformException {
		try {
			return getEntityDao().findPage(page, filters, joinEntitys);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<T> find(List<PropertyFilter> filters) throws PlatformException {
		try {
			return getEntityDao().find(filters);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object orgValue) {
		try {
			return getEntityDao().isPropertyUnique(propertyName, newValue, orgValue);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}

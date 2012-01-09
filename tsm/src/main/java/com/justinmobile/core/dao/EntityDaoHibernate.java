package com.justinmobile.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.utils.reflection.ConvertUtils;
import com.justinmobile.core.utils.reflection.ReflectionUtils;

/**
 * DAO主要继承的类，实现了简单的分页查询功能
 * 
 * @author peak
 * 
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class EntityDaoHibernate<T, PK extends Serializable> extends GenericDaoHibernate<T, PK> implements EntityDao<T, PK> {

	/**
	 * 用于Dao层子类的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
	 * HibernateDao<User, Long>{ }
	 */
	public EntityDaoHibernate() {
		super();
	}

	/**
	 * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数. 在构造函数中定义对象类型Class. eg.
	 * HibernateDao<User, Long> userDao = new HibernateDao<User,
	 * Long>(sessionFactory, User.class);
	 */
	public EntityDaoHibernate(SessionFactory sessionFactory, Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}

	/**
	 * 分页获取全部对象.
	 */
	public Page<T> getAll(final Page<T> page) {
		return findPage(page);
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数. 注意不支持其中的orderBy参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {
		Assert.notNull(page, "page is not null");
		
		String hqlWithOrder = setPageOrder(hql, page);

		Query q = createQuery(hqlWithOrder, values);

		if (page.isAutoCount()) {
			int totalCount = countHqlResult(hqlWithOrder, values);
			page.setTotalCount(totalCount);
		}

		setPageParameterToQuery(q, page);

		List<T> result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数. 注意不支持其中的orderBy参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(final Page<T> page, final String hql, final Map<String, ?> values) {
		Assert.notNull(page, "page不能为空");

		String hqlWithOrder = setPageOrder(hql, page);
		
		Query q = createQuery(hqlWithOrder, values);

		if (page.isAutoCount()) {
			int totalCount = countHqlResult(hqlWithOrder, values);
			page.setTotalCount(totalCount);
		}

		setPageParameterToQuery(q, page);
		
		List<T> result = q.list();
		page.setResult(result);
		return page;
	}
	
	@Override
	public Page<T> findPage(Page<T> page, Map<String, ?> filters, String... joinEntitys) {
		StringBuilder hql = new StringBuilder("select entity from ").append(this.entityClass.getName()).append(" as entity");
		if (ArrayUtils.isNotEmpty(joinEntitys)) {
			for (String joinEntity : joinEntitys) {
				hql.append(" left join entity.").append(joinEntity).append(" as ").append(joinEntity);
			}
		}
		hql.append(" where 1=1 ");
		Map<String, Object> filterMap = Maps.newHashMap();
		if (MapUtils.isNotEmpty(filters)) {
			for (Map.Entry<String, ?> entry : filters.entrySet()) {
				String key = StringUtils.substringAfterLast(entry.getKey(), "_");
				String option = StringUtils.substringBeforeLast(entry.getKey(), "_");
				boolean joinKey = false;
				if (StringUtils.indexOf(key, ".") != -1) {
					String buf = StringUtils.substringBefore(key, ".");
					if (ArrayUtils.contains(joinEntitys, buf)) {
						joinKey = true;
					}
				}
				String matchTypeCode = StringUtils.substring(option, 0, option.length() - 1);
				String propertyTypeCode = StringUtils.substring(option, option.length() - 1, option.length());
				MatchType matchType = MatchType.valueOf(matchTypeCode);
				PropertyType propertyType = PropertyType.valueOf(propertyTypeCode);
				hql.append(" and ");
				if (!joinKey) {
					hql.append("entity.");
				}
				hql.append(key).append(" ").append(matchType.getOption()).append(" :").append(key);
				
				filterMap.put(key, ConvertUtils.convertStringToObject(String.valueOf(entry.getValue()), propertyType.getValue()));
			}
		}
		return findPage(page, hql.toString(), filterMap);
	}

	private String setPageOrder(String hql, Page<T> page) {
		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");
			StringBuilder buf = new StringBuilder(hql);
			int orderbyTag = StringUtils.indexOfIgnoreCase(hql, "order by");
			String entityName = StringUtils.substringBetween(hql, "select", "from");
			if (entityName == null) {
				//as是关键字，为了防止对象名称带as这种词语，所以截取字符的时候采用"空格as空格"的方式
				entityName = StringUtils.substringBefore(StringUtils.trimToEmpty(StringUtils.substringAfter(hql, " as ")), " ");
			}
			if (orderbyTag == -1 && orderByArray.length > 0) {
				buf.append(" order by ");
			} else {
				buf.append(", ");
			}
			for (int i = 0; i < orderByArray.length; i++) {
				//从页面得到的级联排序，可能是在json转换的时候把.转成_了，这边再转回去
				String orderName = StringUtils.replace(orderByArray[i], "_", ".");
				if (StringUtils.isNotBlank(entityName)) {
					buf.append(" " + StringUtils.trim(entityName) + ".");
				}
				if (Page.ASC.equals(orderArray[i])) {
					buf.append(orderName +" asc,");
				} else {
					buf.append(orderName +" desc,");
				}
				if (buf.lastIndexOf(",") == buf.length() - 1) {
					buf.deleteCharAt(buf.length() - 1);
				}
			}
			return buf.toString();
		} else {
			return hql;
		}
	}

	/**
	 * 按Criteria分页查询.
	 * 
	 * @param page
	 *            分页参数.
	 * @param criterions
	 *            数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询输入参数.
	 */
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {
		Assert.notNull(page, "page is not null");
		return findPage(page, createCriteria(criterions));
	}
	
	public Page<T> findPage(final Page<T> page, Criteria criteria) {
		if (page.isAutoCount()) {
			int totalCount = countCriteriaResult(criteria);
			page.setTotalCount(totalCount);
		}

		setPageParameterToCriteria(criteria, page);

		List<T> result = criteria.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 设置分页参数到Query对象,辅助函数.
	 */
	protected Query setPageParameterToQuery(final Query q, final Page<T> page) {

		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

		// hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	protected Criteria setPageParameterToCriteria(final Criteria c, final Page<T> page) {

		Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
		
		// hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

			for (int i = 0; i < orderByArray.length; i++) {
				String orderName = orderByArray[i];
				if (StringUtils.contains(orderName, "_")) {
					String[] orderByArraywithAlias = StringUtils.split(orderName, "_");
					c.createAlias(orderByArraywithAlias[0], orderByArraywithAlias[0] + i, PropertyFilter.JoinType.L.getValue());
					c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
					orderName = orderByArraywithAlias[0] + i + "." + orderByArraywithAlias[1];
				}
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderName));
				} else {
					c.addOrder(Order.desc(orderName));
				}
			}
		}
		//强制加根据id拍正序,否则容易出现oracle每次查出的结果顺序不一致的情况
		c.addOrder(Order.asc("id"));
		return c;
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected int countHqlResult(final String hql, final Object... values) {
		String countHql = prepareCountHql(hql);

		try {
			Integer count = ((Long) findUniqueEntity(countHql, values)).intValue();
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected int countHqlResult(final String hql, final Map<String, ?> values) {
		String countHql = prepareCountHql(hql);

		try {
			Integer count = ((Long) findUnique(countHql, values)).intValue();
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	private String prepareCountHql(String orgHql) {
		String fromHql = orgHql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;
		return countHql;
	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 */
	protected int countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List<CriteriaImpl.OrderEntry>) ReflectionUtils.getFieldValue(impl, "orderEntries");
			ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList<CriteriaImpl.OrderEntry>());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		// 执行Count查询
		Integer totalCountObject = ((Long) c.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		int totalCount = (totalCountObject != null) ? totalCountObject : 0;

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		return totalCount;
	}

	// -- 属性过滤条件(PropertyFilter)查询函数 --//

	/**
	 * 按属性查找对象列表,支持多种匹配方式.
	 * 
	 * @param matchType
	 *            匹配方式,目前支持的取值见PropertyFilter的MatcheType enum.
	 */
	public List<T> findBy(final String propertyName, final Object value, final MatchType matchType) {
		Criterion criterion = buildCriterion(propertyName, value, matchType);
		return findByCriteria(criterion);
	}

	/**
	 * 按属性过滤条件列表查找对象列表.
	 * 支持一级关联查询
	 */
	public List<T> find(List<PropertyFilter> filters) {
		Criteria criteria = getSession().createCriteria(entityClass);
		Criterion[] criterions = buildCriterionByPropertyFilter(filters, criteria);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria.list();
	}
	
	public List<T> find(List<PropertyFilter> filters, String... orderBy) {
		Criteria criteria = getSession().createCriteria(entityClass);
		Criterion[] criterions = buildCriterionByPropertyFilter(filters, criteria);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		if (ArrayUtils.isNotEmpty(orderBy)) {
			for (String order : orderBy) {
				String name = StringUtils.substringBefore(order, "_");
				String direction = StringUtils.substringAfter(order, "_");
				if ("desc".equalsIgnoreCase(direction)) {
					criteria.addOrder(Order.desc(name));
				} else {
					criteria.addOrder(Order.asc(name));
				}
				
			}
		}
		return criteria.list();
	}
	
	public T findUnique(List<PropertyFilter> filters) {
		Criteria criteria = getSession().createCriteria(entityClass);
		Criterion[] criterions = buildCriterionByPropertyFilter(filters, criteria);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return (T) criteria.uniqueResult();
	}

	/**
	 * 按属性过滤条件列表分页查找对象.
	 */
	public Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters) {
		Criteria criteria = getSession().createCriteria(entityClass);
		Criterion[] criterions = buildCriterionByPropertyFilter(filters, criteria);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return findPage(page, criteria);
	}

	/**
	 * 按属性条件参数创建Criterion,辅助函数.
	 */
	protected Criterion buildCriterion(final String propertyName, final Object propertyValue, final MatchType matchType) {
		Assert.hasText(propertyName, "propertyName is not null");
		Criterion criterion = null;
		// 根据MatchType构造criterion
		switch (matchType) {
		case EQ:
			criterion = Restrictions.eq(propertyName, propertyValue);
			break;
		case NE:
			criterion = Restrictions.ne(propertyName, propertyValue);
			break;
		case LIKE:
			criterion = Restrictions.like(propertyName, (String) propertyValue, MatchMode.ANYWHERE);
			break;
		case LE:
			criterion = Restrictions.le(propertyName, propertyValue);
			break;
		case LT:
			criterion = Restrictions.lt(propertyName, propertyValue);
			break;
		case GE:
			criterion = Restrictions.ge(propertyName, propertyValue);
			break;
		case GT:
			criterion = Restrictions.gt(propertyName, propertyValue);
			break;
		case NULL:
			criterion = Restrictions.isNull(propertyName);
			break;
		case NOTNULL:
			criterion = Restrictions.isNotNull(propertyName);
			break;
		case EMPTY:
			criterion = Restrictions.isEmpty(propertyName);
			break;
		case NOTEMPTY:
			criterion = Restrictions.isNotEmpty(propertyName);
			break;
		case IN:
			if (propertyValue instanceof Collection) {
				@SuppressWarnings("rawtypes")
				Collection value = (Collection) propertyValue;
				criterion = Restrictions.in(propertyName, value);
			} else if (propertyValue instanceof Object[]) {
				Object[] value = (Object[]) propertyValue;
				criterion = Restrictions.in(propertyName, value);
			}
			break;
		case NOTIN:
			if (propertyValue instanceof Collection) {
				@SuppressWarnings("rawtypes")
				Collection value = (Collection) propertyValue;
				criterion = Restrictions.not(Restrictions.in(propertyName, value));
			} else if (propertyValue instanceof Object[]) {
				Object[] value = (Object[]) propertyValue;
				criterion = Restrictions.not(Restrictions.in(propertyName, value));
			}
			break;
		case BETWEEN:
			if (propertyValue instanceof Object[]) {
				Object[] value = (Object[]) propertyValue;
				criterion = Restrictions.between(propertyName, value[0], value[1]);
			}
			break;
		}
		return criterion;
	}

	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
	 */
	protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		for (PropertyFilter filter : filters) {
			buildCriterion(criterionList, filter);
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}

	private void buildCriterion(List<Criterion> criterionList, PropertyFilter filter) {
		if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
			Criterion criterion = buildCriterion(filter.getPropertyName(), filter.getMatchValue(), filter.getMatchType());
			criterionList.add(criterion);
		} else {// 包含多个属性需要比较的情况,进行or处理.
			Disjunction disjunction = Restrictions.disjunction();
			for (String param : filter.getPropertyNames()) {
				Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
				disjunction.add(criterion);
			}
			criterionList.add(disjunction);
		}
	}

	/**
	 * 按属性条件列表创建Criterion数组,辅助函数.
	 */
	protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters, Criteria criteria) {
		List<Criterion> criterionList = new ArrayList<Criterion>();
		int i = 0;
		Map<String, String> aliasNames = new HashMap<String, String>();
		for (PropertyFilter filter : filters) {
			String propertyName = null;
			String aliasName = null;
			if (!filter.hasMultiProperties()) {
				propertyName = filter.getPropertyName();
			}
			if (filter.isAlias()) {
				String filterAliasName = filter.getAliasName();
				if (aliasNames.containsKey(filterAliasName)) {
					aliasName = aliasNames.get(filterAliasName);
				} else {
					aliasName = "alias" + i;
					criteria.createAlias(filterAliasName, aliasName, filter.getJoinType());
					criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
					aliasNames.put(filterAliasName, aliasName);
				}
				propertyName = aliasName + "." + filter.getPropertyName();
				i++;
			}
			if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
				Criterion criterion = buildCriterion(propertyName, filter.getMatchValue(), filter.getMatchType());
				criterionList.add(criterion);
			} else {// 包含多个属性需要比较的情况,进行or处理.
				Disjunction disjunction = Restrictions.disjunction();
				for (String param : filter.getPropertyNames()) {
					if (StringUtils.isNotBlank(aliasName)) {
						param = aliasName + "." + param;
					}
					Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
					disjunction.add(criterion);
				}
				criterionList.add(disjunction);
			}
		}
		return criterionList.toArray(new Criterion[criterionList.size()]);
	}

}

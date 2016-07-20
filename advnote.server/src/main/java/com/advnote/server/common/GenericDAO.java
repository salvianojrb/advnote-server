package com.advnote.server.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.ws.rs.InternalServerErrorException;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DiscriminatorType;
import org.springframework.stereotype.Component;

@Component
public class GenericDAO<T> {

	public enum QueryConector {
		AND, OR;
	}

	protected static final String ID = "id";

	public static final String ORDER_ASC = "asc";
	public static final String ORDER_DESC = "desc";
	public static final String NULL_ENTITY_ERROR_MESSAGE = "Entity to nullify cannot be null !";

	private Class<?> clazz = null;

	// search properties
	private T entity = null;
	private boolean fetch = false;
	private int firstResult = 0;
	private int maxResults = Integer.MAX_VALUE;
	private boolean ignoreCase = false;
	private MatchMode matchMode = MatchMode.EXACT;
	private String orderProperty = null;
	private String orderSense = ORDER_ASC;
	private FetchMode fetchMode = FetchMode.JOIN;
	private Map<String, Object> searchProperties = new HashMap<String, Object>();

	public GenericDAO() {

	}

	public GenericDAO(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Session getSession() {
		return HibernateUtil.getSessionFactory();
	}

	public Transaction beginTransaction(Session session) {
		return session.beginTransaction();
	}

	public void commitTransaction(Transaction tx, Session session) {
		try {
			tx.commit();
			HibernateUtil.closeSession(session);
		} catch (HibernateException e) {
			tx.rollback();
		}
	}

	public void rollbackTransaction(Transaction tx, Session session) {
		tx.rollback();
		HibernateUtil.closeSession(session);
	}

	public T saveWithoutTransaction(final T entity, Session session) throws HibernateException {
		if (entity == null) {
			throw new InternalServerErrorException();
		}

		session.save(entity);

		return entity;
	}

	public boolean deleteWithoutTransaction(final Serializable id, Session session) throws HibernateException {
		boolean result = false;
		@SuppressWarnings("unchecked")
		final T entityToRemove = (T) session.get(clazz, id);
		if (entityToRemove != null) {
			session.delete(entityToRemove);
			result = true;
		}
		return result;
	}

	public T updateWithoutTransaction(final T entity, Session session) throws HibernateException {
		try {
			session.merge(entity);
		} catch (HibernateException e) {
			throw new InternalServerErrorException(e);
		}

		return entity;
	}

	public T save(final T entity) {
		if (entity == null) {
			throw new InternalServerErrorException();
		}
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction transaction = session.beginTransaction();
		try {
			session.save(entity);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return entity;
	}

	public boolean delete(final Serializable id) {

		boolean result = false;
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction transaction = session.beginTransaction();
		try {
			@SuppressWarnings("unchecked")
			final T entityToRemove = (T) session.get(clazz, id);
			if (entityToRemove != null) {
				session.delete(entityToRemove);
				result = true;
			}
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public T update(final T entity) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction transaction = session.beginTransaction();
		try {
			session.merge(entity);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return entity;
	}

	public boolean updateUsingSQL(String sql, Map<String, Object> params) {
		boolean result = false;
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction transaction = session.beginTransaction();

		try {
			org.hibernate.Query query = session.createSQLQuery(sql);
			query.setProperties(params);
			int changedRows = query.executeUpdate();
			transaction.commit();

			if (changedRows > 0) {
				result = true;
			}
		} catch (HibernateException e) {
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public Object uniqueResult() {
		Object result = null;
		List<?> listResult = list();
		if (listResult != null) {
			if (listResult.size() == 1) {
				result = listResult.get(0);
			} else if (listResult.size() > 1) {
				throw new NonUniqueResultException(listResult.size());
			}
		}
		return result;
	}

	public List<?> list() {

		Order order = null;
		List<?> result = null;
		nullifyStrings(entity);
		final Session session = HibernateUtil.getSessionFactory();
		try {

			Criteria criteria = session.createCriteria(clazz);
			criteria.setFirstResult(firstResult);
			criteria.setMaxResults(maxResults);
			Example example = Example.create(entity).enableLike(matchMode).excludeZeroes();

			if (ignoreCase) {
				example = example.ignoreCase();
			}

			if (orderProperty != null && !orderProperty.isEmpty()) {
				if (ORDER_DESC.equals(orderSense)) {
					order = Order.desc(orderProperty);
				} else {
					order = Order.asc(orderProperty);
				}
			}

			if (fetch) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						criteria = criteria.setFetchMode(field.getName(), fetchMode);
					}
				}
			}

			criteria.add(example);

			if (order != null) {
				criteria.addOrder(order);
			}
			result = criteria.list();

		} catch (HibernateException e) {
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
			resetFilters();
		}
		return result;
	}

	public int executeHQL(String hql, Map<String, Object> parameters) {
		int result = 0;
		Session session = HibernateUtil.getSessionFactory();
		Transaction transaction = session.beginTransaction();
		Query query = null;
		try {
			query = session.createQuery(hql);
			if (parameters != null) {
				query.setProperties(parameters);
			}
			result = query.executeUpdate();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public int executeCountSQL(String sql, Map<String, Object> parameters) {
		int result = 0;
		Session session = HibernateUtil.getSessionFactory();
		Transaction tx = session.beginTransaction();
		SQLQuery query = null;
		try {
			query = session.createSQLQuery(sql);
			if (parameters != null) {
				query.setProperties(parameters);
			}
			result = Integer.parseInt(String.valueOf(query.uniqueResult()));
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public int executeCountHQL(String hql, Map<String, Object> parameters) {

		int result = 0;
		Query query = null;
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();

		try {
			query = session.createQuery(hql);

			if (parameters != null) {
				query.setProperties(parameters);
			}

			result = Integer.parseInt(String.valueOf(query.uniqueResult()));
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public T findById(final Serializable id) {
		return findByIdByClass(id, false, clazz);
	}

	public T findById(final Serializable id, final boolean fetch) {
		return findByIdByClass(id, fetch, clazz);
	}

	@SuppressWarnings("unchecked")
	public T findByIdByClass(final Serializable id, final boolean fetch, final Class<?> clazz) {
		T result = null;
		String idProperty = null;
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(clazz);
			List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				Field[] superclassFields = superclass.getDeclaredFields();
				fields.addAll(Arrays.asList(superclassFields));
			}
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class)) {
					idProperty = field.getName();
				}
				if (fetch) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						criteria = criteria.setFetchMode(field.getName(), fetchMode);
					}
				}
			}
			result = (T) criteria.add(Restrictions.eq(idProperty, id)).uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
			resetFilters();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return (List<T>) findAllByClass(false, clazz);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(final boolean fetch) {
		return (List<T>) findAllByClass(fetch, clazz);
	}

	public List<?> findAllByClass(final boolean fetch, final Class<?> clazz) {
		List<?> result = null;
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(clazz);
			if (fetch) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						criteria = criteria.setFetchMode(field.getName(), fetchMode);
					}
				}
			}
			result = session.createCriteria(clazz).list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
			resetFilters();
		}
		return result;
	}

	public List<?> findByHQL(final String queryString, final Map<String, Object> parameters) {
		return findByHQL(queryString, 0, Integer.MAX_VALUE, parameters);
	}

	public List<?> findByHQL(final String queryString, final int firstResult, final int maxResults, final Map<String, Object> parameters) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		List<?> result = null;
		try {
			org.hibernate.Query query = session.createQuery(queryString);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			if (parameters != null) {
				query.setProperties(parameters);
			}
			result = query.list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public Object findUniqueByHQL(final String queryString, final Map<String, Object> parameters) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		Object result = null;
		try {
			org.hibernate.Query query = session.createQuery(queryString);
			if (parameters != null) {
				query.setProperties(parameters);
			}
			result = query.uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public List<?> findBySQL(final String queryString, final Map<String, Object> parameters) {
		return findBySQL(queryString, 0, Integer.MAX_VALUE, parameters);
	}

	public List<?> findBySQL(final String queryString, final int firstResult, final int maxResults, final Map<String, Object> parameters) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		List<?> result = null;
		try {
			org.hibernate.Query query = session.createSQLQuery(queryString);

			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			query.setProperties(parameters);
			result = query.list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public List<T> findBySQL(final String queryString, final Map<String, Object> parameters, Class<T> entity,
			final Map<String, DiscriminatorType<?>> scalar) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		try {
			org.hibernate.SQLQuery query = session.createSQLQuery(queryString);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			query.setProperties(parameters);
			query.setResultTransformer(Transformers.aliasToBean(entity));
			
			if (scalar != null) {
				for (Map.Entry<String, DiscriminatorType<?>> entry : scalar.entrySet()) {
					query.addScalar(entry.getKey(), entry.getValue());
				}
			}

			@SuppressWarnings("unchecked")
			List<T> result = query.list();
			tx.commit();
			return result;
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<T> findBySQL(final String queryString, final Map<String, Object> parameters, Class<T> entity) {
		return findBySQL(queryString, parameters, entity, null);
	}

	public List<?> findBySQLWithoutMax(final String queryString, final Map<String, Object> parameters, @SuppressWarnings("rawtypes") Class entity) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		List<?> result = null;
		try {
			org.hibernate.SQLQuery query = session.createSQLQuery(queryString);
			query.setFirstResult(firstResult);
			query.setProperties(parameters);
			query.setResultTransformer(Transformers.aliasToBean(entity));
			result = query.list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public List<?> findBySQL(final String queryString, @SuppressWarnings("rawtypes") Class entity) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		List<?> result = null;
		try {
			org.hibernate.SQLQuery query = session.createSQLQuery(queryString);
			query.setFirstResult(firstResult);
			query.setResultTransformer(Transformers.aliasToBean(entity));
			result = query.list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByRange(String propertyName, Object low, Object high) {
		return (List<T>) findByRangeByClass(propertyName, low, high, clazz);
	}

	public List<?> findByRangeByClass(String propertyName, Object low, Object high, Class<?> clazz) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		List<?> result = null;
		try {
			Criteria criteria = session.createCriteria(clazz);
			criteria.add(Restrictions.between(propertyName, low, high));
			result = criteria.list();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	public Object findByProperties(QueryConector conector, boolean isList) {
		final Session session = HibernateUtil.getSessionFactory();
		final Transaction tx = session.beginTransaction();
		Object result = null;
		try {
			Criteria criteria = session.createCriteria(clazz);
			List<Criterion> criterionList = new ArrayList<Criterion>();
			if (fetch) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (Collection.class.isAssignableFrom(field.getType())) {
						criteria = criteria.setFetchMode(field.getName(), fetchMode);
					}
				}
			}

			for (Entry<String, Object> entry : searchProperties.entrySet()) {

				Object value = entry.getValue();

				if (ignoreCase) {
					criterionList.add(Restrictions.eq(entry.getKey(), value).ignoreCase());
				} else {
					criterionList.add(Restrictions.eq(entry.getKey(), value));
				}
			}

			Criterion[] criterionsArray = new Criterion[criterionList.size()];
			if (conector.equals(QueryConector.AND)) {
				criteria.add(Restrictions.and(criterionList.toArray(criterionsArray)));
			} else if (conector.equals(QueryConector.OR)) {
				criteria.add(Restrictions.or(criterionList.toArray(criterionsArray)));
			}

			if (isList) {
				result = criteria.list();
			} else {
				result = criteria.uniqueResult();
			}
			tx.commit();

		} catch (HibernateException e) {
			tx.rollback();
			throw new InternalServerErrorException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public GenericDAO<T> setEntity(T entity) {
		this.entity = entity;
		return this;
	}

	/**
	 * @param fetch
	 *            the fetch to set
	 */
	public GenericDAO<T> setFetch(boolean fetch) {
		this.fetch = fetch;
		return this;
	}

	/**
	 * @param firstResult
	 *            the firstResult to set
	 */
	public GenericDAO<T> setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	/**
	 * @param maxResults
	 *            the maxResults to set
	 */
	public GenericDAO<T> setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	/**
	 * @param ignoreCase
	 *            the ignoreCase to set
	 */
	public GenericDAO<T> setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	/**
	 * @param matchMode
	 *            the matchMode to set
	 */
	public GenericDAO<T> setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
		return this;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public GenericDAO<T> setClazz(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}

	/**
	 * @param orderProperty
	 *            the orderProperty to set
	 */
	public GenericDAO<T> setOrderProperty(String orderProperty) {
		this.orderProperty = orderProperty;
		return this;
	}

	/**
	 * @param orderSense
	 *            the orderSense to set
	 */
	public GenericDAO<T> setOrderSense(String orderSense) {
		this.orderSense = orderSense;
		return this;
	}

	/**
	 * @param fetchMode
	 *            the fetchMode to set
	 */
	public GenericDAO<T> setFetchMode(FetchMode fetchMode) {
		this.fetchMode = fetchMode;
		return this;
	}

	public GenericDAO<T> addSearchProperty(String key, Object value) {
		this.searchProperties.put(key, value);
		return this;
	}

	/**
	 * @param searchProperties
	 *            the searchProperties to set
	 */
	public GenericDAO<T> setSearchProperties(Map<String, Object> searchProperties) {
		this.searchProperties = searchProperties;
		return this;
	}

	private void nullifyStrings(final T entityToNullify) {
		if (entityToNullify == null) {
			throw new IllegalArgumentException(NULL_ENTITY_ERROR_MESSAGE);
		}
		String fieldValue = null;
		List<Field> listFields = new ArrayList<Field>();
		Class<?> currentClass = entityToNullify.getClass();
		while (currentClass != null) {
			listFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}
		try {
			for (Field field : listFields) {
				field.setAccessible(true);
				if (field.getType().equals(String.class)) {
					fieldValue = (String) field.get(entityToNullify);
					if (fieldValue != null && fieldValue.trim().isEmpty()) {
						field.set(entityToNullify, null);
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new InternalServerErrorException(e);
		}
	}

	private void resetFilters() {
		entity = null;
		fetch = false;
		firstResult = 0;
		maxResults = Integer.MAX_VALUE;
		ignoreCase = false;
		matchMode = MatchMode.EXACT;
		orderProperty = null;
		orderSense = ORDER_ASC;
		fetchMode = FetchMode.JOIN;
	}

}

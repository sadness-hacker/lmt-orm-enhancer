package com.lmt.orm.hibernate.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.builder.BeanInfoBuilder;
import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.hibernate.builder.HqlBuilder;

/**
 * 
 * @author ducx
 * @date 2017-07-22
 * hibernate baseDao实现
 *
 */
public class HibernateBaseDao<T,PK> implements IHibernateBaseDao<T,PK> {
	/**
	 * hql语句，参数化时第一个参数的下标是从0开始
	 */
	private static final int HQL_PARAM_START_INDEX = 0;
	/**
	 * sql语句，参数化时第一个参数的下标是从1开始
	 */
	private static final int SQL_PARAM_START_INDEX = 1;
	
	@Resource
	private SessionFactory sessionFactory;
	
	private Class<T> clazz;
	
	private final String LIST_ALL_HQL;
	
	private final String COUNT_ALL_HQL;
	
	@SuppressWarnings("unchecked")
	public HibernateBaseDao(){
		//获取当前泛型类
		Type type = this.getClass().getGenericSuperclass();
		if (type.toString().indexOf("BaseDao") != -1) {
			ParameterizedType type1 = (ParameterizedType) type;
			Type[] types = type1.getActualTypeArguments();
			setEntityClass((Class<T>) types[0]);
		}else{
			type = ((Class<T>)type).getGenericSuperclass();
			ParameterizedType type1 = (ParameterizedType) type;
			Type[] types = type1.getActualTypeArguments();
			setEntityClass((Class<T>) types[0]);
		}
		LIST_ALL_HQL = "from " + clazz.getSimpleName();
		COUNT_ALL_HQL = "select count(*) from " + clazz.getSimpleName();
	}
	
	public Class<T> getEntityClass() {
		return clazz;
	}
	
	public void setEntityClass(Class<T> entityClass) {
		this.clazz = entityClass;
	}
	
	@Override
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}


	@Override
	public PK insert(T t) {
		@SuppressWarnings("unchecked")
		PK id = (PK) getCurrentSession().save(t);
		return id;
	}

	@Override
	public void update(T t) {
		getCurrentSession().update(t);
	}

	@Override
	public void delete(T t) {
		getCurrentSession().delete(t);
	}
	
	@Override
	public int deleteById(PK id){
		SqlBean bean = HqlBuilder.buildDeleteByIdHql(clazz);
		String hql = bean.getSqlBuilder().toString();
		return executeHqlUpdate(hql,new Object[]{id});
	}

	@Override
	public T get(PK id) {
		T t = getCurrentSession().get(clazz, (Serializable)id);
		return t;
	}

	@Override
	public T load(PK id) {
		T t = getCurrentSession().load(clazz, (Serializable)id);
		return t;
	}

	@Override
	public List<T> listAll() {
		Query<T> query = getCurrentSession().createQuery(LIST_ALL_HQL, clazz);
		return query.getResultList();
	}

	@Override
	public List<T> list(T t) {
		SqlBean bean = HqlBuilder.buildQueryHql(t);
		String hql = bean.getSqlBuilder().toString();
		List<Object> params = bean.getParam();
		return executeHqlQuery(hql, params);
	}

	@Override
	public List<T> listByIdArray(PK[] idArray) {
		if(idArray == null || idArray.length < 1){
			return new ArrayList<T>();
		}
		String ids = "";
		for(int i=0;i<idArray.length;i++){
			if("".equals(ids)){
				ids = "?" + i;
			}else{
				ids = ids + ",?" + i;
			}
		}
		BeanInfo cib = BeanInfoBuilder.build(clazz);
		String hql = "from " + clazz.getSimpleName() + " where " + cib.getIdField().getFieldName() + " in (" + ids + ")";
		return executeHqlQuery(hql, (Object[])idArray);
	}

	@Override
	public List<T> listByIdList(List<PK> idList) {
		@SuppressWarnings("unchecked")
		PK [] idArray = (PK[]) idList.toArray();
		return listByIdArray(idArray);
	}

	@Override
	public long countAll() {
		Query<Long> query = getCurrentSession().createQuery(COUNT_ALL_HQL, Long.class);
		return query.getSingleResult();
	}
	
	@Override
	public long count(T t) {
		if(t == null){
			return 0;
		}
		SqlBean bean = HqlBuilder.buildCountHql(t);
		String hql = bean.getSqlBuilder().toString();
		List<Object> params = bean.getParam();
		return countByHql(hql, params);
	}
	
	@Override
	public long countByHql(String hql, Object... params) {
		Query<Long> query = getCurrentSession().createQuery(hql,Long.class);
		if(params != null){
			int i = HQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.getSingleResult();
	}

	@Override
	public long countBySql(String sql, Object... params) {
		Query<?> query = getCurrentSession().createNativeQuery(sql);
		if(params != null){
			int i = SQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		BigInteger o = (BigInteger) query.getSingleResult();
		return o.longValue();
	}
	
	@Override
	public List<T> executeHqlQuery(String hql, Object... params) {
		Query<T> query = getCurrentSession().createQuery(hql, clazz);
		if(params != null){
			int i = HQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.getResultList();
	}

	@Override
	public List<T> executeSqlQuery(String sql, Object... params) {
		Query<T> query = getCurrentSession().createNativeQuery(sql, clazz);
		if(params != null){
			int i = SQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.getResultList();
	}
	
	@Override
	public T executeHqlQueryOne(String hql, Object... params) {
		Query<T> query = getCurrentSession().createQuery(hql, clazz);
		if(params != null){
			int i = HQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.uniqueResult();
	}

	@Override
	public T executeSqlQueryOne(String sql, Object... params) {
		Query<T> query = getCurrentSession().createNativeQuery(sql, clazz);
		if(params != null){
			int i = SQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.uniqueResult();
	}

	@Override
	public int executeSqlUpdate(String sql, Object... params) {
		Query<?> query = getCurrentSession().createNativeQuery(sql);
		if(params != null){
			int i = SQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.executeUpdate();
	}
	
	@Override
	public int executeHqlUpdate(String hql, Object... params) {
		Query<?> query = getCurrentSession().createQuery(hql);
		if(params != null){
			int i = HQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		return query.executeUpdate();
	}

	@Override
	public PaginationModel<T> queryByPagination(PaginationModel<T> bean) {
		if(bean == null){
			return null;
		}
		int page = bean.getCurrPage();
		if(page < 1){
			page = 1;
			bean.setCurrPage(page);
		}
		int limit = bean.getLimit();
		if(limit < 1){
			limit = 20;
			bean.setLimit(20);
		}
		T t = bean.getT();
		if(t == null){
			long totalNum = countAll();
			Query<T> query = getCurrentSession().createQuery(LIST_ALL_HQL, clazz);
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
			List<T> list = query.getResultList();
			bean.setList(list);
			bean.setTotalNum(totalNum);
			return bean;
		}
		SqlBean countSqlBean = HqlBuilder.buildCountHql(t);
		long totalNum = countByHql(countSqlBean.getSqlBuilder().toString(), countSqlBean.getParam().toArray());
		SqlBean sb = HqlBuilder.buildQueryHql(t);
		String hql = sb.getSqlBuilder().toString();
		Query<T> query = getCurrentSession().createQuery(hql, clazz);
		List<Object> params = sb.getParam();
		if(params != null){
			int i = HQL_PARAM_START_INDEX;
			for(Object o : params){
				query.setParameter(i, o);
				i++;
			}
		}
		query.setFirstResult((page - 1) * limit);
		query.setMaxResults(limit);
		List<T> list = query.getResultList();
		bean.setList(list);
		bean.setTotalNum(totalNum);
		return bean;
	}

}

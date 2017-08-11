package com.lmt.orm.hibernate.dao;

import java.util.List;

import org.hibernate.Session;

import com.lmt.orm.common.model.PaginationModel;

/**
 * 
 * @author ducx
 * @date 2017-07-22
 * hibernate BaseDao接口
 *
 */
public interface IHibernateBaseDao<T,PK> {
	/**
	 * 插入新记录
	 * @param t
	 * @return
	 */
	public PK insert(T t);
	/**
	 * 根据id更新记录
	 * @param t
	 * @return
	 */
	public void update(T t);
	/**
	 * 根据实体删除记录
	 * @param t
	 * @return
	 */
	public void delete(T t);
	/**
	 * 根据id删除记录
	 * @param id
	 */
	public int deleteById(PK id);
	/**
	 * 根据id获取记录
	 * @param id
	 * @return
	 */
	public T get(PK id);
	/**
	 * 根据id获取记录,懒加载
	 * @param t
	 * @return
	 */
	public T load(PK id);
	/**
	 * 返回所有记录列表
	 * @return
	 */
	public List<T> listAll();
	/**
	 * 根据实体返回记录列表
	 * @param t
	 * @return
	 */
	public List<T> list(T t);
	/**
	 * 根据id数组返回记录列表
	 * @param idArray
	 * @return
	 */
	public List<T> listByIdArray(PK [] idArray);
	/**
	 * 根据id列表返回记录列表
	 * @param idList
	 * @return
	 */
	public List<T> listByIdList(List<PK> idList);
	/**
	 * 统计所有记录数
	 * @return
	 */
	public long countAll();
	/**
	 * 根据实体统计符合条件的记录数
	 * @param t
	 * @return
	 */
	public long count(T t);
	/**
	 * 根据hql统计记录数
	 * @param hql
	 * @param params
	 * @return
	 */
	public long countByHql(String hql,Object ... params);
	/**
	 * 根据sql统计记录数
	 * @param sql
	 * @param params
	 * @return
	 */
	public long countBySql(String sql,Object ... params);
	/**
	 * 根据hql查寻
	 * @param hql
	 * @param params
	 * @return
	 */
	public List<T> executeHqlQuery(String hql, Object... params);
	/**
	 * 根据sql查询记录列表
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<T> executeSqlQuery(String sql,Object ... params);
	/**
	 * 根据hql查询记录
	 * @param hql
	 * @param params
	 * @return
	 */
	public T executeHqlQueryOne(String hql,Object ... params);
	/**
	 * 根据sql查询记录
	 * @param sql
	 * @param params
	 * @return
	 */
	public T executeSqlQueryOne(String sql,Object ... params);
	/**
	 * 根据sql执行更新
	 * @param sql
	 * @param params
	 * @return
	 */
	public int executeSqlUpdate(String sql,Object ... params);
	/**
	 * 根据hql执行更新
	 * @param hql
	 * @param params
	 * @return
	 */
	public int executeHqlUpdate(String hql, Object ... params);
	/**
	 * 根据分页实体分页查询
	 * @param bean
	 * @return
	 */
	public PaginationModel<T> queryByPagination(PaginationModel<T> bean);
	/**
	 * 获取当前connection
	 * @return
	 */
	public Session getCurrentSession();
	
}

package com.lmt.orm.hibernate.service;


import java.util.List;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.hibernate.dao.HibernateBaseDao;

/**
 * 
 * @author ducx
 * @date 2017-07-22
 * hibernate baseService接口
 *
 */
public interface IHibernateBaseService<D extends HibernateBaseDao<T, PK>,T,PK> {
	/**
	 * 插入实体到数据库，返回主键
	 * @param t
	 * @return
	 */
	public PK insert(T t);
	/**
	 * 根据主键查询实体
	 * @param id
	 * @return
	 */
	public T get(PK id);
	/**
	 * 根据主键更新实体
	 * @param t
	 */
	public void update(T t);
	/**
	 * 根据实体条件删除记录
	 * @param t
	 */
	public void delete(T t);
	/**
	 * 根据id删除记录
	 * @param id
	 */
	public int deleteById(PK id);
	/**
	 * 根据id加载实体，懒加载
	 * @param id
	 * @return
	 */
	public T load(PK id);
	/**
	 * 加载所有实体
	 * @return
	 */
	public List<T> listAll();
	/**
	 * 根据实体查询符合条件的实体列表
	 * @param t
	 * @return
	 */
	public List<T> list(T t);
	/**
	 * 统计记录总数
	 * @return
	 */
	public long countAll();
	/**
	 * 根据sql统计记录总数
	 * @param hql
	 * @param params
	 * @return
	 */
	public long countByHql(String hql,Object ... params);
	/**
	 * 根据sql统计记录总数
	 * @param sql
	 * @param params
	 * @return
	 */
	public long countBySql(String sql,Object ... params);
	/**
	 * 分页查询
	 * @param paginationModel
	 * @return
	 */
	public PaginationModel<T> queryByPagination(PaginationModel<T> paginationModel);
	
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
	
}

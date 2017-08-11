package com.lmt.orm.mybatis.mapper;

import java.util.List;

import com.lmt.orm.common.model.PaginationModel;

/**
 * 
 * @author ducx
 * @date 2017-07-21
 * mybatis基础mapper
 *
 */
public interface IBaseMapper<T,PK> {
	/**
	 * 插入实体，返回插入的实体，如果是自增id，则设置插入后的id到实体中
	 * @param t
	 * @return
	 */
	public T insert(T t);
	/**
	 * 根据id查询实体
	 * @param id
	 * @return
	 */
	public T get(PK id);
	/**
	 * 根据实体条件，查询实体
	 * @param t
	 * @return
	 */
	public T load(T t);
	/**
	 * 根据id删除记录,返回删除的记录数
	 * @param id
	 * @return
	 */
	public int delete(PK id);
	/**
	 * 根据实体条件删除记录，返回删除的记录数
	 * @param t
	 * @return
	 */
	public int deleteMore(T t);
	/**
	 * 根据id更新实体，返回更新记录数
	 * @param t
	 * @return
	 */
	public int update(T t);
	/**
	 * 根据实体过滤条件，更新实体,返回更新记录数
	 * @param filter
	 * @param t
	 * @return
	 */
	public int update(T filter,T t);
	/**
	 * 统计记录总数
	 * @return
	 */
	public long countAll();
	/**
	 * 根据实体统计记录数
	 * @param t
	 * @return
	 */
	public long count(T t);
	/**
	 * 根据实体查询记录
	 * @param t
	 * @return
	 */
	public List<T> list(T t);
	/**
	 * 获取所有记录
	 * @return
	 */
	public List<T> listAll();
	/**
	 * 根据分页条件，查寻记录列表，记录总数
	 * @param paginationModel
	 * @return
	 */
	public PaginationModel<T> queryByPagination(PaginationModel<T> paginationModel);
	
}

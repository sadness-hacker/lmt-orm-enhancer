package com.lmt.orm.mybatis.service;

import java.util.List;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.mybatis.mapper.IBaseMapper;

/**
 * 
 * @author ducx
 * @date 2017-07-24
 * mybatis通用基础service接口
 * @param <D>	mapper接口
 * @param <T>	mapper对应的实体类
 * @param <PK>	实体类对应的主键
 */
public interface IMybatisBaseService<D extends IBaseMapper<T, PK>,T,PK> {

	/**
	 * 插入新记录，返回该记录（如果是自增id，会把生成的id放入到实体的id字段）
	 * @param t
	 * @return
	 */
	public T insert(T t);
	/**
	 * 根据id获取记录
	 * @param id
	 * @return
	 */
	public T get(PK id);
	/**
	 * 根据id更新记录
	 * @param t
	 * @return
	 */
	public int update(T t);
	/**
	 * 根据id删除记录
	 * @param id
	 * @return
	 */
	public int delete(PK id);
	/**
	 * 根据实体查询一个符合条件的实体（如果有多个记录，则会抛出运行时异常）
	 * @param t
	 * @return
	 */
	public T load(T t);
	/**
	 * 查询所有记录
	 * @return
	 */
	public List<T> listAll();
	/**
	 * 根据实体条件查询符合记录的列表
	 * @param t
	 * @return
	 */
	public List<T> list(T t);
	/**
	 * 统计所有记录数量
	 * @return
	 */
	public long countAll();
	/**
	 * 根据实体条件统计记录数量
	 * @param t
	 * @return
	 */
	public long count(T t);
	/**
	 * 根据分页条件查询分页结果
	 * @param paginationModel
	 * @return
	 */
	public PaginationModel<T> queryByPagination(PaginationModel<T> paginationModel);
	
}

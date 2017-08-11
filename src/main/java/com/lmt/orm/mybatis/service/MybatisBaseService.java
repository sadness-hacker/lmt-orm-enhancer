package com.lmt.orm.mybatis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.mybatis.mapper.IBaseMapper;
/**
 * 
 * @author ducx
 * @date 2017-07-24
 * mybatis通用基础service实现类
 * @param <D>	mapper接口
 * @param <T>	mapper对应的实体类
 * @param <PK>	实体类对应的主键
 */
public class MybatisBaseService<D extends IBaseMapper<T, PK>,T,PK> implements IMybatisBaseService<D, T, PK> {

	private D baseMapper;
	
	@Autowired
	public void setBaseDao(D baseMapper){
		this.baseMapper = baseMapper;
	}
	
	public D getBaseDao(){
		return baseMapper;
	}
	
	@Override
	public T insert(T t) {
		return baseMapper.insert(t);
	}

	@Override
	public T get(PK id) {
		return baseMapper.get(id);
	}

	@Override
	public int update(T t) {
		return baseMapper.update(t);
	}

	@Override
	public int delete(PK id) {
		return baseMapper.delete(id);
	}

	@Override
	public T load(T t) {
		return baseMapper.load(t);
	}

	@Override
	public List<T> listAll() {
		return baseMapper.listAll();
	}

	@Override
	public List<T> list(T t) {
		return baseMapper.list(t);
	}

	@Override
	public long countAll() {
		return baseMapper.countAll();
	}

	@Override
	public long count(T t) {
		return baseMapper.count(t);
	}

	@Override
	public PaginationModel<T> queryByPagination(PaginationModel<T> paginationModel) {
		return baseMapper.queryByPagination(paginationModel);
	}

}

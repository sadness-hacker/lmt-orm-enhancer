package com.lmt.orm.hibernate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.hibernate.dao.HibernateBaseDao;

/**
 * 
 * @author ducx
 * @date 2017-07-22
 * hibernate baseService实现类
 *
 */
public class HibernateBaseService<D extends HibernateBaseDao<T, PK>,T,PK> implements IHibernateBaseService<D,T,PK> {
	
	private D baseDao;
	
	@Autowired
	public void setBaseDao(D baseDao){
		this.baseDao = baseDao;
	}
	
	public D getBaseDao(){
		return baseDao;
	}
	
	public PK insert(T t){
		return baseDao.insert(t);
	}

	@Override
	public T get(PK id) {
		return baseDao.get(id);
	}

	@Override
	public void update(T t) {
		baseDao.update(t);
	}

	@Override
	public void delete(T t) {
		baseDao.delete(t);
	}
	
	@Override
	public int deleteById(PK id) {
		return baseDao.deleteById(id);
	}
	
	@Override
	public T load(PK id) {
		return baseDao.load(id);
	}

	@Override
	public List<T> listAll() {
		return baseDao.listAll();
	}

	@Override
	public List<T> list(T t) {
		return baseDao.list(t);
	}

	@Override
	public long countAll() {
		return baseDao.countAll();
	}
	
	@Override
	public long countByHql(String hql,Object ... params) {
		return baseDao.countByHql(hql, params);
	}

	@Override
	public long countBySql(String sql,Object ... params) {
		return baseDao.countBySql(sql, params);
	}

	@Override
	public PaginationModel<T> queryByPagination(PaginationModel<T> paginationModel) {
		return baseDao.queryByPagination(paginationModel);
	}

	@Override
	public List<T> listByIdArray(PK[] idArray) {
		return baseDao.listByIdArray(idArray);
	}

	@Override
	public List<T> listByIdList(List<PK> idList) {
		return baseDao.listByIdList(idList);
	}
	
}

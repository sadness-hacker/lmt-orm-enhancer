package com.lmt.orm.common.serivce;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lmt.orm.common.dao.BaseDao;
import com.lmt.orm.common.model.PaginationModel;

/**
 * 
 * @author ducx
 * @date 2017-04-11
 * 对应jdbc封装的baseService
 * @param <D>
 * @param <T>
 * @param <PK>
 */
public class BaseService<D extends BaseDao<T, PK>,T,PK> implements IBaseService<D, T, PK> {
	
	private D baseDao;
	
	@Autowired
	public void setBaseDao(D baseDao){
		this.baseDao = baseDao;
	}
	
	public D getBaseDao(){
		return baseDao;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public T insert(T t){
		return baseDao.insert(t);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public T get(PK id) {
		return baseDao.get(id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public int update(T t) {
		return baseDao.update(t);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public int delete(PK id) {
		return baseDao.delete(id);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public T load(T t) {
		return baseDao.load(t);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<T> listAll() {
		return baseDao.listAll();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<T> query(T t) {
		return baseDao.query(t);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public long countAll() {
		return baseDao.countAll();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public long count(String sql,Object ... params) {
		return baseDao.count(sql, params);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public PaginationModel<T> list(PaginationModel<T> paginationModel) {
		return baseDao.listByPagination(paginationModel);
	}

}

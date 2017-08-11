package com.lmt.orm.common.serivce;

import java.util.List;

import com.lmt.orm.common.dao.IBaseDao;
import com.lmt.orm.common.model.PaginationModel;

/**
 * 
 * @author ducx
 * @date 2017-04-11
 * 对应jdbc封装的baseService
 *
 */
public interface IBaseService<D extends IBaseDao<T,PK>,T,PK> {
	
	public T insert(T t);
	
	public T get(PK id);
	
	public int update(T t);
	
	public int delete(PK id);
	
	public T load(T t);
	
	public List<T> listAll();
	
	public List<T> query(T t);
	
	public long countAll();
	
	public long count(String sql,Object ... params);
	
	public PaginationModel<T> list(PaginationModel<T> paginationModel);
	
}

package com.lmt.orm.common.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.FieldInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.builder.BeanInfoBuilder;
import com.lmt.orm.builder.SqlBuilder;

/**
 * 
 * @author ducx
 * @date 2017-04-12
 *
 * @param <T>
 * @param <PK>
 */
public class BaseDao<T,PK> implements IBaseDao<T, PK> {
	
	@Resource
	private DataSource dataSource;
	
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public BaseDao(){
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
	}
	
	public Class<T> getEntityClass() {
		return clazz;
	}
	
	public void setEntityClass(Class<T> entityClass) {
		this.clazz = entityClass;
	}
	
	public Connection getConnection(){
		Connection conn = DataSourceUtils.getConnection(dataSource);
		return conn;
	}

	@Override
	public T insert(T t) {
		if(t == null){
			return null;
		}
		SqlBean bean = SqlBuilder.buildInsertSql(clazz);
		Object[] params = SqlBuilder.buildParamsArray(bean, t);
		ResultSetHandler<T> rs = new BeanHandler<T>(clazz);
		QueryRunner run = new QueryRunner();
		try {
			Connection conn = getConnection();
			run.insert(conn,bean.getSqlBuilder().toString(), rs, params);
			BeanInfo info = BeanInfoBuilder.build(clazz);
			if(info.isAutoIncreatmentId()){
				String sql = "select last_insert_id()";
				FieldInfo fi = info.getIdField();
				Number id = run.query(conn,sql, new ScalarHandler<Number>());
				if(id != null){
					Object o = id;
					if((Integer.class).equals(fi.getType())){
						o = id.intValue();
					}else if(Long.class.equals(fi.getType())){
						o = id.longValue();
					}
					Method method = info.findWriteMethod(fi.getFieldName());
					try {
						method.invoke(t, o);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	@Override
	public int update(T t) {
		if(t == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildUpdateSql(t.getClass());
		Object[] params = SqlBuilder.buildParamsArray(bean, t);
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(getConnection(), bean.getSqlBuilder().toString(), params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return num;
	}

	@Override
	public int delete(PK id) {
		if(id == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildDeleteByIdSql(clazz);
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(getConnection(), bean.getSqlBuilder().toString(), id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return num;
	}

	@Override
	public T get(PK id) {
		if(id == null){
			return null;
		}
		SqlBean bean = SqlBuilder.buildGetByIdSql(clazz);
		ResultSetHandler<T> rs = new BeanHandler<T>(clazz);
		T t = null;
		try {
			QueryRunner run = new QueryRunner();
			Connection conn = getConnection();
			t = run.query(conn, bean.getSqlBuilder().toString(), rs, id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	@Override
	public T load(T t) {
		SqlBean bean = SqlBuilder.buildQuerySql(t);
		ResultSetHandler<T> rs = new BeanHandler<T>(clazz);
		t = null;
		try {
			QueryRunner run = new QueryRunner();
			t = run.query(getConnection(), bean.getSqlBuilder().toString(), rs, bean.getParam().toArray());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	@Override
	public List<T> listAll() {
		SqlBean bean = SqlBuilder.buildListAllSql(clazz);
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		List<T> list = new ArrayList<T>();
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(getConnection(), bean.getSqlBuilder().toString(), rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return list;
	}

	@Override
	public List<T> query(T t) {
		List<T> list = new ArrayList<T>();
		SqlBean bean = SqlBuilder.buildQuerySql(t);
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(getConnection(), bean.getSqlBuilder().toString(), rs, bean.getParam().toArray());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	@Override
	public List<T> listByIdArray(PK [] idArray) {
		if(idArray == null || idArray.length < 1){
			return new ArrayList<T>();
		}
		String ids = "";
		for(int i=0;i<idArray.length;i++){
			if("".equals(ids)){
				ids = "?";
			}else{
				ids = ids + ",?";
			}
		}
		BeanInfo cib = BeanInfoBuilder.build(clazz);
		String sql = "select " + SqlBuilder.buildResultSql(clazz) + " from " + cib.getTableName() + " where " + cib.getIdField().getColumnName() + "in (" + ids + ")";
		return executeQuery(sql, idArray);
	}

	@Override
	public List<T> listByIdList(List<PK> idList) {
		@SuppressWarnings("unchecked")
		PK [] idArray = (PK[]) idList.toArray();
		return listByIdArray(idArray);
	}

	@Override
	public long countAll() {
		BeanInfo cib = BeanInfoBuilder.build(clazz);
		String sql = "select count(*) from " + cib.getTableName();
		QueryRunner run = new QueryRunner();
		ScalarHandler<Long> sh = new ScalarHandler<Long>();
		try {
			return run.query(getConnection(), sql, sh);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long count(String sql, Object... params) {
		QueryRunner run = new QueryRunner();
		ScalarHandler<Long> sh = new ScalarHandler<Long>();
		try {
			return run.query(getConnection(), sql, sh, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<T> executeQuery(String sql, Object... params) {
		List<T> list = new ArrayList<T>();
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(getConnection(), sql, rs, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	@Override
	public T executeQueryOne(String sql, Object... params) {
		List<T> list = null;
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(getConnection(), sql, rs, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(list.size() == 0){
			return null;
		}else if(list.size() == 1){
			return list.get(0);
		}
		throw new RuntimeException("query one but find more than one record,sql->" + sql);
	}

	@Override
	public int executeUpdate(String sql, Object... params) {
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(getConnection(), sql, params);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return num;
	}

	@Override
	public PaginationModel<T> listByPagination(PaginationModel<T> bean) {
		T t = bean.getT();
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
		if(t == null){
			long totalNum = countAll();
			SqlBean sb = SqlBuilder.buildListAllSql(clazz);
			String sql = sb.getSqlBuilder().toString();
			sql = sql + " limit " + (page - 1) * limit + "," + limit;
			List<T> list = executeQuery(sql, sb.getParam().toArray());
			bean.setList(list);
			bean.setTotalNum(totalNum);
			return bean;
		}
		SqlBean countSqlBean = SqlBuilder.buildCountSql(t);
		long totalNum = count(countSqlBean.getSqlBuilder().toString(), countSqlBean.getParam().toArray());
		SqlBean sb = SqlBuilder.buildQuerySql(t);
		String sql = sb.getSqlBuilder().toString();
		sql = sql + " limit " + (page - 1) * limit + "," + limit;
		List<T> list = executeQuery(sql, sb.getParam().toArray());
		bean.setList(list);
		bean.setTotalNum(totalNum);
		return bean;
	}

}

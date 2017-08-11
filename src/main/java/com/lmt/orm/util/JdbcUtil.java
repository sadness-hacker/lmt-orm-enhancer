package com.lmt.orm.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.FieldInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.builder.BeanInfoBuilder;
import com.lmt.orm.builder.SqlBuilder;
import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.exception.SqlRunnerException;

/**
 * 
 * @author ducx
 * @date 2017-04-12
 * jdbc工具类，执行sql
 * 单例
 *
 */
public enum JdbcUtil {
	RUNNER;
	private JdbcUtil(){
		
	}
	public <T> T insert(Connection conn,T t){
		if(t == null){
			return null;
		}
		QueryRunner run = new QueryRunner();
		SqlBean bean = SqlBuilder.buildInsertSql(t.getClass());
		Object [] params = SqlBuilder.buildParamsArray(bean,t);
		try{
			run.update(conn, bean.getSqlBuilder().toString(), params);
			BeanInfo info = BeanInfoBuilder.build(t.getClass());
			if(info.isAutoIncreatmentId()){
				String sql = "SELECT LAST_INSERT_ID()";
				FieldInfo id = info.getIdField();
				Class<?> clazz = id.getType();
				Number number = run.query(conn, sql, new ScalarHandler<Number>());
				Object o = number;
				if(Integer.class.equals(clazz)){
					o = number.intValue();
				}else if(Long.class.equals(clazz)){
					o = number.longValue();
				}
				try {
					MethodUtil.invoke(info.findWriteMethod(id.getFieldName()), t, new Object[]{o});
				} catch (Exception e) {
					throw new SqlRunnerException("id写入set方法执行出错...", e);
				}
			}
		} catch (SQLException e){
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return t;
	}
	
	public <T,PK> T get(Connection conn,Class<T> clazz,PK id){
		if(id == null){
			return null;
		}
		SqlBean bean = SqlBuilder.buildGetByIdSql(clazz);
		ResultSetHandler<T> rs = new BeanHandler<T>(clazz);
		T t = null;
		try {
			QueryRunner run = new QueryRunner();
			t = run.query(conn, bean.getSqlBuilder().toString(), rs, id);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return t;
	}
	

	public <T> T load(Connection conn,Class<T> clazz,T t) {
		if(t == null){
			return null;
		}
		SqlBean bean = SqlBuilder.buildQuerySql(t);
		ResultSetHandler<T> rs = new BeanHandler<T>(clazz);
		t = null;
		try {
			QueryRunner run = new QueryRunner();
			t = run.query(conn, bean.getSqlBuilder().toString(), rs, bean.getParam().toArray());
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return t;
	}
	
	public <T> int update(Connection conn,T t){
		if(t == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildUpdateSql(t.getClass());
		Object[] params = SqlBuilder.buildParamsArray(bean, t);
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(conn, bean.getSqlBuilder().toString(), params);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return num;
	}
	
	public <T> int update(Connection conn,T filterModel,T updateModel){
		if(filterModel == null || updateModel == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildUpdateSql(filterModel, updateModel);
		return executeUpdate(conn, bean.getSqlBuilder().toString(), bean.getParam().toArray());
	}
	
	public <T,PK> int delete(Connection conn,Class<T> clazz,PK id){
		if(id == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildDeleteByIdSql(clazz);
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(conn, bean.getSqlBuilder().toString(), id);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return num;
	}
	
	public <T> int delete(Connection conn,T t){
		if(t == null){
			return 0;
		}
		SqlBean bean = SqlBuilder.buildDeleteSql(t);
		return executeUpdate(conn, bean.getSqlBuilder().toString(), bean.getParam().toArray());
	}
	
	public <T> List<T> listAll(Connection conn,Class<T> clazz){
		SqlBean bean = SqlBuilder.buildListAllSql(clazz);
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		List<T> list = new ArrayList<T>();
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(conn, bean.getSqlBuilder().toString(), rs);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return list;
	}
	
	public <T> List<T> list(Connection conn,Class<T> clazz,T t){
		List<T> list = new ArrayList<T>();
		SqlBean bean = SqlBuilder.buildQuerySql(t);
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(conn, bean.getSqlBuilder().toString(), rs, bean.getParam().toArray());
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return list;
	}
	
	public <T> PaginationModel<T> queryByPagination(Connection conn,Class<T> clazz,PaginationModel<T> bean) {
		if(bean == null){
			return bean;
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
			long totalNum = countAll(conn,clazz);
			SqlBean sb = SqlBuilder.buildListAllSql(clazz);
			String sql = sb.getSqlBuilder().toString();
			sql = sql + " limit " + (page - 1) * limit + "," + limit;
			List<T> list = executeQuery(conn,clazz,sql, sb.getParam().toArray());
			bean.setList(list);
			bean.setTotalNum(totalNum);
			return bean;
		}
		long totalNum = count(conn, t);
		SqlBean sb = SqlBuilder.buildQuerySql(t);
		String sql = sb.getSqlBuilder().toString();
		sql = sql + " limit " + (page - 1) * limit + "," + limit;
		List<T> list = executeQuery(conn,clazz,sql, sb.getParam().toArray());
		bean.setList(list);
		bean.setTotalNum(totalNum);
		return bean;
	}
	
	public <T,PK> List<T> listByIdArray(Connection conn,Class<T> clazz,PK[] idArray){
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
		return executeQuery(conn, clazz, sql, idArray);
	}
	
	public <T,PK> List<T> listByIdList(Connection conn,Class<T> clazz,List<PK> idList){
		@SuppressWarnings("unchecked")
		PK [] idArray = (PK[]) idList.toArray();
		return listByIdArray(conn,clazz,idArray);
	}
	
	public <T> long countAll(Connection conn,Class<T> clazz){
		BeanInfo cib = BeanInfoBuilder.build(clazz);
		String sql = "select count(*) from " + cib.getTableName();
		QueryRunner run = new QueryRunner();
		ScalarHandler<Long> sh = new ScalarHandler<Long>();
		try {
			return run.query(conn, sql, sh);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
	}
	
	public long count(Connection conn,String sql,Object [] params){
		QueryRunner run = new QueryRunner();
		ScalarHandler<Long> sh = new ScalarHandler<Long>();
		try {
			return run.query(conn, sql, sh, params);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
	}
	
	public <T> long count(Connection conn,T t){
		SqlBean countSqlBean = SqlBuilder.buildCountSql(t);
		return count(conn,countSqlBean.getSqlBuilder().toString(), countSqlBean.getParam().toArray());
	}
	
	public <T> List<T> executeQuery(Connection conn,Class<T> clazz,String sql,Object ... params){
		List<T> list = new ArrayList<T>();
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(conn, sql, rs, params);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return list;
	}
	
	public <T> T executeQueryOne(Connection conn,Class<T> clazz,String sql,Object ... params){
		List<T> list = null;
		ResultSetHandler<List<T>> rs = new BeanListHandler<T>(clazz);
		try {
			QueryRunner run = new QueryRunner();
			list = run.query(conn, sql, rs, params);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		if(list.size() == 0){
			return null;
		}else if(list.size() == 1){
			return list.get(0);
		}
		throw new SqlRunnerException("sql执行异常,数据异常，要求返回一个，实际返回多个...");
	}
	
	public <T> int executeUpdate(Connection conn,String sql,Object ... params){
		int num = 0;
		try {
			QueryRunner run = new QueryRunner();
			num = run.update(conn, sql, params);
		} catch (SQLException e) {
			throw new SqlRunnerException("sql执行异常...", e);
		}
		return num;
	}
}

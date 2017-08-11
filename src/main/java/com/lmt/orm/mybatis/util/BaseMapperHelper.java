package com.lmt.orm.mybatis.util;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.exception.SqlRunnerException;
import com.lmt.orm.util.JdbcUtil;

/**
 * 
 * @author ducx
 * @date 2017-07-21
 * mybatis基础mapper接口中的方法执行工具类
 * 单例实现
 *
 */
public enum BaseMapperHelper {
	RUNNER;
	
	private Set<String> allowMethodSet = new HashSet<String>();
	/**
	 * 添加需要拦截的方法
	 */
	private BaseMapperHelper(){
		allowMethodSet.add("insert");
		allowMethodSet.add("get");
		allowMethodSet.add("load");
		allowMethodSet.add("delete");
		allowMethodSet.add("deleteMore");
		allowMethodSet.add("update");
		allowMethodSet.add("countAll");
		allowMethodSet.add("count");
		allowMethodSet.add("list");
		allowMethodSet.add("listAll");
		allowMethodSet.add("queryByPagination");
	}
	
	/**
	 * 检测method是否需要拦截执行
	 * @param method
	 * @return
	 */
	public boolean checkMethod(String name){
		if(name == null){
			return false;
		}
		if(allowMethodSet.contains(name)){
			return true;
		}
		return false;
	}
	
	/**
	 * 执行要拦截的方法，返回jdbc执行结果
	 * @param connection
	 * @param methodName
	 * @param entityClass
	 * @param pkClass
	 * @param params
	 * @return
	 */
	public Object proceed(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		switch (methodName) {
		case "insert":
			return insert(connection, methodName, entityClass, pkClass, params);
		case "get":
			return get(connection, methodName, entityClass, pkClass, params);
		case "load":
			return load(connection, methodName, entityClass, pkClass, params);
		case "delete":
			return delete(connection, methodName, entityClass, pkClass, params);
		case "deleteMore":
			return deleteMore(connection, methodName, entityClass, pkClass, params);
		case "update":
			return update(connection, methodName, entityClass, pkClass, params);
		case "countAll":
			return countAll(connection, methodName, entityClass, pkClass, params);
		case "count":
			return count(connection, methodName, entityClass, pkClass, params);
		case "list":
			return list(connection, methodName, entityClass, pkClass, params);
		case "listAll":
			return listAll(connection, methodName, entityClass, pkClass, params);
		case "queryByPagination":
			return queryByPagination(connection, methodName, entityClass, pkClass, params);
		default:
			throw new SqlRunnerException("方法" + methodName + "不是IBaseMapper中的方法...");
		}
	}
	
	private Object insert(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		Object param = params[0];
		return JdbcUtil.RUNNER.insert(connection, param);
	}
	
	private Object get(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		Object param = params[0];
		return JdbcUtil.RUNNER.get(connection, entityClass, param);
	}
	
	private <T> Object load(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		@SuppressWarnings("unchecked")
		T param = (T) params[0];
		return JdbcUtil.RUNNER.load(connection, entityClass,param);
	}
	
	private <T> Object delete(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		Object param = params[0];
		return JdbcUtil.RUNNER.delete(connection, entityClass, param);
	}
	
	private Object deleteMore(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		Object param = params[0];
		return JdbcUtil.RUNNER.delete(connection, param);
	}
	
	private <T> Object update(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length < 1){
			return null;
		}
		if(params.length == 1){
			Object param = params[0];
			return JdbcUtil.RUNNER.update(connection, param);
		}
		Object filterModel = params[0];
		Object updateModel = params[0];
		return JdbcUtil.RUNNER.update(connection, filterModel, updateModel);
	}
	
	private Object countAll(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		return JdbcUtil.RUNNER.countAll(connection, entityClass);
	}
	
	private Object count(Connection connection,String methodName,Class<?> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		Object param = params[0];
		return JdbcUtil.RUNNER.count(connection, param);
	}
	
	private <T> Object list(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		@SuppressWarnings("unchecked")
		T param = (T) params[0];
		return JdbcUtil.RUNNER.list(connection, entityClass, param);
	}
	
	private <T> Object listAll(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		return JdbcUtil.RUNNER.listAll(connection, entityClass);
	}
	
	private <T> Object queryByPagination(Connection connection,String methodName,Class<T> entityClass,Class<?> pkClass,Object [] params){
		if(params == null || params.length != 1){
			return null;
		}
		@SuppressWarnings("unchecked")
		PaginationModel<T> param = (PaginationModel<T>) params[0];
		return JdbcUtil.RUNNER.queryByPagination(connection, entityClass, param);
	}
	
}

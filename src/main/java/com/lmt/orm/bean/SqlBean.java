package com.lmt.orm.bean;

import java.util.List;

/**
 * 
 * @author ducx
 * @date 2017-07-17
 *
 */
public class SqlBean {

	private StringBuilder sqlBuilder;
	
	private List<Object> param;
	
	private Class<?> clazz;
	
	private List<String> paramNames;

	public StringBuilder getSqlBuilder() {
		return sqlBuilder;
	}

	public void setSqlBuilder(StringBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	public List<Object> getParam() {
		return param;
	}

	public void setParam(List<Object> param) {
		this.param = param;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public List<String> getParamNames() {
		return paramNames;
	}

	public void setParamNames(List<String> paramNames) {
		this.paramNames = paramNames;
	}
	
}

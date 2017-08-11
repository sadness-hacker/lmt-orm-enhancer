package com.lmt.orm.bean;

import java.lang.reflect.Method;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 *
 */
public class FieldInfo {
	
	private String columnName;
	
	private String fieldName;
	
	private Class<?> type;
	
	private Method readMethod;
	
	private Method writeMethod;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}
	
}

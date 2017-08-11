package com.lmt.orm.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 * bean信息类
 *
 */
public class BeanInfo {

	/**
	 * 当前beanInfo对应的class
	 */
	private Class<?> clazz;
	
	/**
	 * 类名
	 */
	private String className;
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 主键属性对应的字段信息
	 */
	private FieldInfo idField;
	
	/**
	 * 是否是自增主键
	 */
	private boolean autoIncreatmentId;
	
	/**
	 * 其他属性对应的bean信息
	 */
	private List<FieldInfo> fieldInfoList;
	
	/**
	 * bean中属性对应的操作方法
	 */
	private Map<String, Method> methodMap = new HashMap<String, Method>();
	
	public BeanInfo(Class<?> clazz){
		this.clazz = clazz;
		this.className = clazz.getName();
		this.autoIncreatmentId = false;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public FieldInfo getIdField() {
		return idField;
	}

	public void setIdField(FieldInfo idField) {
		this.idField = idField;
	}

	public boolean isAutoIncreatmentId() {
		return autoIncreatmentId;
	}

	public void setAutoIncreatmentId(boolean autoIncreatmentId) {
		this.autoIncreatmentId = autoIncreatmentId;
	}

	public List<FieldInfo> getFieldInfoList() {
		return fieldInfoList;
	}

	public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
		this.fieldInfoList = fieldInfoList;
	}

	/**
	 * 根据字段名或对应的数据库列名获取该字段对应的get方法
	 * @param fieldName
	 * @return
	 */
	public Method findReadMethod(String fieldName){
		String key = "get->" + fieldName;
		return this.methodMap.get(key);
	}
	/**
	 * 根据字段名或对应的数据库列名获取该字段对应的set方法
	 * @param fieldName
	 * @return
	 */
	public Method findWriteMethod(String fieldName){
		String key = "set->" + fieldName;
		return this.methodMap.get(key);
	}
	/**
	 * 放入字段对应的get方法到map
	 * @param fieldName
	 * @param method
	 */
	public void putReadMethod(String fieldName,Method method){
		String key = "get->" + fieldName;
		this.methodMap.put(key, method);
	}
	/**
	 * 放入字段对应的set方法到map
	 * @param fieldName
	 * @param method
	 */
	public void putWriteMethod(String fieldName,Method method){
		String key = "set->" + fieldName;
		this.methodMap.put(key, method);
	}
}

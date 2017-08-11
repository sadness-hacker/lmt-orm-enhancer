package com.lmt.orm.hibernate.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.FieldInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.builder.BeanInfoBuilder;
import com.lmt.orm.cache.Cache;

/**
 * 
 * @author ducx
 * @date 2017-07-24
 * 
 *
 */
public class HqlBuilder {

	/**
	 * 根据对象生成查询的hql，注意这里只会过滤null值，非null的值均会加入的查询sql中
	 * @param obj
	 * @return
	 */
	public static SqlBean buildQueryHql(Object obj){
		if(obj == null){
			return null;
		}
		Class<?> clazz = obj.getClass();
		BeanInfo info = BeanInfoBuilder.build(clazz);
		FieldInfo id = info.getIdField();
		String fn = id.getFieldName();
		Method method = info.findReadMethod(fn);
		Object o = null;
		try {
			o = method.invoke(obj);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		StringBuilder nameBuilder = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		int i = 0;
		if(o != null){
			nameBuilder.append(id.getFieldName()).append("=?").append(i);
			i++;
			params.add(o);
		}
		List<FieldInfo> fieldList = info.getFieldInfoList();
		for(FieldInfo field : fieldList){
			o = null;
			method = info.findReadMethod(field.getFieldName());
			try {
				o = method.invoke(obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if(o == null){
				continue;
			}
			nameBuilder.append(" and ").append(field.getFieldName()).append("=?").append(i);
			i++;
			params.add(o);
		}
		String nb = nameBuilder.toString().trim();
		if(nb.startsWith("and")){
			nb = nb.replaceFirst("and", "");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("from ")
		.append(clazz.getSimpleName());
		if(params.size() > 0){
			sb.append(" where ")
			.append(nb);
		}
		SqlBean bean = new SqlBean();
		bean.setParam(params);
		bean.setParamNames(new ArrayList<String>());
		bean.setSqlBuilder(sb);
		return bean;
	}
	/**
	 * 根据过滤条件创建hql统计语句
	 * @param obj
	 * @return
	 */
	public static SqlBean buildCountHql(Object obj){
		if(obj == null){
			return null;
		}
		Class<?> clazz = obj.getClass();
		BeanInfo info = BeanInfoBuilder.build(clazz);
		FieldInfo id = info.getIdField();
		String fn = id.getFieldName();
		Method method = info.findReadMethod(fn);
		Object o = null;
		try {
			o = method.invoke(obj);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		StringBuilder nameBuilder = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		int i = 0;
		if(o != null){
			nameBuilder.append(id.getColumnName()).append("=?").append(i);
			i++;
			params.add(o);
		}
		List<FieldInfo> fieldList = info.getFieldInfoList();
		for(FieldInfo field : fieldList){
			o = null;
			method = info.findReadMethod(field.getFieldName());
			try {
				o = method.invoke(obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if(o == null){
				continue;
			}
			nameBuilder.append(" and ").append(field.getFieldName()).append("=?").append(i);
			i++;
			params.add(o);
		}
		String nb = nameBuilder.toString().trim();
		if(nb.startsWith("and")){
			nb = nb.replaceFirst("and", "");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select ")
		.append(" count(*) ")
		.append(" from ")
		.append(clazz.getSimpleName());
		if(params.size() > 0){
			sb.append(" where ")
			.append(nb);
		}
		SqlBean bean = new SqlBean();
		bean.setParam(params);
		bean.setParamNames(new ArrayList<String>());
		bean.setSqlBuilder(sb);
		return bean;
	}
	
	/**
	 * 根据id删除记录
	 * @param clazz
	 * @return
	 */
	public static SqlBean buildDeleteByIdHql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:delete:by:id:hql";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		FieldInfo id = info.getIdField();
		StringBuilder sb = new StringBuilder();
		sb.append("delete ")
		.append(" from ")
		.append(clazz.getSimpleName())
		.append(" where ")
		.append(id.getFieldName())
		.append(" = ?0");
		List<String> paramsNames = new ArrayList<String>(1);
		paramsNames.add(id.getFieldName());
		bean = new SqlBean();
		bean.setSqlBuilder(sb);
		bean.setParamNames(paramsNames);
		bean.setParam(new ArrayList<Object>());
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
}

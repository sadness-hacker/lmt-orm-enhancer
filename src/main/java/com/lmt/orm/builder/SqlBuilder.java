package com.lmt.orm.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.FieldInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.cache.Cache;
import com.lmt.orm.exception.SqlBuilderException;
import com.lmt.orm.util.MethodUtil;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 *
 */
public class SqlBuilder {

	public static SqlBean buildInsertSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:insert";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		List<FieldInfo> fieldList = info.getFieldInfoList();
		List<String> paramsNames = new ArrayList<String>(fieldList.size() + 1);
		StringBuilder nameBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		if(!info.isAutoIncreatmentId()){
			FieldInfo field = info.getIdField();
			paramsNames.add(field.getColumnName());
			nameBuilder.append("`").append(field.getColumnName()).append("`");
			valueBuilder.append("?");
		}
		for(FieldInfo field : fieldList){
			paramsNames.add(field.getColumnName());
			nameBuilder.append(",`").append(field.getColumnName()).append("`");
			valueBuilder.append(",").append("?");
		}
		String nb = nameBuilder.toString();
		String vb = valueBuilder.toString();
		if(nb.startsWith(",")){
			nb = nb.replaceFirst(",", "");
			vb = vb.replaceFirst(",", "");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ")
		.append(tableName)
		.append(" (")
		.append(nb)
		.append(") values (")
		.append(vb)
		.append(")");
		bean = new SqlBean();
		bean.setSqlBuilder(sb);
		bean.setParamNames(paramsNames);
		bean.setParam(new ArrayList<Object>());
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	public static SqlBean buildUpdateSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:update";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		List<FieldInfo> fieldList = info.getFieldInfoList();
		List<String> paramsNames = new ArrayList<String>(fieldList.size() + 1);
		StringBuilder nameBuilder = new StringBuilder();
		for(FieldInfo field : fieldList){
			paramsNames.add(field.getColumnName());
			nameBuilder.append(",`").append(field.getColumnName()).append("`=?");
		}
		String nb = nameBuilder.toString();
		if(nb.startsWith(",")){
			nb = nb.replaceFirst(",", "");
		}
		FieldInfo id = info.getIdField();
		paramsNames.add(id.getColumnName());
		StringBuilder sb = new StringBuilder();
		sb.append("update ")
		.append(tableName)
		.append(" set ")
		.append(nb)
		.append(" where `")
		.append(id.getColumnName())
		.append("` = ?");
		bean = new SqlBean();
		bean.setSqlBuilder(sb);
		bean.setParamNames(paramsNames);
		bean.setParam(new ArrayList<Object>());
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	public static SqlBean buildDeleteByIdSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:delete:by:id";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		FieldInfo id = info.getIdField();
		StringBuilder sb = new StringBuilder();
		sb.append("delete ")
		.append(" from `")
		.append(tableName)
		.append("` where `")
		.append(id.getColumnName())
		.append("` = ?");
		List<String> paramsNames = new ArrayList<String>(1);
		paramsNames.add(id.getColumnName());
		bean = new SqlBean();
		bean.setSqlBuilder(sb);
		bean.setParamNames(paramsNames);
		bean.setParam(new ArrayList<Object>());
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	public static SqlBean buildListAllSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:list:all";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		FieldInfo id = info.getIdField();
		List<FieldInfo> fieldList = info.getFieldInfoList();
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(id.getColumnName()).append(" as `").append(id.getFieldName()).append("`");
		for(FieldInfo field : fieldList){
			nameBuilder.append(",").append(field.getColumnName()).append(" as `").append(field.getFieldName()).append("`");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select ")
		.append(nameBuilder)
		.append(" from ")
		.append(tableName)
		.append(" order by `")
		.append(id.getColumnName())
		.append("` desc");
		bean = new SqlBean();
		bean.setParamNames(new ArrayList<String>());
		bean.setParam(new ArrayList<Object>());
		bean.setSqlBuilder(sb);
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	/**
	 * 生成根据id获取的语句
	 * @param clazz
	 * @return
	 */
	public static SqlBean buildGetByIdSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:get:by:id";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		FieldInfo id = info.getIdField();
		List<FieldInfo> fieldList = info.getFieldInfoList();
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append("`").append(id.getColumnName()).append("` as `").append(id.getFieldName()).append("`");
		for(FieldInfo field : fieldList){
			nameBuilder.append(",`").append(field.getColumnName()).append("` as `").append(field.getFieldName()).append("`");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select ")
		.append(nameBuilder)
		.append(" from ")
		.append(tableName)
		.append(" where `")
		.append(id.getColumnName())
		.append("` = ?");
		List<String> paramsNames = new ArrayList<String>(1);
		paramsNames.add(id.getColumnName());
		bean = new SqlBean();
		bean.setSqlBuilder(sb);
		bean.setParamNames(paramsNames);
		bean.setParam(new ArrayList<Object>());
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	/**
	 * 根据对象生成查询的sql，注意这里只会过滤null值，非null的值均会加入的查询sql中
	 * @param obj
	 * @return
	 */
	public static SqlBean buildQuerySql(Object obj){
		if(obj == null){
			return null;
		}
		Class<?> clazz = obj.getClass();
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
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
		if(o != null){
			nameBuilder.append("`").append(id.getColumnName()).append("`=?");
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
			nameBuilder.append(" and `").append(field.getColumnName()).append("`=?");
			params.add(o);
		}
		String nb = nameBuilder.toString().trim();
		if(nb.startsWith("and")){
			nb = nb.replaceFirst("and", "");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select ")
		.append(buildResultSql(clazz))
		.append(" from ")
		.append(tableName);
		if(params.size() > 0){
			sb.append(" where ")
			.append(nb);
		}
		sb.append(" order by `")
		.append(id.getColumnName())
		.append("` desc");
		SqlBean bean = new SqlBean();
		bean.setParam(params);
		bean.setParamNames(new ArrayList<String>());
		bean.setSqlBuilder(sb);
		return bean;
	}
	
	public static SqlBean buildCountAllSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":basic:count:all";
		SqlBean bean = Cache.SQL_BEAN_CACHE.get(key);
		if(bean != null){
			return bean;
		}
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ")
		.append(tableName);
		bean = new SqlBean();
		bean.setParamNames(new ArrayList<String>());
		bean.setParam(new ArrayList<Object>());
		bean.setSqlBuilder(sb);
		Cache.SQL_BEAN_CACHE.put(key, bean);
		return bean;
	}
	
	public static SqlBean buildCountSql(Object obj){
		if(obj == null){
			return null;
		}
		Class<?> clazz = obj.getClass();
		BeanInfo info = BeanInfoBuilder.build(clazz);
		String tableName = info.getTableName();
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
		List<String> paramsNames = new ArrayList<String>();
		StringBuilder nameBuilder = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		if(o != null){
			nameBuilder.append("`").append(id.getColumnName()).append("`=?");
			paramsNames.add(id.getFieldName());
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
			nameBuilder.append(" and `").append(field.getColumnName()).append("`=?");
			paramsNames.add(field.getFieldName());
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
		.append(tableName);
		if(params.size() > 0){
			sb.append(" where ")
			.append(nb);
		}
		SqlBean bean = new SqlBean();
		bean.setParam(params);
		bean.setParamNames(paramsNames);
		bean.setSqlBuilder(sb);
		return bean;
	}
	
	public static String buildResultSql(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		return buildResultSql(clazz,null);
	}
	
	public static String buildResultSql(Class<?> clazz,String alias){
		if(clazz == null){
			return null;
		}
		if(StringUtils.isBlank(alias)){
			alias = "";
		}else{
			alias = alias.trim() + ".";
		}
		String key = clazz.getName() + ":result:param:" + alias;
		String sql = Cache.SQL_CACHE.get(key);
		if(sql == null){
			BeanInfo bi = BeanInfoBuilder.build(clazz);
			StringBuilder sb = new StringBuilder();
			FieldInfo fi = bi.getIdField();
			if(fi != null){
				sb.append(alias).append(fi.getColumnName()).append(" as `").append(fi.getFieldName()).append("`");
			}
			List<FieldInfo> list = bi.getFieldInfoList();
			for(FieldInfo field : list){
				sb.append(",").append(alias).append(field.getColumnName()).append(" as `").append(field.getFieldName()).append("`");
			}
			sql = sb.toString();
			if(sql.startsWith(",")){
				sql = sql.replaceFirst(",", "");
			}
			Cache.SQL_CACHE.put(key, sql);
		}
		return sql;
	}
	
	/**
	 * 根据语句bean、条件object生成对应的参数列表
	 * @param bean
	 * @param obj
	 * @return
	 */
	public static Object[] buildParamsArray(SqlBean bean,Object obj){
		if(bean == null || obj == null){
			return null;
		}
		List<String> paramsNames = bean.getParamNames();
		if(paramsNames == null || paramsNames.size() == 0){
			return null;
		}
		BeanInfo info = BeanInfoBuilder.build(obj.getClass());
		Object [] params = new Object[paramsNames.size()];
		int i = 0;
		for(String fieldName : paramsNames){
			Method method = info.findReadMethod(fieldName);
			try {
				Object o  = method.invoke(obj);
				params[i] = o;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			i++;
		}
		return params;
	}
	
	public static <T> SqlBean buildUpdateSql(T filter,T update){
		Class<?> clazz = filter.getClass();
		BeanInfo bi = BeanInfoBuilder.build(clazz);
		FieldInfo idField = bi.getIdField();
		List<FieldInfo> fieldList = bi.getFieldInfoList();
		try{
			List<Object> params = new ArrayList<Object>();
			List<Object> whereParams = new ArrayList<Object>();
			StringBuilder setParamBuilder = new StringBuilder();
			StringBuilder whereBuilder = new StringBuilder();
			if(idField != null){
				Method method = bi.findReadMethod(idField.getFieldName());
				Object o = MethodUtil.invoke(method, update, new Object[]{});
				if(o != null){
					setParamBuilder.append("`").append(idField.getColumnName()).append("`=?");
					params.add(o);
				}
				o = MethodUtil.invoke(method, filter, new Object[]{});
				if(o != null){
					whereBuilder.append("`").append(idField.getColumnName()).append("`=?");
					whereParams.add(o);
				}
			}
			for(FieldInfo f : fieldList){
				Method method = bi.findReadMethod(f.getFieldName());
				Object o = MethodUtil.invoke(method, update, new Object[]{});
				if(o != null){
					setParamBuilder.append(",`").append(f.getColumnName()).append("`=?");
					params.add(o);
				}
				o = MethodUtil.invoke(method, filter, new Object[]{});
				if(o != null){
					whereBuilder.append(" and `").append(f.getColumnName()).append("`=?");
					whereParams.add(o);
				}
			}
			
			String setSql = setParamBuilder.toString().trim();
			if(setSql.startsWith(",")){
				setSql = setSql.replaceFirst(",", "");
			}
			String whereSql = whereBuilder.toString().trim();
			if(whereSql.startsWith("and")){
				whereSql = whereSql.replaceFirst("and", "");
			}
			StringBuilder sb = new StringBuilder();
			sb.append("update ")
			.append(bi.getTableName())
			.append(" set ")
			.append(setSql)
			.append(" where ")
			.append(whereSql);
			SqlBean bean = new SqlBean();
			bean.setSqlBuilder(sb);
			bean.setClazz(clazz);
			params.addAll(whereParams);
			bean.setParam(params);
			bean.setParamNames(new ArrayList<String>());
			return bean;
		}catch(Exception e){
			throw new SqlBuilderException("动态sql生成出错啦！！！", e);
		}
	}
	
	public static <T> SqlBean buildDeleteSql(T t){
		Class<?> clazz = t.getClass();
		BeanInfo bi = BeanInfoBuilder.build(clazz);
		FieldInfo idField = bi.getIdField();
		List<FieldInfo> fieldList = bi.getFieldInfoList();
		try{
			List<Object> params = new ArrayList<Object>();
			StringBuilder whereBuilder = new StringBuilder();
			if(idField != null){
				Method method = bi.findReadMethod(idField.getFieldName());
				Object o = MethodUtil.invoke(method, t, new Object[]{});
				if(o != null){
					whereBuilder.append("`").append(idField.getColumnName()).append("`=?");
					params.add(o);
				}
			}
			for(FieldInfo f : fieldList){
				Method method = bi.findReadMethod(f.getFieldName());
				Object o = MethodUtil.invoke(method, t, new Object[]{});
				if(o != null){
					whereBuilder.append(" and `").append(f.getColumnName()).append("`=?");
					params.add(o);
				}
			}
			String whereSql = whereBuilder.toString().trim();
			if(whereSql.startsWith("and")){
				whereSql = whereSql.replaceFirst("and", "");
			}
			StringBuilder sb = new StringBuilder();
			sb.append("delete from ")
			.append(bi.getTableName())
			.append(" where ")
			.append(whereSql);
			SqlBean bean = new SqlBean();
			bean.setSqlBuilder(sb);
			bean.setClazz(clazz);
			bean.setParam(params);
			bean.setParamNames(new ArrayList<String>());
			return bean;
		}catch(Exception e){
			throw new SqlBuilderException("动态sql生成出错啦！！！", e);
		}
	}
	
}

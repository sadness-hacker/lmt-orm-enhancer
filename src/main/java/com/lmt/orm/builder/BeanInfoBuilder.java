package com.lmt.orm.builder;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.FieldInfo;
import com.lmt.orm.cache.Cache;
import com.lmt.orm.exception.SqlBuilderException;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 * 根据class创建beanInfo
 *
 */
public class BeanInfoBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(BeanInfoBuilder.class);
	
	/**
	 * 根据class创建beanInfo
	 * @param clazz
	 * @return
	 */
	public static BeanInfo build(Class<?> clazz){
		if(clazz == null){
			return null;
		}
		//尝试从缓存中获取beanInfo
		BeanInfo beanInfo = Cache.BEAN_INFO_CACHE.get(clazz.getName());
		if(beanInfo != null){
			return beanInfo;
		}
		//缓存中不存在，创建新的beanInfo,设置对应的表名
		beanInfo = new BeanInfo(clazz);
		beanInfo.setTableName(getTableName(clazz));
		
		//循环处理所有字段
		List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
		List<Field> list = listField(clazz);
		for(Field field : list){
			//判断字段类型，静态字段，final字段，transient字段
			int mod = field.getModifiers();
			if(Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isTransient(mod)){
				continue;
			}
			String fieldName = field.getName();
			PropertyDescriptor desc = null;
			try {
				desc = new PropertyDescriptor(fieldName, clazz);
			} catch (IntrospectionException e) {
				log.error("bean解析出错啦! " + clazz.getName() + " " + fieldName, e);
				throw new SqlBuilderException("bean解析出错啦！ " + clazz.getName() + " " + fieldName, e);
			}
			Method readMethod = desc.getReadMethod();
			Method writeMethod = desc.getWriteMethod();
			//判断加有@Transient注解的字段的字段不进行处理
			Transient tran = getAnnotation(field,readMethod,writeMethod,Transient.class);
			if(tran != null){
				continue;
			}
			//设置访问权限为true,避免反射时安全检查造成性能问题
			field.setAccessible(true);
			readMethod.setAccessible(true);
			writeMethod.setAccessible(true);
			//对非id字段进行处理
			Id id = getAnnotation(field,readMethod,writeMethod,Id.class);
			if(id == null){
				Column column = getAnnotation(field,readMethod,writeMethod,Column.class);
				String columnName = fieldName;
				if(column != null && !StringUtils.isBlank(column.name())){
					columnName = column.name();
				}
				FieldInfo fi = new FieldInfo();
				fi.setColumnName(columnName);
				fi.setFieldName(fieldName);
				fi.setType(clazz);
				fi.setReadMethod(readMethod);
				fi.setWriteMethod(writeMethod);
				fi.setType(field.getType());
				fieldList.add(fi);
				beanInfo.putReadMethod(fieldName, readMethod);
				beanInfo.putReadMethod(columnName, readMethod);
				beanInfo.putWriteMethod(fieldName, writeMethod);
				beanInfo.putWriteMethod(columnName, writeMethod);
				continue;
			//对id字段进行处理
			}else{
				GeneratedValue gv = getAnnotation(field,readMethod,writeMethod,GeneratedValue.class);
				if(gv != null){
					GenerationType t = gv.strategy();
					if(t == GenerationType.AUTO || t == GenerationType.IDENTITY){
						if(field.getType().equals(Integer.class) ||
							field.getType().equals(Long.class) ||
							field.getType().equals(Short.class) ||
							field.getType().equals(Byte.class)){
							beanInfo.setAutoIncreatmentId(true);
						}else{
							log.warn("bean解析出错啦！{} {} {}",clazz.getName(),fieldName," id字段不能把非整数类型设置为自增的");
						}
					}
				}
				String columnName = fieldName;
				Column column = getAnnotation(field,readMethod,writeMethod,Column.class);
				if(column != null && !StringUtils.isBlank(column.name())){
					columnName = column.name();
				}
				FieldInfo fi = new FieldInfo();
				fi.setColumnName(columnName);
				fi.setFieldName(fieldName);
				fi.setType(clazz);
				fi.setReadMethod(readMethod);
				fi.setWriteMethod(writeMethod);
				fi.setType(field.getType());
				beanInfo.setIdField(fi);
				beanInfo.putReadMethod(fieldName, readMethod);
				beanInfo.putReadMethod(columnName, readMethod);
				beanInfo.putWriteMethod(fieldName, writeMethod);
				beanInfo.putWriteMethod(columnName, writeMethod);
			}
		}
		beanInfo.setFieldInfoList(fieldList);
		return beanInfo;
	}
	
	/**
	 * 递归获取class类中的所有字段（包括其分类中的字段）
	 * @param clazz
	 * @return
	 */
	private static List<Field> listField(Class<?> clazz){
		List<Field> list = new ArrayList<Field>();
		Field[] arr = clazz.getDeclaredFields();
		for(Field f : arr){
			list.add(f);
		}
		Class<?> c = clazz.getSuperclass();
		if(c == null || c.equals(Object.class)){
			return list;
		}
		list.addAll(listField(c)) ;
		return list;
	}
	/**
	 * 获取表名
	 * @param clazz
	 * @return
	 */
	private static String getTableName(Class<?> clazz){
		Table table = getTable(clazz);
		String tableName = clazz.getSimpleName();
		if(table != null && !StringUtils.isBlank(table.name())){
			tableName = table.name();
		}
		return tableName;
	}
	/**
	 * 递归获取clazz中的@Table注解（包括父类中的注解）
	 * @param clazz
	 * @return
	 */
	private static Table getTable(Class<?> clazz){
		Table table = clazz.getAnnotation(Table.class);
		if(table == null){
			Class<?> c = clazz.getSuperclass();
			if(c != null && !c.equals(Object.class)){
				return getTable(c);
			}
		}
		return table;
	}
	/**
	 * 根据字段,该字段的readMethod,writeMethod获取指定clazz的注解, 这样获取的比较全面，避免遇注解加在方法上时，获取不到注解的情况
	 * @param field
	 * @param readMethod
	 * @param writeMethod
	 * @param clazz
	 * @return
	 */
	private static <T extends Annotation> T getAnnotation(Field field,Method readMethod,Method writeMethod,Class<T> clazz){
		T t = field.getAnnotation(clazz);
		if(t != null){
			return t;
		}
		t = readMethod.getAnnotation(clazz);
		if(t != null){
			return t;
		}
		t = writeMethod.getAnnotation(clazz);
		return t;
	}
	
}

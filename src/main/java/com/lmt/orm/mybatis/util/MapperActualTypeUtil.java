package com.lmt.orm.mybatis.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author ducx
 * @date 2017-07-21
 * 通过实现com.lmt.orm.mybatis.mapper.IBaseMapper的接口
 * 获取com.lmt.orm.mybatis.mapper.IBaseMapper的泛型类型
 *
 */
public class MapperActualTypeUtil {
	/**
	 * 类型缓存
	 */
	private static final Map<String, Type> typeCache = new ConcurrentHashMap<String, Type>();

	/**
	 * 获取泛型实体类型
	 * @param clazz
	 * @return
	 */
	public static Type getEntityType(Class<?> clazz){
		return getType(clazz,0);
	}
	/**
	 * 获取泛型主键类型
	 * @param clazz
	 * @return
	 */
	public static Type getPKType(Class<?> clazz){
		return getType(clazz,1);
	}
	/**
	 * 根据泛型类，泛型位置获取泛型类型
	 * @param clazz
	 * @param index
	 * @return
	 */
	public static Type getType(Class<?> clazz,int index){
		if(clazz == null){
			return null;
		}
		String key = clazz.getName() + ":" + index;
		Type t = typeCache.get(key);
		if(t != null){
			return t;
		}
		Type [] types = clazz.getGenericInterfaces();
		for(Type type : types){
			if (type.toString().indexOf("com.lmt.orm.mybatis.mapper.IBaseMapper") != -1) {
				ParameterizedType type1 = (ParameterizedType) type;
				Type[] arr = type1.getActualTypeArguments();
				typeCache.put(key, arr[index]);
				return arr[index];
			}
		}
		return null;
	}
	
}

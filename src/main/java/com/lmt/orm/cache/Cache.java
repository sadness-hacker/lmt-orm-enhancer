package com.lmt.orm.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lmt.orm.bean.BeanInfo;
import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.bean.SqlNodeBean;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 * orm内部缓存，缓存beanInfo,sql语句等
 *
 */
public class Cache {

	/**
	 * bean info缓存
	 */
	public static final Map<String,BeanInfo> BEAN_INFO_CACHE = new ConcurrentHashMap<String, BeanInfo>();
	/**
	 * sql namespace 缓存
	 */
	public static final Map<String, Map<String,SqlNodeBean>> SQL_NAMESPACE_CACHE = new ConcurrentHashMap<String, Map<String,SqlNodeBean>>();
	/**
	 * sql bean node 缓存
	 */
	public static final Map<String, SqlNodeBean> SQL_BEAN_NODE_CACHE = new ConcurrentHashMap<String, SqlNodeBean>();
	/**
	 * sql片段缓存
	 */
	public static final Map<String,String> SQL_CACHE = new HashMap<String, String>();
	/**
	 * sql bean缓存
	 */
	public static final Map<String, SqlBean> SQL_BEAN_CACHE = new HashMap<String, SqlBean>();
	
}

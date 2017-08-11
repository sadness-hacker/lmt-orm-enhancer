package com.lmt.orm.xml.ognl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.ClassResolver;
/**
 * 
 * @author ducx
 * @date 2017-07-16
 *
 */
public class OgnlClassResolver implements ClassResolver {
	
	private final Map<String, Class<?>> classMap = new ConcurrentHashMap<String, Class<?>>(100);

	/**
	 * 使用当前线程的classLoader加载class
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Class classForName(String className, Map context)
			throws ClassNotFoundException {
		Class<?> result = null;
	    if ((result = classMap.get(className)) == null) {
	      try {
	          result = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
	      } catch (ClassNotFoundException e1) {
	          if (className.indexOf('.') == -1) {
	             String cn = "java.lang." + className;
	             result = Class.forName(cn, true, Thread.currentThread().getContextClassLoader());
	             classMap.put(cn, result);
	          }
	      }
	      classMap.put(className, result);
	    }
	    return result;
	}

}

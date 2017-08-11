package com.lmt.orm.util;

import java.lang.reflect.Method;

/**
 * 
 * @author ducx
 * @date 2017-04-12
 *
 */
public class MethodUtil {
	
	public static Object invoke(Method method,Object obj,Object ... params) throws Exception{
		return method.invoke(obj, params);
	}
}

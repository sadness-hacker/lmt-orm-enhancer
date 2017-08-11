package com.lmt.orm.xml.ognl;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 *
 */
public class OgnlCache {

	private static final Map<String, Object> expressionCache = new ConcurrentHashMap<String, Object>();
	
	private OgnlCache(){
		
	}
	
	/**
	 * 获取正则表达式计算结果
	 * @param expression
	 * @param root
	 * @return
	 */
	public static Object getValue(String expression,Object root){
		ClassResolver classResolver = new OgnlClassResolver();
		@SuppressWarnings("rawtypes")
		Map context = Ognl.addDefaultContext(root, classResolver, new OgnlContext(classResolver,null,new MemberAccess() {
			
			@Override
			public Object setup(Map context, Object target, Member member,
					String propertyName) {
				return null;
			}
			
			@Override
			public void restore(Map context, Object target, Member member,
					String propertyName, Object state) {
				
			}
			
			@Override
			public boolean isAccessible(Map context, Object target, Member member,
					String propertyName) {
				return true;
			}
		}));
		try {
			return Ognl.getValue(parseExpression(expression), context, root);
		} catch (OgnlException e) {
			throw new RuntimeException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
		}
	}
	
	private static Object parseExpression(String expression) throws OgnlException {
	    Object node = expressionCache.get(expression);
	    if (node == null) {
	      node = Ognl.parseExpression(expression);
	      expressionCache.put(expression, node);
	    }
	    return node;
	}
	
}

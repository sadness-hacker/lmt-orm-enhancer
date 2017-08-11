package com.lmt.orm.xml.ognl;

import java.util.Map;

/**
 * 
 * @author ducx
 * @date 2017-07-20
 * ongl计算结果
 *
 */
public class ExpressionEvaluate {
	/**
	 * 计算ongl表达式结果
	 * @param expression
	 * @param root
	 * @return
	 */
	public static Object evaluateValue(String expression,Object root){
		if(root instanceof Map && expression.indexOf(".") < 0){
			Map<?,?> map = (Map<?, ?>) root;
			return map.get(expression);
		}else{
			return OgnlCache.getValue(expression, root);
		}
	}
}

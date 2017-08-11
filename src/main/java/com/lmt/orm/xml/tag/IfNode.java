package com.lmt.orm.xml.tag;

import java.util.List;

import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.xml.ognl.OgnlCache;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * <if>标签处理器
 *
 */
public class IfNode implements SqlNode {

	private String expression;
	
	private List<SqlNode> childNodeList;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<SqlNode> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<SqlNode> childNodeList) {
		this.childNodeList = childNodeList;
	}

	@Override
	public void apply(SqlBean bean, Object param) {
		if(childNodeList == null || childNodeList.size() == 0){
			return;
		}
		boolean o = (boolean) OgnlCache.getValue(expression, param);
		if(o){
			for(SqlNode node : childNodeList){
				node.apply(bean, param);
			}
		}
	}
	
}

package com.lmt.orm.xml.tag;

import java.util.ArrayList;
import java.util.List;

import com.lmt.orm.bean.SqlBean;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * <set>标签处理器
 *
 */
public class SetNode implements SqlNode{
	
	private static final String TAG = "SET";

	private List<SqlNode> childNodeList;

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
		SqlBean sqlBean = new SqlBean();
		sqlBean.setParam(new ArrayList<Object>());
		sqlBean.setSqlBuilder(new StringBuilder());
		for(SqlNode node : childNodeList){
			node.apply(sqlBean, param);
		}
		String sql = sqlBean.getSqlBuilder().toString().trim();
		if(sql.toLowerCase().startsWith(",")){
			sql = sql.replaceFirst(",", "");
		}
		if(sql.toLowerCase().endsWith(",")){
			sql = sql.substring(0, sql.length() - 1);
		}
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(TAG).append(" ").append(sql).append(" "));
		List<Object> list = bean.getParam();
		list.addAll(sqlBean.getParam());
		bean.setParam(list);
	}
	
}

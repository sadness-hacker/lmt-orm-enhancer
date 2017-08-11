package com.lmt.orm.xml.tag;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lmt.orm.bean.SqlBean;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * <where>标签处理器
 */
public class WhereNode implements SqlNode{
	
	private static final String TAG = "WHERE";
	
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
		if(StringUtils.isBlank(sql)){
			return;
		}
		if(sql.toLowerCase().startsWith("or ")){
			sql = sql.replaceFirst("or ", "");
		}
		if(sql.toLowerCase().endsWith(" or")){
			sql = sql.substring(0, sql.length() - 3);
		}
		if(sql.toLowerCase().startsWith("and ")){
			sql = sql.replaceFirst("and ", "");
		}
		if(sql.toLowerCase().endsWith(" and")){
			sql = sql.substring(0, sql.length() - 4);
		}
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(TAG).append(" ").append(sql));
		List<Object> list = bean.getParam();
		list.addAll(sqlBean.getParam());
		bean.setParam(list);
	}
	
	
}

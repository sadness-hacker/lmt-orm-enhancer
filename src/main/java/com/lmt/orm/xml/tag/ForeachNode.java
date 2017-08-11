package com.lmt.orm.xml.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.xml.ognl.ExpressionEvaluate;

/**
 * 
 * @author ducx
 * @date 2017-07-17
 * foreach标签
 *
 */
public class ForeachNode implements SqlNode{
	
	//item，index，collection，open，separator，close

	private String item;
	
	private String index;
	
	private String collection;
	
	private String open = "(";
	
	private String separator = ",";
	
	private String close = ")";
	
	private List<SqlNode> childNodeList;

	public List<SqlNode> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<SqlNode> childNodeList) {
		this.childNodeList = childNodeList;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	@Override
	public void apply(SqlBean bean, Object param) {
		if(childNodeList != null && childNodeList.size() == 0){
			return;
		}
		Object o = null;
		switch (collection) {
		case "list":
			if(param instanceof List){
				o = param;
			}else if(param instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String,Object> paramMap = (Map<String, Object>) param;
				o = paramMap.get("list") == null ? paramMap.get("0") : paramMap.get("list");
			}else{
				o = ExpressionEvaluate.evaluateValue("list", param);
				if(o == null){
					o = ExpressionEvaluate.evaluateValue("0", param);
				}
			}
			break;
		case "array":
			if(param.getClass().isArray()){
				o = param;
			}else if(param instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String,Object> paramMap = (Map<String, Object>) param;
				o = paramMap.get("array") == null ? paramMap.get("0") : paramMap.get("array");
			}else{
				o = ExpressionEvaluate.evaluateValue("array", param);
				if(o == null){
					o = ExpressionEvaluate.evaluateValue("0", param);
				}
			}
			break;
		default:
			o = ExpressionEvaluate.evaluateValue(collection, param);
			break;
		}
		if(o == null){
			return;
		}
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(open));
		
		if (o instanceof List) {
			List<?> list = (List<?>) o;
			Map<String, Object> itemMap = new HashMap<String, Object>();
			for(Object obj : list){
				itemMap.clear();
				itemMap.put(item, obj);
				for(SqlNode node : childNodeList){
					node.apply(bean, itemMap);
				}
				bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(separator));
			}
		}else if(o instanceof Map){
			Map<?,?> map = (Map<?, ?>) o;
			for(Map.Entry<?, ?> e : map.entrySet()){
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put(index, e.getKey());
				itemMap.put(item, e.getValue());
				for(SqlNode node : childNodeList){
					node.apply(bean, itemMap);
				}
				bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(separator));
			}
		}else{
			Object [] arr = (Object[]) o;
			Map<String, Object> itemMap = new HashMap<String, Object>();
			for(Object obj : arr){
				itemMap.clear();
				itemMap.put(item, obj);
				for(SqlNode node : childNodeList){
					node.apply(bean, itemMap);
				}
				bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(separator));
			}
		}
		
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(close));
	}
}

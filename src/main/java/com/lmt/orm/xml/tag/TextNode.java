package com.lmt.orm.xml.tag;

import java.util.List;
import java.util.Map;

import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.builder.SqlBuilder;
import com.lmt.orm.xml.ognl.OgnlCache;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * 纯文本节点处理器
 *
 */
public class TextNode implements SqlNode {
	/**
	 * 参数化类型
	 */
	public static final String PARAM = "param";
	/**
	 * 字符串拼接类型
	 */
	public static final String CONCAT = "concat";
	/**
	 * 返回字段结果类型
	 */
	public static final String RESULT = "result";
	/**
	 * 纯文本类型
	 */
	public static final String PLAIN = "plain";
	
	private String paramName = "";
	
	private String prefix = "";
	
	private String suffix = "";
	
	private String type;

	private List<TextNode> childNodeList;

	public List<TextNode> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<TextNode> childNodeList) {
		this.childNodeList = childNodeList;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void apply(SqlBean bean, Object param) {
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(prefix));
		List<Object> list = bean.getParam();
		switch (type) {
		case PLAIN:
			break;
		case PARAM:
			bean.setSqlBuilder(bean.getSqlBuilder().append(" ?"));
			list.add(evaluateValue(param));
			bean.setParam(list);
			break;
		case CONCAT:
			bean.setSqlBuilder(bean.getSqlBuilder().append(evaluateValue(param)));
			break;
		case RESULT:
			String alias = null;
			if(paramName != null){
				alias = paramName.trim();
			}
			String sql = SqlBuilder.buildResultSql(bean.getClazz(),alias);
			bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(sql));
			break;
		default:
			break;
		}
		if(childNodeList != null){
			for(SqlNode node : childNodeList){
				node.apply(bean, param);
			}
		}
		bean.setSqlBuilder(bean.getSqlBuilder().append(suffix));
	}
	
	/**
	 * 根据paramName，参数root评估计算的值
	 * @param root
	 * @return
	 */
	private Object evaluateValue(Object root){
		if(root instanceof Map && paramName.indexOf(".") < 0){
			Map<?,?> map = (Map<?, ?>) root;
			return map.get(paramName);
		}else{
			return OgnlCache.getValue(paramName, root);
		}
	}
	
}

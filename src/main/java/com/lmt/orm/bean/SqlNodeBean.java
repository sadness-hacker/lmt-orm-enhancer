package com.lmt.orm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lmt.orm.xml.tag.SqlNode;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 *
 */
public class SqlNodeBean {
	/**
	 * 命名空间
	 */
	private String namespace;
	/**
	 * sql id
	 */
	private String id;
	/**
	 * 综合键值
	 */
	private String key;
	/**
	 * sql前缀
	 */
	private String prefixSql = "";
	/**
	 * sql后缀
	 */
	private String suffixSql = "";
	/**
	 * sql类型(select,insert,update,delete)
	 */
	private String sqlType;
	/**
	 * 返回值类型
	 */
	private Class<?> resultClass;
	/**
	 * 节点列表
	 */
	private List<SqlNode> sqlNodeList;
	
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPrefixSql() {
		return prefixSql;
	}
	public void setPrefixSql(String prefixSql) {
		this.prefixSql = prefixSql;
	}
	public String getSuffixSql() {
		return suffixSql;
	}
	public void setSuffixSql(String suffixSql) {
		this.suffixSql = suffixSql;
	}
	public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	public List<SqlNode> getSqlNodeList() {
		return sqlNodeList;
	}
	public void setSqlNodeList(List<SqlNode> sqlNodeList) {
		this.sqlNodeList = sqlNodeList;
	}
	
	public Class<?> getResultClass() {
		return resultClass;
	}
	public void setResultClass(Class<?> resultClass) {
		this.resultClass = resultClass;
	}
	
	public SqlBean apply(Class<?> resultType,Object param){
		if(param == null){
			param = new HashMap<String, Object>();
		}
		SqlBean bean = new SqlBean();
		if(resultType == null){
			bean.setClazz(resultClass);
			
		}else{
			bean.setClazz(resultType);
		}
		bean.setSqlBuilder(new StringBuilder(prefixSql));
		bean.setParam(new ArrayList<Object>());
		if(sqlNodeList != null && sqlNodeList.size() > 0){
			for(SqlNode node : sqlNodeList){
				node.apply(bean,param);
			}
		}
		bean.setSqlBuilder(bean.getSqlBuilder().append(" ").append(suffixSql));
		return bean;
	}
	
}

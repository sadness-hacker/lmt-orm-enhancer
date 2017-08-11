package com.lmt.orm.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lmt.orm.bean.SqlNodeBean;
import com.lmt.orm.cache.Cache;
import com.lmt.orm.xml.tag.SqlNode;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * xml sql解析器
 *
 */
public class XMLSqlParser {

	public static void parse(List<Resource> resList) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException{
		for(Resource res : resList){
			parse(res);
		}
	}
	
	public static void parse(Resource res) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException{
		if(!res.isReadable() || !res.getFilename().toLowerCase().endsWith(".xml")){
			return;
		}
		InputStream is = res.getInputStream();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(is);
		if(!doc.hasChildNodes()){
			return;
		}
		String docType = doc.getDoctype().getName();
		if(!"mapper".equalsIgnoreCase(docType)){
			return;
		}
		NodeList nodeList = doc.getElementsByTagName("mapper");
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			parseMapper(node);
		}
		is.close();
	}
	
	private static void parseMapper(Node node) throws ClassNotFoundException{
		String namespace = node.getAttributes().getNamedItem("namespace").getTextContent();
		Map<String, SqlNodeBean> nsMap = Cache.SQL_NAMESPACE_CACHE.get(namespace);
		if(nsMap == null){
			nsMap = new ConcurrentHashMap<String, SqlNodeBean>();
		}
		NodeList nodeList = node.getChildNodes();
		for(int i=0;i<nodeList.getLength();i++){
			Node n = nodeList.item(i);
			String name = n.getNodeName();
			switch (name) {
			case "sql":
				parseSql(n,namespace,nsMap,"sql");
				break;
			case "select":
				parseSql(n,namespace,nsMap,"select");
				break;
			case "insert":
				parseSql(n,namespace,nsMap,"insert");
				break;
			case "update":
				parseSql(n,namespace,nsMap,"update");
				break;
			case "delete":
				parseSql(n,namespace,nsMap,"delete");
				break;
			default:
				break;
			}
		}
	}
	
	private static SqlNodeBean parseSql(Node sqlNode,String namespace,Map<String,SqlNodeBean> nsMap,String type) throws ClassNotFoundException{
		String id = sqlNode.getAttributes().getNamedItem("id").getNodeValue();
		String key = namespace + "." + id;
		if(nsMap.containsKey(id)){
			return null;
		}
		SqlNodeBean bean = new SqlNodeBean();
		bean.setId(id);
		bean.setNamespace(namespace);
		bean.setKey(key);
		bean.setSqlType(type);
		String resultType = null;
		if(sqlNode.getAttributes().getNamedItem("resultType") != null){
			resultType = sqlNode.getAttributes().getNamedItem("resultType").getNodeValue();
			if(!StringUtils.isBlank(resultType)){
				Class<?> clazz = Class.forName(resultType);
				bean.setResultClass(clazz);
			}
		}
		NodeList nodeList = sqlNode.getChildNodes();
		List<SqlNode> childNodeList = new ArrayList<SqlNode>();
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			SqlNode sn = NodeParser.parse(node);
			childNodeList.add(sn);
		}
		bean.setSqlNodeList(childNodeList);
		nsMap.put(id, bean);
		Cache.SQL_BEAN_NODE_CACHE.put(key, bean);
		return bean;
	}
	
}

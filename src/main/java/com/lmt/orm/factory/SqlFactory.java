package com.lmt.orm.factory;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.bean.SqlNodeBean;
import com.lmt.orm.cache.Cache;
import com.lmt.orm.common.util.Scan;
import com.lmt.orm.xml.parser.XMLSqlParser;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 *
 */
public class SqlFactory {

	private String [] basePackage;
	
	@PostConstruct
	public void init() throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException{
		if(basePackage == null || basePackage.length == 0){
			return;
		}
		for(String pkg : basePackage){
			String s = "classpath*:/" + pkg.replace(".", "/") + "**/*.xml";
			List<Resource> resList = Scan.scanResource(s);
			XMLSqlParser.parse(resList);
		}
	}

	public String[] getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String[] basePackage) {
		this.basePackage = basePackage;
	}
	
	public SqlBean getBean(String namespace,String id,Class<?> resultType,Object param){
		String key = namespace + "." + id;
		return getBean(key,resultType,param);
	}
	
	public SqlBean getBean(Class<?> clazz,String id,Class<?> resultType,Object param){
		String key = clazz.getName() + "." + id;
		return getBean(key,resultType,param);
	}
	
	public SqlBean getBean(String key,Class<?> resultType,Object param){
		SqlNodeBean bean = Cache.SQL_BEAN_NODE_CACHE.get(key);
		return bean.apply(resultType, param);
	}
	
}

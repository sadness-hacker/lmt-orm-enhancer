package com.lmt.orm.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 * 
 * @author ducx
 * @date 2017-07-15
 * 扫描加载class或资源的工具类
 *
 */
public class Scan {
	
	private static final Logger log = LoggerFactory.getLogger(Scan.class);

	/**
	 * 扫描加载指定路径下的所有class
	 * @param patterPaths
	 * @return
	 */
	public static Set<Class<?>> scan(String ... patterPaths){
		Set<Class<?>> set = new HashSet<Class<?>>();
		if(patterPaths == null || patterPaths.length == 0){
			return set;
		}
		ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(rpr);
		for(String s : patterPaths){
			if (!s.toLowerCase().endsWith("*.class")) {
				if(s.endsWith("/")){
					s = s + "*.class";
				}else{
					s = s + "/*.class";
				}
				
			}
			try {
				Resource [] arr = rpr.getResources(s);
				for(Resource res : arr){
					if(res.isReadable()){
						MetadataReader reader = readerFactory.getMetadataReader(res);
						String className = reader.getClassMetadata().getClassName();
						set.add(Class.forName(className));
						log.info("scan find class : {}",className);
					}
				}
			} catch (IOException e) {
				log.error("class加载出错喽！路径：" + s, e);
			} catch (ClassNotFoundException e) {
				log.error("class加载出错啦！路径：" + s, e);
			}
		}
		return set;
	}
	
	/**
	 * 加载扫描指定路径下的所有资源
	 * @param patterPaths
	 * @return
	 */
	public static List<Resource> scanResource(String ... patterPaths){
		List<Resource> list = new ArrayList<Resource>();
		ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
		for(String s : patterPaths){
			try {
				Resource[] arr = rpr.getResources(s);
				for(Resource res : arr){
					list.add(res);
				}
			} catch (IOException e) {
				log.error("文件资源加载出错喽！路径：" + s, e);
			}
			
		}
		return list;
	}
	
	public static void main(String [] args){
		String path = "classpath*:/com/**/*";
		scan(path);
	}
	
}

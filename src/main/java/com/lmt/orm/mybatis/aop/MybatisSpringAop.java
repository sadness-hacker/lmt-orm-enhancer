package com.lmt.orm.mybatis.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.binding.MapperProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.lmt.orm.mybatis.util.BaseMapperHelper;
import com.lmt.orm.mybatis.util.MapperActualTypeUtil;

/**
 * 
 * @author ducx
 * @date 2017-07-21
 * MybatisSpringAop基于spring的mybatis拦截器，配置拦截处理通用mapper接口中的方法，减少xml编写
 *
 */
public class MybatisSpringAop implements MethodInterceptor {

	/**
	 * 注入dataSource
	 */
	@Resource
	private DataSource dataSource;
	/**
	 * MapperProxy.class.getField("mapperInterface")
	 * 对应MapperProxy中的mapperInterface字段
	 */
	private Field mapperInterfaceField;
	
	public MybatisSpringAop() {
		try {
			mapperInterfaceField = MapperProxy.class.getDeclaredField("mapperInterface");
			mapperInterfaceField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取当前线程对应的connection
	 * @return
	 */
	private Connection getCurrentConnection(){
		return DataSourceUtils.getConnection(dataSource);
	}
	
	/**
	 * 执行拦截到的方法
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		InvocationHandler handler = Proxy.getInvocationHandler(invocation.getThis());
		if(handler instanceof MapperProxy){
			Method method = invocation.getMethod();
			String methodName = method.getName();
			if(!BaseMapperHelper.RUNNER.checkMethod(methodName)){
				return invocation.proceed();
			}
			Class<?> clazz = (Class<?>) mapperInterfaceField.get(handler);
			Class<?> entityClass = (Class<?>) MapperActualTypeUtil.getEntityType(clazz);
			Class<?> pkClass = (Class<?>) MapperActualTypeUtil.getPKType(clazz);
			Object [] params = invocation.getArguments();
			Connection conn = getCurrentConnection();
			return BaseMapperHelper.RUNNER.proceed(conn, methodName, entityClass, pkClass, params);
		}
		return invocation.proceed();
	}

}

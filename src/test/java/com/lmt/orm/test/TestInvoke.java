package com.lmt.orm.test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.lmt.orm.test.model.User;

/**
 * 
 * @author ducx
 * @date 2017-07-25
 * 测试反射，invoke，get,set执行时间差异
 *
 */
public class TestInvoke {

	public static void main(String [] args) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, ClassNotFoundException{

		User user = new User();
		user.setUsername("ducx");
		String email = "adu003@163.com";
		PropertyDescriptor emailPd = new PropertyDescriptor("email", User.class);
		PropertyDescriptor usernamePd = new PropertyDescriptor("username", User.class);
		Field field = User.class.getDeclaredField("username");
		field.setAccessible(true);
		Method readMethod  = usernamePd.getReadMethod();
		readMethod.setAccessible(true);
		int num = 10000000;
		long t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			user.getUsername();
		}
		System.out.println((System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			readMethod.invoke(user);
		}
		System.out.println((System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			field.get(user);
		}
		System.out.println((System.currentTimeMillis() - t1));
		
		Method writeMethod = emailPd.getWriteMethod();
		writeMethod.setAccessible(true);
		t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			user.setEmail(email);
		}
		System.out.println((System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			writeMethod.invoke(user, new Object[]{email});
		}
		System.out.println((System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			field.set(user, email);
		}
		System.out.println((System.currentTimeMillis() - t1));
		
	}
	
}

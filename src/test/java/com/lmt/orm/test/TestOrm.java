package com.lmt.orm.test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.lmt.orm.bean.SqlBean;
import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.factory.SqlFactory;
import com.lmt.orm.test.model.User;
import com.lmt.orm.test.mybatis.service.IUserService;

/**
 * 
 * @author ducx
 * @date 2017-07-20
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations={"classpath:applicationContext.xml"}) 
public class TestOrm {

	@Resource
	private IUserService userService;
	
	@Resource
	private SqlFactory sqlFactory;
	
	@Resource
	private SqlSessionFactoryBean sqlSessionFactoryBean;
	
//	@Test
	public void insert(){
		User user = new User();
		user.setUsername("ducx");
		user.setEmail("adu003@163.com");
		user.setCreateTime(new Date());
		user.setPassword("0809");
		user.setPhone("18789559678");
		user.setRealname("杜成宪");
		user.setSalt("109080");
		user = userService.insert(user);
		System.out.println(user.getId());
	}
	
//	@Test
	public void get(){
		userService.get(1);
		userService.get(1);
		userService.get(1);
		long t = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			userService.get(1);
		}
		System.out.println("get->" + (System.currentTimeMillis() - t));
	}
	
	@Test
	public void getByMapper(){
		userService.getByMapper(1);
		userService.getByMapper(1);
		User u = userService.getByMapper(1);
		long t = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			userService.getByMapper(1);
		}
		System.out.println("getByMapper->" + (System.currentTimeMillis() - t));
		System.out.println(u.getId() + " " + u.getUsername() + " " + u.getEmail());
	}
	
	@Test
	public void getByMapper1(){
		User u = userService.getByMapper(1);
		System.out.println(u.getId() + " " + u.getUsername() + " " + u.getEmail());
	}
	
	@Test
	public void loadByMapper(){
		User user = new User();
		user.setId(1);
		User u = userService.load(user);
		userService.load(user);
		userService.load(user);
		long t = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			userService.load(user);
		}
		System.out.println("loadByMapper->" + (System.currentTimeMillis() - t));
		System.out.println(u.getId() + " " + u.getUsername() + " " + u.getEmail());
	}
	
	@Test
	public void get1(){
		User u = userService.get(1);
		userService.get(1);
		userService.get(1);
		long t = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			userService.get(1);
		}
		System.out.println("get1->" + (System.currentTimeMillis() - t));
		System.out.println(u.getId() + " " + u.getUsername() + " " + u.getEmail());
	}
	
//	@Test
	public void getBySqlBean() throws ClassNotFoundException, IOException, ParserConfigurationException, SAXException{
		long t = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			User u = new User();
			u.setId(1);
			SqlBean sb = sqlFactory.getBean("com.lmt.orm.test.mapper.UserMapper", "get", null, u);
			
		}
		System.out.println("getBySqlBean->" + (System.currentTimeMillis() - t));
	}
	
	@Test
	public void listByPageModel(){
		User user = new User();
		user.setUsername("ducx");
		PaginationModel<User> pageModel = new PaginationModel<User>();
		pageModel.setT(user);
		pageModel.setLimit(3);
		pageModel.setCurrPage(2);
		pageModel = userService.listByPage(pageModel);
		List<User> list = pageModel.getList();
		for(User u : list){
			System.out.println(u.getId() + " " + u.getUsername() + " " + u.getEmail());
		}
	}
	
}

package com.lmt.orm.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.test.hibernate.service.IHUserService;
import com.lmt.orm.test.model.User;

/**
 * 
 * @author ducx
 * @date 2017-07-24
 * 测试hibernate的扩展
 *
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations={"classpath:applicationContext.xml"}) 
public class TestHibernate {

	@Resource
	private IHUserService hUserService;
	
	public void init(){
//		hUserService.countAll();
//		hUserService.delete(id);
//		hUserService.get(id);
//		hUserService.insert(t);
//		hUserService.listAll();
//		hUserService.listByIdList(idList)
//		hUserService.load(id)
//		hUserService.queryByPagination(paginationModel);
//		hUserService.update(t);
	}
	
	@Test
	public void countAll(){
		long num = hUserService.countAll();
		System.out.println("countAll->" + num);
	}
	
	@Test
	public void get(){
		User user = hUserService.get(1);
		System.out.println("get->" + user.getId() + "->" + user.getUsername());
	}
	
	@Test
	@Rollback(value=false)
	public void insert(){
		long num1 = hUserService.countAll();
		User user = new User();
		user.setCreateTime(new Date());
		user.setEmail("a@adu.com");
		user.setPassword("9527");
		user.setPhone("18789551111");
		user.setRealname("rn");
		user.setSalt("salt");
		user.setUsername("ducx");
		int id = hUserService.insert(user);
		System.out.println("insert->" + id);
		long num2 = hUserService.countAll();
		System.out.println("insert->num->" + num1 + "->" + num2);
	}
	
	@Test
	public void listAll(){
		List<User> list = hUserService.listAll();
		for(User user : list){
			System.out.println("listAll->" + user.getId() + "->" + user.getUsername());
		}
	}
	
	@Test
	public void list(){
		User u = new User();
		u.setUsername("ducx");
		List<User> list = hUserService.list(u);
		for(User user : list){
			System.out.println("list->" + user.getId() + "->" + user.getUsername());
		}
	}
	
	@Test
	public void listByIdList(){
		List<Integer> idList = new ArrayList<Integer>();
		idList.add(1);
		idList.add(2);
		idList.add(3);
		List<User> list = hUserService.listByIdList(idList);
		for(User user : list){
			System.out.println("listByIdList->" + user.getId() + "->" + user.getUsername());
		}
	}
	
	@Test
	public void load(){
		User user = hUserService.load(1);
		System.out.println("load->" + user.getId() + "->" + user.getUsername());
	}
	
	@Test
	@Rollback(value=false)
	public void update(){
		User user = hUserService.get(1);
		if(user != null){
			user.setCreateTime(new Date());
			String pwd = user.getPassword();
			user.setPassword(String.valueOf(Math.random()));
			hUserService.update(user);
			System.out.println("update->" + pwd + "->" + user.getPassword());
		}
	}
	
	@Test
	public void delete(){
		long num1 = hUserService.countAll();
		User u = new User();
		u.setId(6);
		u.setUsername("ducx");
		hUserService.delete(u);
		long num2 = hUserService.countAll();
		System.out.println("delete->num->" + num1 + "->" + num2);
	}
	
	@Test
	public void deleteById(){
		long num1 = hUserService.countAll();
		int num = hUserService.deleteById(6);
		System.out.println("deleteById->num->" + num);
		long num2 = hUserService.countAll();
		System.out.println("deleteById->num->" + num1 + "->" + num2);
	}
	
	@Test
	public void queryByPagination(){
		PaginationModel<User> model = new PaginationModel<User>();
		User u = new User();
		u.setUsername("ducx");
		model.setT(u);
		model.setLimit(2);
		model = hUserService.queryByPagination(model);
		System.out.println("queryByPagination->num->" + model.getTotalNum() + "->totalPage->" + model.getTotalPage());
		List<User> list = model.getList();
		for(User user : list){
			System.out.println("queryByPagination->" + user.getId() + "->" + user.getUsername());
		}
	}
	
}

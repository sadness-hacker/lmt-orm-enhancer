package com.lmt.orm.test.mybatis.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.common.serivce.BaseService;
import com.lmt.orm.test.jdbc.dao.UserDao;
import com.lmt.orm.test.model.User;
import com.lmt.orm.test.mybatis.mapper.UserMapper;
/**
 * 
 * @author ducx
 * @date 2017-07-20
 *
 */
@Service
public class UserService extends BaseService<UserDao, User, Integer> implements IUserService {

	@Resource
	private UserMapper userMapper;
	
	public User getByMapper(int id){
		double round = Math.random();
		User u = new User();
		u.setId(id);
		return userMapper.get(u,round);
	}
	
	public User load(User user){
		return userMapper.load(user);
	}
	
	public User get(int id){
		return userMapper.get(id);
	}

	@Override
	public PaginationModel<User> listByPage(PaginationModel<User> pageModel) {
		userMapper.listByPage(pageModel);
		return pageModel;
	}
}

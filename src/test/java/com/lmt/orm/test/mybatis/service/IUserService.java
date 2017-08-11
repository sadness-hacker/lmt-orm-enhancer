package com.lmt.orm.test.mybatis.service;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.common.serivce.IBaseService;
import com.lmt.orm.test.jdbc.dao.UserDao;
import com.lmt.orm.test.model.User;

/**
 * 
 * @author ducx
 * @date 2017-07-20
 *
 */
public interface IUserService extends IBaseService<UserDao, User, Integer> {

	public User getByMapper(int id);
	
	public PaginationModel<User> listByPage(PaginationModel<User> pageModel);
	
}

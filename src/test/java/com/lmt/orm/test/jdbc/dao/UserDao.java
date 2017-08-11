package com.lmt.orm.test.jdbc.dao;

import org.springframework.stereotype.Repository;

import com.lmt.orm.common.dao.BaseDao;
import com.lmt.orm.test.model.User;
/**
 * 
 * @author ducx
 * @date 2017-07-20
 *
 */
@Repository
public class UserDao extends BaseDao<User, Integer> implements IUserDao {

}

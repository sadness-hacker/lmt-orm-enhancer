package com.lmt.orm.test.hibernate.dao;

import org.springframework.stereotype.Repository;

import com.lmt.orm.hibernate.dao.HibernateBaseDao;
import com.lmt.orm.test.model.User;
/**
 * 
 * @author ducx
 * @date 2017-07-24
 *
 */
@Repository
public class HUserDao extends HibernateBaseDao<User, Integer> {

}

package com.lmt.orm.test.hibernate.service;

import com.lmt.orm.hibernate.service.IHibernateBaseService;
import com.lmt.orm.test.hibernate.dao.HUserDao;
import com.lmt.orm.test.model.User;
/**
 * 
 * @author ducx
 * @date 2017-07-24
 *
 */
public interface IHUserService extends IHibernateBaseService<HUserDao, User, Integer> {

}

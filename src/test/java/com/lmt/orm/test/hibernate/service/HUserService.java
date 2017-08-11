package com.lmt.orm.test.hibernate.service;

import org.springframework.stereotype.Service;

import com.lmt.orm.hibernate.service.HibernateBaseService;
import com.lmt.orm.test.hibernate.dao.HUserDao;
import com.lmt.orm.test.model.User;
/**
 * 
 * @author ducx
 * @date 2017-07-24
 *
 */
@Service
public class HUserService extends HibernateBaseService<HUserDao, User, Integer> implements IHUserService {

}

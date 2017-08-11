package com.lmt.orm.test.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import com.lmt.orm.common.model.PaginationModel;
import com.lmt.orm.mybatis.mapper.IBaseMapper;
import com.lmt.orm.test.model.User;

/**
 * 
 * @author ducx
 * @date 2017-07-20
 *
 */
@MapperScan
public interface UserMapper extends IBaseMapper<User, Integer> {
	
	public User get(@Param(value="user") User user,@Param(value="round") double round);
	
	public List<User> listByPage(PaginationModel<User> pageModel);

}

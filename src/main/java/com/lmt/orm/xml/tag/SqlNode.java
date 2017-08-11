package com.lmt.orm.xml.tag;

import com.lmt.orm.bean.SqlBean;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 *
 */
public interface SqlNode {

	/**
	 * 执行编译出动态sql
	 * @param bean
	 * @param paramMap
	 */
	public void apply(SqlBean bean, Object param);

}

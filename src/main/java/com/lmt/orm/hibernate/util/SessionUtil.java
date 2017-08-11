package com.lmt.orm.hibernate.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 * 
 * @author ducx
 * @date 2017-07-23
 *
 */
public class SessionUtil {

	/**
	 * 通过session获取jdbc连接
	 * @param session
	 * @return
	 */
	public static Connection getConnection(Session session){
		W work = new W();
		session.doWork(work);
		return work.getConnection();
	}
	
}

class W implements Work{

	private Connection connection;
	
	@Override
	public void execute(Connection connection) throws SQLException {
		this.connection = connection;
	}
	
	public Connection getConnection(){
		return this.connection;
	}
	
}

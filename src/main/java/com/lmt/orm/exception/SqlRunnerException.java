package com.lmt.orm.exception;
/**
 * 
 * @author ducx
 * @date 2017-07-21
 * sql执行错误异常
 *
 */
public class SqlRunnerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SqlRunnerException(){
		super();
	}
	
	public SqlRunnerException(String msg){
		super(msg);
	}
	
	public SqlRunnerException(Throwable e){
		super(e);
	}
	
	public SqlRunnerException(String msg,Throwable e){
		super(msg,e);
	}

}

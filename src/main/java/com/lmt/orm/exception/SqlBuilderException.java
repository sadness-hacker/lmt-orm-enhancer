package com.lmt.orm.exception;
/**
 * 
 * @author ducx
 * @date 2017-07-21
 * sql执行错误异常
 *
 */
public class SqlBuilderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SqlBuilderException(){
		super();
	}
	
	public SqlBuilderException(String msg){
		super(msg);
	}
	
	public SqlBuilderException(Throwable e){
		super(e);
	}
	
	public SqlBuilderException(String msg,Throwable e){
		super(msg,e);
	}

}

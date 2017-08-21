package com.lmt.orm.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页查询bean
 * @author ducx
 *
 * @param <T>
 */
public class PaginationModel<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页码
	 */
	private int currPage = 1;
	/**
	 * 总页数
	 */
	private int totalPage = 0;
	/**
	 * 每页数量
	 */
	private int limit = 10;
	/**
	 * 记录总数
	 */
	private long totalNum = 0;
	/**
	 * 查询实体
	 */
	private T t;
	/**
	 * 其他参数map
	 */
	private Map<String, Object> paramsMap;
	/**
	 * 结果列表
	 */
	private List<T> list;
	/**
	 * 是否开启自动分页查寻
	 * Mybatis分页插件中有用到，默认为true，分页查寻
	 * 设置为false时会关闭mybatis插件自动分页查寻
	 */
	private boolean autoPage = true;
	
	public int getCurrPage() {
		if(currPage < 1){
			currPage = 1;
		}
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	
	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getLimit() {
		if(limit < 1){
			this.limit = 10;
		}
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getTotalNum() {
		if(totalNum < 0){
			totalNum = 0;
		}
		return totalNum;
	}

	public void setTotalNum(long totalNum) {
		if(limit < 1){
			this.limit = 10;
		}
		totalPage = (int) (totalNum / limit);
		if(totalNum % limit > 0){
			this.totalPage = this.totalPage + 1;
		}
		this.totalNum = totalNum;
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public boolean isAutoPage() {
		return autoPage;
	}

	public void setAutoPage(boolean autoPage) {
		this.autoPage = autoPage;
	}
	
}

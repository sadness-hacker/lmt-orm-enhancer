package com.lmt.orm.mybatis.interceptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmt.orm.common.model.PaginationModel;


/**
 * 
 * @author ducx
 * @date 2017-07-26
 * Mybatis分页查寻拦截器,
 * 拦截参数含有PaginationModel的查寻
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class PageInteceptor implements Interceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(PageInteceptor.class);
	
	/**
	 * BoundSql中的sql字段对应的Field
	 */
	private Field sqlField;
	/**
	 * 执行拦截操作
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//获取PaginationModel参数
		Object [] args = invocation.getArgs();
		Object param = args[1];
		PaginationModel<?> pageModel = findPaginationModelFromParam(param);
		//不存在PaginationModel参数则不是分页查寻
		if(pageModel == null || !pageModel.isAutoPage()){
			return invocation.proceed();
		}
		//存在PaginationModel参数表明是分页查寻，则进行分页查寻
		MappedStatement mappedStatement = (MappedStatement) args[0];
		RowBounds rowBounds = (RowBounds) args[2];
		ResultHandler resultHandler = (ResultHandler) args[3];
		Executor executor = (Executor) invocation.getTarget();
		CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if(args.length == 4){
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(param);
            cacheKey = executor.createCacheKey(mappedStatement, param, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
		
		Connection conn = executor.getTransaction().getConnection();
		String sql = boundSql.getSql();
		logger.debug("默认sql:{}",sql);
		//查寻总记录数
		String countSql = generateCountSql(sql);
		logger.debug("统计sql:{}",countSql);
		PreparedStatement stat = conn.prepareStatement(countSql);
		setParameters(stat, mappedStatement, boundSql, boundSql.getParameterObject());
		ResultSet rs = stat.executeQuery();
		int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        stat.close();
        pageModel.setTotalNum(count);
        //分页查寻
        sql = generatePageSql(sql, pageModel);
        logger.debug("分页sql:{}",sql);
        sqlField.set(boundSql, sql);
        Object o = executor.query(mappedStatement, param, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
        //将结果设置到传入的PaginationModel中的list字段
        if(o instanceof List){
        	try{
        		pageModel.setList((List) o);
        	} catch (Exception e){
        		logger.warn("查寻结果放入pageModel出错啦！", e);
        	}
        }
		return o;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 初始化获取BoundSql中的sql字段的field
	 */
	@Override
	public void setProperties(Properties properties) {
		try {
			sqlField = BoundSql.class.getDeclaredField("sql");
			sqlField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取分页查寻PaginationModel对象
	 * @param param
	 * @return
	 */
	private PaginationModel<?> findPaginationModelFromParam(Object param){
		if(param == null){
			return null;
		}
		if(param instanceof PaginationModel){
			return (PaginationModel<?>) param;
		}
		if(param instanceof Map){
			Map<?, ?> map = (Map<?, ?>) param;
			for(Object o : map.values()){
				if(o instanceof PaginationModel){
					return (PaginationModel<?>) o;
				}
			}
		}
		return null;
	}

	/**
	 * 转换为统计sql
	 * @param sql
	 * @return
	 */
	private String generateCountSql(String sql){
		String tmp = sql.toLowerCase();
		int si = tmp.indexOf("select");
		int fi = tmp.indexOf("from");
		return sql.substring(0, si + 6) + " count(*) " + sql.substring(fi);
	}
	/**
	 * 生成分页语句
	 * @param sql
	 * @param pageModel
	 * @return
	 */
	private String generatePageSql(String sql,PaginationModel<?> pageModel){
		 int start = (pageModel.getCurrPage() - 1) * pageModel.getLimit();
	     sql = sql + " limit " + start + "," + pageModel.getLimit();
	     return sql;
	}
	
	/**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
	private void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null: configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)&& boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter "+ propertyName + " of statement "+ mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

}

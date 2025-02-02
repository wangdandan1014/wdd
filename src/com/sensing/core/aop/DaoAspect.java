package com.sensing.core.aop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sensing.core.utils.BaseController;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import com.sensing.core.utils.time.DateUtil;

/**
 * sql打印记录
 * <p>Title: DaoAspect</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2019年6月27日
 * @version 1.0
 */
@Aspect
@Component
public class DaoAspect {

    private static final Log log = LogFactory.getLog(DaoAspect.class);

    @Around("execution(* com.sensing.core.dao.*.*(..))")
    public Object daoAround(ProceedingJoinPoint point) throws Throwable {
        ApplicationContext ctx_bean = ContextLoader.getCurrentWebApplicationContext();
        if (ctx_bean == null) {
            return point.proceed(point.getArgs());
        }
        SqlSessionFactory readOnlyMySqlSessionFactory = (SqlSessionFactory) ctx_bean.getBean("sqlSessionFactory");
        if (readOnlyMySqlSessionFactory == null) {
            return point.proceed(point.getArgs());
        }
        String sql = before(point, readOnlyMySqlSessionFactory);
        Object proceed;
        if (BaseController.isDebug) {
            long l1 = System.currentTimeMillis();
            proceed = point.proceed(point.getArgs());
            long l2 = System.currentTimeMillis();
            sql = sql.replaceAll("\n", " ");
            String[] sqlArrs = sql.split("\\s");
    		String sqlShow = "";
    		for (String string : sqlArrs) {
    			if (StringUtils.isNotEmpty(string)) {
    				sqlShow += (string.trim()+" ");
    			}
    		}
            if (l2 - l1 > 5000) {
                log.error("sql执行时间较慢,时间是:" + (l2 - l1) + "ms;sql:" + sqlShow);
            } else {
//                log.info("sql耗时:" + (l2 - l1) + "ms;sql:" + sqlShow);
            }
        } else {
            proceed = point.proceed(point.getArgs());
        }
        return proceed;
    }

    public static String before(JoinPoint point, SqlSessionFactory readOnlyMySqlSessionFactory) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取sql参数
        SMethodSignature method = new SMethodSignature(readOnlyMySqlSessionFactory.getConfiguration(), signature.getMethod());
        Object tempParam = method.convertArgsToSqlCommandParam(point.getArgs());
        Object parameterObject = wrapCollection(tempParam);

//	        String sqlid=signature.getDeclaringTypeName()+"."+signature.getMethod().getName();
        // 当dao接口存在集成关系的时候，需要解决sqlid精确查找
        Class<?>[] classz = point.getTarget().getClass().getInterfaces();
        String sqlid = "";
        if (classz.length > 0) {
            sqlid = classz[0].getName() + "."
                    + signature.getMethod().getName();
        } else {
            sqlid = signature.getDeclaringTypeName() + "."
                    + signature.getMethod().getName();
        }
        BoundSql boundSql = readOnlyMySqlSessionFactory.getConfiguration()
                .getMappedStatement(sqlid)
                .getBoundSql(parameterObject);
        String sqlString = boundSql.getSql();
//        String compareSql = sqlString.toLowerCase();
        return getSql(readOnlyMySqlSessionFactory.getConfiguration(), boundSql, parameterObject, sqlString, signature.getMethod().getReturnType());
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, Object parameterObject, String sqlString, Class<?> returnType) {
        if (boundSql == null || sqlString.isEmpty()) {
            return "";
        }
        String targetSql = fillSql(configuration, boundSql, parameterObject, sqlString);
        return targetSql;
    }

    private static String fillSql(Configuration configuration, BoundSql boundSql, Object parameterObject, String sqlString) {
        if (sqlString.indexOf("?") < 0) {
            return sqlString;
        }
        List<String> values = getValuesList(configuration, boundSql, parameterObject);
        if (values == null || values.size() == 0) {
            throw new RuntimeException("解析sql语句时异常，没取到参数");
        }
        Matcher m = Pattern.compile("\\?").matcher(sqlString);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (m.find()) {
            if (index >= values.size()) {
                break;
            }
            try {
                m.appendReplacement(sb, values.get(index));
            } catch (Exception ex) {
//                log.error("==========AroundAspect Exception2========:" + ex.getMessage());
            }
            index++;
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static List<String> getValuesList(Configuration configuration, BoundSql boundSql, Object parameterObject) {
        List<String> valuesList = new ArrayList<>();
        if (parameterObject == null) {
            return valuesList;
        }
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null && parameterMappings.size() > 0) {
            String targetValue = null;
            Object value;
            String propertyName;
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        value = metaObject.getValue(propertyName);
                    }
                    if (value == null) {
                        targetValue = "NULL";
                    } else if (value instanceof Date) {
                        targetValue = String.format("'%s'", DateUtil.DateToString((Date) value));
                    } else if (value instanceof String) {
                        targetValue = String.format("'%s'", value);
                    } else {
                        targetValue = value.toString();
                    }
                    valuesList.add(targetValue);
                }
            }
        }
        return valuesList;
    }

    public static Object wrapCollection(final Object object) {
        if (object instanceof Collection) {
            DefaultSqlSession.StrictMap<Object> map = new DefaultSqlSession.StrictMap<Object>();
            map.put("collection", object);
            if (object instanceof List) {
                map.put("list", object);
            }
            return map;
        } else if (object != null && object.getClass().isArray()) {
            DefaultSqlSession.StrictMap<Object> map = new DefaultSqlSession.StrictMap<Object>();
            map.put("array", object);
            return map;
        }
        return object;
    }


}

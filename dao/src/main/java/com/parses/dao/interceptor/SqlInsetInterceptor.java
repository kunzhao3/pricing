package com.parses.dao.interceptor;

import com.parses.dao.FileBufferedWriter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlInsetInterceptor implements Interceptor {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private static final Logger logger = LoggerFactory.getLogger(SqlInsetInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        // 仅处理INSERT类型SQL
        if (ms.getSqlCommandType() != SqlCommandType.INSERT) {
            return invocation.proceed();
        }
        if(TransactionSynchronizationManager.isActualTransactionActive()){
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
                    BoundSql boundSql = ms.getBoundSql(parameter);
                    Configuration config = ms.getConfiguration();
                    try {
                        String formattedSql = formatSql(boundSql, config);
                        long timestamp = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date(timestamp));
                        FileBufferedWriter writerThread = new FileBufferedWriter(uploadDir + date + "-insert.sql");
                        writerThread.write(formattedSql);
                    } catch (Exception e) {
                        logger.warn("SQL format error", e);
                    }
                }
            });
        }
        return invocation.proceed();
    }

    private String formatSql(BoundSql boundSql, Configuration config) {
        String sql = boundSql.getSql();
        Object paramObj = boundSql.getParameterObject();
        List<ParameterMapping> mappings = boundSql.getParameterMappings();
        if (mappings.isEmpty() || paramObj == null) {
            return sql;
        }
        // 处理批量操作参数
        if (paramObj instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) paramObj;
            if (paramMap.containsKey("collection") || paramMap.containsKey("list")) {
                Object collection = paramMap.get("collection") != null ? paramMap.get("collection") : paramMap.get("list");
                return formatBatchSql(sql, collection, mappings, config);
            }
        }
        // 普通参数处理
        return formatSingleSql(sql, paramObj, mappings, config);
    }

    private String formatBatchSql(String sql, Object collection, List<ParameterMapping> mappings, Configuration config) {
        if (!(collection instanceof Iterable)) {
            return sql;
        }
        StringBuilder result = new StringBuilder();
        String itemSql = sql;
        int index = 0;
        for (Object parameter : (Iterable<?>) collection) {
            MetaObject metaObject = config.newMetaObject(parameter);
            for (ParameterMapping mapping : mappings) {
                /**
                 * mappings 格式
                 * __frch_item_0.productNo __frch_item_0.productName __frch_item_0.productPrice
                 * __frch_item_1.productNo __frch_item_1.productName __frch_item_1.productPrice
                 * ............................................................................
                 * __frch_item_11.productNo __frch_item_12.productName __frch_item_12.productPrice
                 */
                String property = mapping.getProperty();
                // 处理批量参数前缀
                if (property.startsWith("__frch_item_" + index + ".")) {
                    property = substringInput(property);
                    Object value = getParameterValue(metaObject, property);
                    itemSql = itemSql.replaceFirst("\\?", formatParameter(value));
                }
            }
            index++;
        }
        itemSql = itemSql.replaceAll("values\\n", "values").replaceAll("\\)\\s*,\\s*\\n\\s*\\(", "),\n    (");
        result.append(itemSql).append("\n;");
        return result.toString();
    }

    private String formatSingleSql(String sql, Object parameter, List<ParameterMapping> mappings, Configuration config) {
        StringBuilder result = new StringBuilder();
        if (parameter instanceof String) {
            sql = sql.replaceFirst("\\?", formatParameter(parameter));
        } else {
            MetaObject metaObject = config.newMetaObject(parameter);
            for (ParameterMapping mapping : mappings) {
                Object value = getParameterValue(metaObject, mapping.getProperty());
                sql = sql.replaceFirst("\\?", formatParameter(value));
            }
        }
        result.append(sql).append("\n;");
        return result.toString();
    }

    private Object getParameterValue(MetaObject metaObject, String property) {
        try {
            return metaObject.getValue(property);
        } catch (Exception e) {
            logger.debug("Get parameter value failed for property: {}", property);
            return null;
        }
    }

    private String formatParameter(Object value) {
        if (value == null) return "null";
        if (value instanceof Integer) return value.toString();
        if (value instanceof BigDecimal) return value.toString();
        if (value instanceof Date) return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) + "'";
        return "'" + value + "'";
    }

    public static String substringInput(String input) {
        int dotIndex = input.lastIndexOf(".");
        if (dotIndex != -1) {
            return input.substring(dotIndex + 1);
        }
        return "";
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可配置属性
    }
}

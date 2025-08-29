package com.parses.dao.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.parses.dao.datasource.DynamicDataSource;
import com.parses.dao.datasource.constant.DataSourceAddressEnum;
import com.parses.dao.interceptor.SqlInsetInterceptor;
import com.parses.dao.interceptor.SqlQueryInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan("com.parses.dao")
public class MyBatisConfig {
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource1() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("jdbc.datasource1.url"));
        dataSource.setUsername(env.getProperty("jdbc.datasource1.username"));
        dataSource.setPassword(env.getProperty("jdbc.datasource1.password"));
        dataSource.setInitialSize(env.getProperty("druid.initialSize",Integer.class));
        dataSource.setMaxActive(env.getProperty("druid.maxActive",Integer.class));
        dataSource.setMaxWait(env.getProperty("druid.maxWait",Long.class));
        return dataSource;
    }

    @Bean
    public DataSource dataSource2() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("jdbc.datasource2.url"));
        dataSource.setUsername(env.getProperty("jdbc.datasource2.username"));
        dataSource.setPassword(env.getProperty("jdbc.datasource2.password"));
        dataSource.setInitialSize(env.getProperty("druid.initialSize",Integer.class));
        dataSource.setMaxActive(env.getProperty("druid.maxActive",Integer.class));
        dataSource.setMaxWait(env.getProperty("druid.maxWait",Long.class));
        return dataSource;
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource source = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceAddressEnum.datasource1, dataSource1());
        targetDataSources.put(DataSourceAddressEnum.datasource2, dataSource2());
        source.setTargetDataSources(targetDataSources);
        source.setDefaultTargetDataSource(dataSource1());
        return source;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource, MybatisProperties mybatisProperties) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        // 扫描 Mapper XML 文件路径（很重要）
        if (!ObjectUtils.isEmpty(mybatisProperties.resolveMapperLocations())) {
            factoryBean.setMapperLocations(mybatisProperties.resolveMapperLocations());
        } else {
            factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:sqlmapper/**/*.xml"));
        }
        factoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        factoryBean.setPlugins(new Interceptor[]{sqlInsertInterceptor()});
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlQueryInterceptor sqlQueryInterceptor() {
        return new SqlQueryInterceptor();
    }

    @Bean
    public SqlInsetInterceptor sqlInsertInterceptor() {
        return new SqlInsetInterceptor();
    }
}

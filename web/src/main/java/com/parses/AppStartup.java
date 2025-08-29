package com.parses;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude =DruidDataSourceAutoConfigure.class)
@EnableApolloConfig({"application","TEST1.JDBC"})
public class AppStartup
{
    public static void main(String[] args) {
        SpringApplication.run(AppStartup.class, args);
    }
}

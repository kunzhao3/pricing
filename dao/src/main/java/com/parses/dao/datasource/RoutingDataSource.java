package com.parses.dao.datasource;

import com.parses.dao.datasource.constant.DataSourceAddressEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoutingDataSource {
    DataSourceAddressEnum value() default DataSourceAddressEnum.datasource1;
}

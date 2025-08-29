package com.parses.dao.datasource.aspect;

import com.parses.dao.datasource.RoutingDataSource;
import com.parses.dao.datasource.constant.DataSourceAddressEnum;
import com.parses.dao.datasource.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Order(Integer.MIN_VALUE)
public class RoutingDataSourceAspect {
    @Pointcut("@annotation(com.parses.dao.datasource.RoutingDataSource)|| @within(com.parses.dao.datasource.RoutingDataSource)")
    public void routingDataSourcePointcut() {
    }

    @Around("routingDataSourcePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method originMethod = this.resolveMethod(joinPoint);
        RoutingDataSource routerDataSource = originMethod.getAnnotation(RoutingDataSource.class);
        DataSourceAddressEnum dataSourceAddressEnum = Objects.isNull(routerDataSource) ? DataSourceAddressEnum.datasource1 : routerDataSource.value();
        DataSourceContextHolder.setCurrentDataSource(dataSourceAddressEnum);
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.removeDataSource();
        }
    }

    protected Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method method = this.getDeclaredMethodFor(targetClass, signature.getName(), signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("Cannot resolve target method: " + signature.getMethod().getName());
        } else {
            return method;
        }
    }
    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException var6) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass != null ? this.getDeclaredMethodFor(superClass, name, parameterTypes) : null;
        }
    }
}

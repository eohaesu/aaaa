package com.deotis.digitalars.system.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.deotis.digitalars.system.config.datasource.DataSourceContext;
import com.deotis.digitalars.system.config.datasource.DataSourceEnum;
import com.deotis.digitalars.system.config.datasource.DataSourceRetention;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description aspect for dynamic datasource
 *
 */
@Slf4j
@Component
@Aspect
public class DynamicDataSourceAspect {
    @Pointcut("execution(* com.deotis.digitalars.service..*(..)) " +
            "&& @annotation(com.deotis.digitalars.system.config.datasource.DataSourceRetention)")
    
    public void dataSourcePointcut() {
    	log.debug("Load Aspect for multi DataSource annotation use sample : @DataSourceRetention(DataSourceEnum.secondary)");
    }

    @Around("dataSourcePointcut()")
    public Object doAround(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        
        Method method = methodSignature.getMethod();
        
        DataSourceRetention annotation = method.getAnnotation(DataSourceRetention.class);
        
        DataSourceEnum datasourceEnum = annotation.value();

        if (datasourceEnum == DataSourceEnum.primary) {
            DataSourceContext.setDataSourceType(DataSourceEnum.primary);
        } else if (datasourceEnum == DataSourceEnum.secondary) {
            DataSourceContext.setDataSourceType(DataSourceEnum.secondary);
        }

        Object result = null;
        
        try {
        	
            result = pjp.proceed();
            
        } catch (Throwable throwable) {
        	log.error("DataSource Aspect Error. message:{}, exception:{}", throwable.getMessage(), throwable.toString());
        } finally {
        	//reset DataSource
            DataSourceContext.resetDataSourceType();
        }

        return result;
    }
}
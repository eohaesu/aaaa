package com.deotis.digitalars.system.config.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author jongjin
 * @description Runtime retention for dynamic datasource
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRetention {
	DataSourceEnum value() default DataSourceEnum.primary;
}

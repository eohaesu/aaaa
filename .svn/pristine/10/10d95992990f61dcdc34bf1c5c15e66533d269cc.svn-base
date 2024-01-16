package com.deotis.digitalars.system.config.datasource;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
 
/**
 * 
 * @author jongjin
 * @description dynamic data source configuration (Hikari connection pool)
 */

@Configuration
@MapperScan(basePackages="com.deotis.digitalars.mapper")
@EnableTransactionManagement
class DataSourceConfig {

    @Bean
    @Qualifier("primaryHikariConfig")
    @ConfigurationProperties(prefix="spring.datasource.hikari.primary")
    HikariConfig primaryHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Qualifier("primaryDataSource")
    DataSource primaryDataSource() {
        return new HikariDataSource(primaryHikariConfig());
    }
    @Bean
    @Qualifier("secondaryHikariConfig")
    @ConfigurationProperties(prefix="spring.datasource.hikari.secondary")
    HikariConfig secondaryHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Qualifier("secondaryDataSource")
    DataSource secondaryDataSource(){
        return new HikariDataSource(secondaryHikariConfig());
    }
    
    
    @Bean("dynamicDataSource")
    @Primary
    DynamicDataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,
                                               @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
    	
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        targetDataSources.put(DataSourceEnum.primary, primaryDataSource);
        targetDataSources.put(DataSourceEnum.secondary, secondaryDataSource);

        DynamicDataSource routingDataSource = new DynamicDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);

        return routingDataSource;
    }
    
    @Bean(name="dynamicSqlSessionFactory")
    @Primary
    SqlSessionFactory sqlSessionFactoryDynamic(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception{
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/deotis/digitalars/mapper/xml/**/*.xml"));
        
        if(sessionFactory.getObject() != null) {
        	sessionFactory.getObject().getConfiguration().setCallSettersOnNulls(true); // set null column has key/value
        	//sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true); //use camel case
        }

        return sessionFactory.getObject();
    }

    
    @Bean(name = "dynamicDs")
    @Primary
    SqlSessionTemplate sqlSessionTemplateDynamic(@Qualifier("dynamicSqlSessionFactory") SqlSessionFactory sqlSessionFactoryDynamic){
      return new SqlSessionTemplate(sqlSessionFactoryDynamic);
    }

    
}
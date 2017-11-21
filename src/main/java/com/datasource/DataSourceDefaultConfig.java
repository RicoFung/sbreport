package com.datasource;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource(value = "classpath:config/datasource.properties", ignoreResourceNotFound = true)
public class DataSourceDefaultConfig 
{
    @Value("${datasource.default.url}")
    private String url;
    @Value("${datasource.default.username}")
    private String user;
    @Value("${datasource.default.password}")
    private String password;
    @Value("${datasource.default.driver-class-name}")
    private String driverClass;
    @Value("${datasource.default.filters}")
    private String filters;
    @Value("${datasource.default.initialSize}")
    private int initialSize;
    @Value("${datasource.default.maxActive}")
    private int maxActive;
    @Value("${datasource.default.minIdle}")
    private int minIdle;
    @Value("${datasource.default.maxWait}")
    private int maxWait;
    @Value("${datasource.default.mapper-location}")
    private String mapperLocation;
    @Value("${mybatis.config-location}")
    private String mybatisConfigLocation;
 
    @Bean(name = "dataSource")
    public DataSource dataSource() throws SQLException 
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters(filters);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        return dataSource;
    }
 
    @Bean(name = "sqlSessionFactory")
    @DependsOn({ "dataSource" })
    public SqlSessionFactory sqlSessionFactory() throws Exception 
    {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocation));
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(mybatisConfigLocation));
        return sessionFactory.getObject();
    }

	@Bean(name = "sqlSessionTemplate")
	@DependsOn({ "sqlSessionFactory" })
	public SqlSessionTemplate sqlSessionTemplate() throws Exception 
	{
		return new SqlSessionTemplate(sqlSessionFactory());
	}
	
	@Bean(name = "transactionManager")
	@DependsOn({ "dataSource" })
	public DataSourceTransactionManager transactionManager() throws SQLException 
	{
		return new DataSourceTransactionManager(dataSource());
	}
	
	@Bean(name = "transactionInterceptor")
	@DependsOn({ "transactionManager" })
	public TransactionInterceptor transactionInterceptor() throws Throwable
	{
		Properties prop = new Properties();
		prop.setProperty("add*", "PROPAGATION_REQUIRED,-Exception");
		prop.setProperty("del*", "PROPAGATION_REQUIRED,-Exception");
		prop.setProperty("upd*", "PROPAGATION_REQUIRED,-Exception");
		prop.setProperty("get*", "PROPAGATION_NEVER,readOnly");
		prop.setProperty("query*", "PROPAGATION_NEVER,readOnly");
		TransactionInterceptor ti = new TransactionInterceptor();
		ti.setTransactionManager(transactionManager());
		ti.setTransactionAttributes(prop);
		return ti;
	}

	@Bean(name = "beanNameAutoProxyCreator")
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() throws Throwable
	{
		BeanNameAutoProxyCreator bpc = new BeanNameAutoProxyCreator();
		bpc.setProxyTargetClass(true);
		bpc.setBeanNames("*Service");
		bpc.setInterceptorNames("transactionInterceptor");
		return bpc;
	}
}
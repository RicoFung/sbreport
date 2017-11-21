package com.datasource;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;

//@Configuration
//@PropertySource(value = "classpath:config/datasource.properties", ignoreResourceNotFound = true)
public class DataSourceFirstConfig 
{
    @Value("${datasource.first.unique-resource-name}")
    private String uniqueResourceName;
    @Value("${datasource.first.url}")
    private String url;
    @Value("${datasource.first.username}")
    private String user;
    @Value("${datasource.first.password}")
    private String password;
    @Value("${datasource.first.driver-class-name}")
    private String driverClass;
    @Value("${datasource.first.filters}")
    private String filters;
    @Value("${datasource.first.initialSize}")
    private int initialSize;
    @Value("${datasource.first.maxActive}")
    private int maxActive;
    @Value("${datasource.first.minIdle}")
    private int minIdle;
    @Value("${datasource.first.maxWait}")
    private int maxWait;
    @Value("${datasource.first.mapper-location}")
    private String mapperLocation;
    @Value("${mybatis.config-location}")
    private String mybatisConfigLocation;
 
    @Bean(name = "dataSourceFirst")
    @Primary
    public DataSource dataSourceFirst() throws SQLException 
    {
        DruidXADataSource dataSource = new DruidXADataSource();
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
        
        AtomikosDataSourceBean adsBean = new AtomikosDataSourceBean();
        adsBean.setXaDataSource(dataSource);
        adsBean.setUniqueResourceName(uniqueResourceName);
        return adsBean;
    }

//	  打开后，分布式事务JTA失效
//    @Bean(name = "transactionManagerFirst")
//    @DependsOn({ "dataSourceFirst" })
//    @Primary
//    public DataSourceTransactionManager transactionManagerFirst() 
//    {
//        return new DataSourceTransactionManager(dataSourceFirst());
//    }
 
    @Bean(name = "sqlSessionFactoryFirst")
    @DependsOn({ "dataSourceFirst" })
    @Primary
    public SqlSessionFactory sqlSessionFactoryFirst() throws Exception 
    {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceFirst());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocation));
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(mybatisConfigLocation));
        return sessionFactory.getObject();
    }

	@Bean(name = "sqlSessionTemplateFirst")
    @DependsOn({ "sqlSessionFactoryFirst" })
	@Primary
	public SqlSessionTemplate sqlSessionTemplateFirst() throws Exception 
	{
		return new SqlSessionTemplate(sqlSessionFactoryFirst());
	}
}
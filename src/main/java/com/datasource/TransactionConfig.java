package com.datasource;

import java.util.Properties;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

// 打开注释，开启分布式事务
//@Configuration
//@EnableTransactionManagement
//@PropertySource(value = "classpath:config/jta.properties", ignoreResourceNotFound = true)
public class TransactionConfig
{
	@Bean(name = "userTransaction")
	public UserTransaction userTransaction() throws Throwable
	{
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(300);
		return userTransactionImp;
	}

	@Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() throws Throwable
	{
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(true);
		return userTransactionManager;
	}

	@Bean(name = "transactionManager")
	@DependsOn({ "userTransaction", "atomikosTransactionManager" })
	public PlatformTransactionManager transactionManager() throws Throwable
	{
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
		jtaTransactionManager.setAllowCustomIsolationLevels(true);
		return jtaTransactionManager;
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
		ti.setTransactionAttributes(prop);
		ti.setTransactionManager(transactionManager());
		return ti;
	}

	@Bean(name = "beanNameAutoProxyCreator")
	@DependsOn({ "transactionInterceptor" })
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() throws Throwable
	{
		BeanNameAutoProxyCreator bpc = new BeanNameAutoProxyCreator();
		bpc.setProxyTargetClass(true);
		bpc.setBeanNames("*Service");
		bpc.setInterceptorNames("transactionInterceptor");
		return bpc;
	}
}

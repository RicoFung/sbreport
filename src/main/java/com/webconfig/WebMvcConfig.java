package com.webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter
{
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
		registry.addViewController("/").setViewName("forward:/index.jsp"); // 设置默认首页(必须加入“forward:”, 否则会访问spring.mvc.view.prefix所指定的目录)
		super.addViewControllers(registry);
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver()
	{
		CommonsMultipartResolver cmr = new CommonsMultipartResolver();
		// 默认编码
		cmr.setDefaultEncoding("UTF-8");
		// 文件大小最大值. 注意限制的不是针对单个文件，而是所有文件的容量之和
		cmr.setMaxUploadSize(10485760000l);
		// 内存中的最大值
		cmr.setMaxInMemorySize(40960);
		return cmr;
	}
}

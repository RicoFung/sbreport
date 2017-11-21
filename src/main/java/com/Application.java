package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
// exclude表示自动配置时不包括Multipart配置（使用CommonsMultipartFile实现多文件上传）
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class })
public class Application extends SpringBootServletInitializer
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(Application.class);
	}
}

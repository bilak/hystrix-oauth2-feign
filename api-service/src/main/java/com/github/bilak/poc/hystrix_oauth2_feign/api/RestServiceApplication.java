package com.github.bilak.poc.hystrix_oauth2_feign.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.RequestContextFilter;

/**
 * Created by lvasek on 09/09/16.
 */
@SpringBootApplication
@EnableFeignClients
public class RestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestServiceApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean requestContextFilterRegistration() {
		FilterRegistrationBean filter = new FilterRegistrationBean();
		filter.setFilter(requestContextFilter());
		filter.setOrder(0);
		return filter;
	}

	@Bean
	public RequestContextFilter requestContextFilter() {
		return new RequestContextFilter();
	}
}

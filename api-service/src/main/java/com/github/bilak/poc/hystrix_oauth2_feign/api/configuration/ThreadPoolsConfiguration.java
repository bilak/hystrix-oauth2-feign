package com.github.bilak.poc.hystrix_oauth2_feign.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

/**
 * Created by lvasek on 09/09/16.
 */
@Configuration
public class ThreadPoolsConfiguration {

	public static final String DELEGATING_SECURITY_EXECUTOR_NAME = "delegatingSecurityExecutor";

	@Bean(name = DELEGATING_SECURITY_EXECUTOR_NAME)
	DelegatingSecurityContextAsyncTaskExecutor delegatingSecurityExecutor() {
		return new DelegatingSecurityContextAsyncTaskExecutor(new SimpleAsyncTaskExecutor("delegating-security-"));
	}

}

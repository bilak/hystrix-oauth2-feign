package com.github.bilak.poc.hystrix_oauth2_feign.api.configuration;

import com.github.bilak.poc.hystrix_oauth2_feign.api.hystrix.concurrency.strategy.DelegatingSecurityContextAndRequestContextExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by lvasek on 09/09/16.
 */
@Configuration
public class ThreadPoolsConfiguration {

	public static final String DELEGATING_SECURITY_EXECUTOR_NAME = "delegatingSecurityExecutor";

	@Bean(name = DELEGATING_SECURITY_EXECUTOR_NAME)
	ExecutorService delegatingSecurityExecutor() {
		return new DelegatingSecurityContextAndRequestContextExecutor(
				new ScheduledThreadPoolExecutor(10, new CustomizableThreadFactory("delegating-security-")));
	}

}

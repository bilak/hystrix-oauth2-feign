package com.github.bilak.poc.hystrix_oauth2_feign.api.hystrix.concurrency.strategy;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * Created by lvasek on 12/09/16.
 */
@Component
public class DelegatingRequestAttributesStrategy extends HystrixConcurrencyStrategy {

	public DelegatingRequestAttributesStrategy() {
		HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
	}

	@Override
	public <T> Callable<T> wrapCallable(Callable<T> callable) {
		final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		return () -> {
			try {
				RequestContextHolder.setRequestAttributes(requestAttributes, true);
				return callable.call();
			} finally {
				RequestContextHolder.resetRequestAttributes();
			}
		};
	}

}

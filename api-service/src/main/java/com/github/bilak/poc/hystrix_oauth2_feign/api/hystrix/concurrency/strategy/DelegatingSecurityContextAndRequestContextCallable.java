package com.github.bilak.poc.hystrix_oauth2_feign.api.hystrix.concurrency.strategy;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * Created by lvasek on 13/09/16.
 */
public class DelegatingSecurityContextAndRequestContextCallable<V> implements Callable<V> {

	private final Callable<V> delegate;

	/**
	 * The {@link SecurityContext} that the delegate {@link Callable} will be
	 * ran as.
	 */
	private final SecurityContext delegateSecurityContext;

	private final RequestAttributes delegateRequestAttributes;

	/**
	 * The {@link SecurityContext} that was on the {@link SecurityContextHolder}
	 * prior to being set to the delegateSecurityContext.
	 */
	private SecurityContext originalSecurityContext;

	private RequestAttributes originalRequestAttributes;

	public DelegatingSecurityContextAndRequestContextCallable(Callable<V> delegate, SecurityContext delegateSecurityContext,
			RequestAttributes delegateRequestAttributes) {
		this.delegate = delegate;
		this.delegateSecurityContext = delegateSecurityContext;
		this.delegateRequestAttributes = delegateRequestAttributes;
		RequestContextHolder.setRequestAttributes(delegateRequestAttributes, true);
	}

	public DelegatingSecurityContextAndRequestContextCallable(Callable<V> delegate) {
		this(delegate, SecurityContextHolder.getContext(), RequestContextHolder.getRequestAttributes());
	}

	@Override
	public V call() throws Exception {
		this.originalSecurityContext = SecurityContextHolder.getContext();
		RequestContextHolder.setRequestAttributes(delegateRequestAttributes, true);
		try {
			SecurityContextHolder.setContext(delegateSecurityContext);
			return delegate.call();
		} finally {
			SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
			if (emptyContext.equals(originalSecurityContext)) {
				SecurityContextHolder.clearContext();
			} else {
				SecurityContextHolder.setContext(originalSecurityContext);
			}
			this.originalSecurityContext = null;
			RequestContextHolder.resetRequestAttributes();
		}
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public static <V> Callable<V> create(Callable<V> delegate, SecurityContext securityContext, RequestAttributes requestAttributes) {
		return new DelegatingSecurityContextAndRequestContextCallable<V>(
				delegate,
				securityContext == null ? SecurityContextHolder.getContext() : securityContext,
				requestAttributes == null ? RequestContextHolder.getRequestAttributes() : requestAttributes
		);
	}
}

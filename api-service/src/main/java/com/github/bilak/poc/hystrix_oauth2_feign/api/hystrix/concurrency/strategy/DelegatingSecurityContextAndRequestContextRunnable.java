package com.github.bilak.poc.hystrix_oauth2_feign.api.hystrix.concurrency.strategy;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by lvasek on 13/09/16.
 */
public class DelegatingSecurityContextAndRequestContextRunnable implements Runnable {

	private final Runnable delegate;
	/**
	 * The {@link SecurityContext} that the delegate {@link Runnable} will be
	 * ran as.
	 */
	private final SecurityContext delegateSecurityContext;

	/**
	 * The {@link SecurityContext} that was on the {@link SecurityContextHolder}
	 * prior to being set to the delegateSecurityContext.
	 */
	private SecurityContext originalSecurityContext;

	private final RequestAttributes delegateRequestAttributes;

	public DelegatingSecurityContextAndRequestContextRunnable(Runnable delegate, SecurityContext delegateSecurityContext,
			RequestAttributes delegateRequestAttributes) {
		this.delegate = delegate;
		this.delegateSecurityContext = delegateSecurityContext;
		this.delegateRequestAttributes = delegateRequestAttributes;
		RequestContextHolder.setRequestAttributes(delegateRequestAttributes, true);
	}

	public DelegatingSecurityContextAndRequestContextRunnable(Runnable delegate) {
		this(delegate, SecurityContextHolder.getContext(), RequestContextHolder.getRequestAttributes());
	}

	@Override
	public void run() {
		this.originalSecurityContext = SecurityContextHolder.getContext();
		RequestContextHolder.setRequestAttributes(delegateRequestAttributes, true);
		try {
			SecurityContextHolder.setContext(delegateSecurityContext);
			delegate.run();
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

	public static Runnable create(Runnable delegate, SecurityContext securityContext, RequestAttributes requestAttributes) {
		return new DelegatingSecurityContextAndRequestContextRunnable(
				delegate,
				securityContext == null ? SecurityContextHolder.getContext() : securityContext,
				requestAttributes == null ? RequestContextHolder.getRequestAttributes() : requestAttributes);
	}
}

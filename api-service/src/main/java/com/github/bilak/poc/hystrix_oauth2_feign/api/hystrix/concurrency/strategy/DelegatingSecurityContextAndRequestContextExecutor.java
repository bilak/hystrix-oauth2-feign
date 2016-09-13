package com.github.bilak.poc.hystrix_oauth2_feign.api.hystrix.concurrency.strategy;

import org.springframework.security.core.context.SecurityContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lvasek on 12/09/16.
 */
public class DelegatingSecurityContextAndRequestContextExecutor implements Executor, ExecutorService {

	private final Executor delegateExecutor;
	private final SecurityContext delegateSecurityContext;

	public DelegatingSecurityContextAndRequestContextExecutor(Executor delegateExecutor, SecurityContext delegateSecurityContext) {
		this.delegateExecutor = delegateExecutor;
		this.delegateSecurityContext = delegateSecurityContext;
	}

	public DelegatingSecurityContextAndRequestContextExecutor(Executor executor) {
		this(executor, null);
	}

	@Override
	public void execute(Runnable command) {

		command = wrap(command);
		delegateExecutor.execute(command);
	}

	public Runnable wrap(Runnable runnable) {
		return DelegatingSecurityContextAndRequestContextRunnable.create(runnable, delegateSecurityContext, null);
	}

	protected final <T> Callable<T> wrap(Callable<T> delegate) {
		return DelegatingSecurityContextAndRequestContextCallable.create(delegate, delegateSecurityContext, null);
	}

	public final void shutdown() {
		getDelegate().shutdown();
	}

	public final List<Runnable> shutdownNow() {
		return getDelegate().shutdownNow();
	}

	public final boolean isShutdown() {
		return getDelegate().isShutdown();
	}

	public final boolean isTerminated() {
		return getDelegate().isTerminated();
	}

	public final boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return getDelegate().awaitTermination(timeout, unit);
	}

	public final <T> Future<T> submit(Callable<T> task) {
		task = wrap(task);
		return getDelegate().submit(task);
	}

	public final <T> Future<T> submit(Runnable task, T result) {
		task = wrap(task);
		return getDelegate().submit(task, result);
	}

	public final Future<?> submit(Runnable task) {
		task = wrap(task);
		return getDelegate().submit(task);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final List invokeAll(Collection tasks) throws InterruptedException {
		tasks = createTasks(tasks);
		return getDelegate().invokeAll(tasks);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final List invokeAll(Collection tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		tasks = createTasks(tasks);
		return getDelegate().invokeAll(tasks, timeout, unit);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final Object invokeAny(Collection tasks) throws InterruptedException,
			ExecutionException {
		tasks = createTasks(tasks);
		return getDelegate().invokeAny(tasks);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final Object invokeAny(Collection tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		tasks = createTasks(tasks);
		return getDelegate().invokeAny(tasks, timeout, unit);
	}

	private <T> Collection<Callable<T>> createTasks(Collection<Callable<T>> tasks) {
		if (tasks == null) {
			return null;
		}
		List<Callable<T>> results = new ArrayList<Callable<T>>(tasks.size());
		for (Callable<T> task : tasks) {
			results.add(wrap(task));
		}
		return results;
	}

	private ExecutorService getDelegate() {
		return (ExecutorService) getDelegateExecutor();
	}

	protected final Executor getDelegateExecutor() {
		return delegateExecutor;
	}
}

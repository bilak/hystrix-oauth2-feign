package com.github.bilak.poc.hystrix_oauth2_feign.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by lvasek on 09/09/16.
 */
@RestController
public class SampleController implements SampleService {

	private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

	private DelegatingSecurityContextAsyncTaskExecutor executor;
	private SampleServiceClient sampleServiceClient;

	@Autowired
	public SampleController(DelegatingSecurityContextAsyncTaskExecutor executor, SampleServiceClient sampleServiceClient) {
		this.executor = executor;
		this.sampleServiceClient = sampleServiceClient;
	}

	@Override
	public ResponseEntity<List<String>> getDefinedEntries() {
		return ResponseEntity.ok(Arrays.asList("one", "two", "three"));
	}

	@GetMapping("/entries/current-thread")
	public ResponseEntity<List<String>> callInCurrentThread(Principal principal) {
		logger.debug("Principal from rest {}", principal);
		return sampleServiceClient.getDefinedEntries();
	}

	@GetMapping("/entries/another-thread")
	public ResponseEntity<List<String>> callInAnotherThread(Principal principal) throws ExecutionException, InterruptedException {
		logger.debug("Principal from rest {}", principal);
		Future<List<String>> result = executor.submit(
				new SampleServiceClientCaller(sampleServiceClient)
		);
		return Optional.ofNullable(result.get())
				.map(r -> ResponseEntity.ok(r))
				.orElse(new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND));
	}

	/*
	public static class DelegatingRequestAttributesSampleServiceClientCaller<V> implements Callable<V> {

		private Callable<V> delegate;

		public DelegatingRequestAttributesSampleServiceClientCaller(Callable<V> delegate) {
			RequestContextHolder.setRequestAttributes(RequestContextHolder.currentRequestAttributes(), true);
			this.delegate = delegate;
		}

		@Override
		public V call() throws Exception {

			return delegate.call();
		}
	}
	*/

	public static class SampleServiceClientCaller implements Callable<List<String>> {

		private SampleServiceClient sampleServiceClient;

		public SampleServiceClientCaller(SampleServiceClient sampleServiceClient) {
			this.sampleServiceClient = sampleServiceClient;
		}

		@Override
		public List<String> call() throws Exception {
			logger.debug("Security context in another thread {}", SecurityContextHolder.getContext());
			return sampleServiceClient.getDefinedEntries().getBody();
		}
	}

}

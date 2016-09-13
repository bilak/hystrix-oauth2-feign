package com.github.bilak.poc.hystrix_oauth2_feign.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by lvasek on 09/09/16.
 */
@RestController
public class SampleController implements SampleService {

	private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

	private ExecutorService executor;
	private SampleServiceClient sampleServiceClient;

	@Autowired
	public SampleController(ExecutorService executor, SampleServiceClient sampleServiceClient) {
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
				new SampleServiceClientCallerCallable(sampleServiceClient)
		);
		return Optional.ofNullable(result.get())
				.map(r -> ResponseEntity.ok(r))
				.orElse(new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/entries/runnable")
	public ResponseEntity<String> callThreadAsValidation() {
		executor.submit(new SampleServiceClientCallerRunnable(sampleServiceClient));
		return ResponseEntity.ok("OK");
	}

	public static class SampleServiceClientCallerCallable implements Callable<List<String>> {

		private SampleServiceClient sampleServiceClient;

		public SampleServiceClientCallerCallable(SampleServiceClient sampleServiceClient) {
			this.sampleServiceClient = sampleServiceClient;
		}

		@Override
		public List<String> call() throws Exception {
			logger.debug("Security context in another thread {}", SecurityContextHolder.getContext());
			return sampleServiceClient.getDefinedEntries().getBody();
		}
	}

	public static class SampleServiceClientCallerRunnable implements Runnable {

		private SampleServiceClient sampleServiceClient;

		public SampleServiceClientCallerRunnable(SampleServiceClient sampleServiceClient) {
			this.sampleServiceClient = sampleServiceClient;
		}

		@Override
		public void run() {
			try {
				logger.debug("Going to call sample service");
				ResponseEntity<List<String>> response = sampleServiceClient.getDefinedEntries();
				logger.debug("Found {} entries {}", response.getBody().size(), response.getBody());
			} catch (Throwable e) {
				logger.error("Error while calling sample service client from within runnable", e);
			}
		}
	}
}

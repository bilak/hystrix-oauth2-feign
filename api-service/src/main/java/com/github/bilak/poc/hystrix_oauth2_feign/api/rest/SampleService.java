package com.github.bilak.poc.hystrix_oauth2_feign.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by lvasek on 09/09/16.
 */
public interface SampleService {

	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	ResponseEntity<List<String>> getDefinedEntries();
}

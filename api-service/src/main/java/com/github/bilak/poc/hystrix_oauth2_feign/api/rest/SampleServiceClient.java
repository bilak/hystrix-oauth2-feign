package com.github.bilak.poc.hystrix_oauth2_feign.api.rest;

import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by lvasek on 09/09/16.
 */
@FeignClient(url = "http://localhost:8090", name = "sampleServiceClient")
public interface SampleServiceClient extends SampleService {
}

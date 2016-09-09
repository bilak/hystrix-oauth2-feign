package com.github.bilak.poc.hystrix_oauth2_feign.api.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Created by lvasek on 09/09/16.
 */
@Configuration
public class FeignOAuth2Configuration {

	private Environment environment;

	@Autowired
	public FeignOAuth2Configuration(Environment environment) {
		this.environment = environment;
	}

	@Bean
	public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
		return new OAuth2FeignRequestInterceptor(oauth2ClientContext, clientResourceDetails());
	}

	private OAuth2ProtectedResourceDetails clientResourceDetails() {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri(environment.getProperty("security.oauth2.client.accessTokenUri"));
		resource.setClientId(environment.getProperty("security.oauth2.client.client-id"));
		resource.setClientSecret(environment.getProperty("security.oauth2.client.client-secret"));
		return resource;
	}
}

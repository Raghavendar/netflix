package com.test.ux.main;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.csc.fs.ngpa.datadictionary")
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableSwagger2
public class App extends SpringBootServletInitializer {

	@Value("${server.ssl.key-store-password}")
	private String keyStorePassword;

	@Value("${server.ssl.keyStoreType}")
	private String keyStoreType;

	@Value("${server.ssl.key-store}")
	private Resource resource;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(App.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	
	
	// @LoadBalanced
	@Bean(name = "template")
	RestTemplate restTemplate() {
		try {
			return new RestTemplate(clientHttpRequestFactory());
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient());
		httpComponentsClientHttpRequestFactory.setConnectTimeout(60000);
		httpComponentsClientHttpRequestFactory.setReadTimeout(180000);
		return httpComponentsClientHttpRequestFactory;
	}

	private HttpClient httpClient() throws Exception {
		final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		if (resource.exists()) {
			InputStream inputStream = resource.getInputStream();

			try {
				if (inputStream != null) {
					trustStore.load(inputStream, keyStorePassword.toCharArray());
				}
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} else {
			throw new RuntimeException("Cannot find resource: " + resource.getFilename());
		}

		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
				.build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);// getDefaultHostnameVerifier()
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		return httpClient;

	}
	  @Bean
	    public Docket swaggerSettings() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .select()
	                .apis(RequestHandlerSelectors.any())
	                .paths(PathSelectors.any())
	                .build()
	                .pathMapping("/ux");
	    }
}

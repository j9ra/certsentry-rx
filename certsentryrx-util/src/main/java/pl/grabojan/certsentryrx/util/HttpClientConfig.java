package pl.grabojan.certsentryrx.util;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import pl.grabojan.certsentryrx.util.cert.PkixURIResolver;
import pl.grabojan.certsentryrx.util.hc.HttpResource;
import reactor.netty.http.client.HttpClient;

@Configuration
public class HttpClientConfig {

	@Bean
	public HttpClient httpClient() {
		HttpClient httpClient = HttpClient.create()
		        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
		        .responseTimeout(Duration.ofSeconds(10))
		        .doOnConnected(conn -> conn
		                .addHandlerLast(new ReadTimeoutHandler(10))
		                .addHandlerLast(new WriteTimeoutHandler(10)));
		
		return httpClient;
	}
	
	@Bean
	public ReactorResourceFactory resourceFactory() {
	    ReactorResourceFactory factory = new ReactorResourceFactory();
	    factory.setUseGlobalResources(false); 
	    return factory;
	}
	
	@Bean
	public ReactorClientHttpConnector reactorClientHttpConnector() {
//	    return new ReactorClientHttpConnector(httpClient());
		
		 Function<HttpClient, HttpClient> mapper = client -> {
		        // Further customizations...
			 client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
		        .responseTimeout(Duration.ofSeconds(10))
		        .doOnConnected(conn -> conn
		                .addHandlerLast(new ReadTimeoutHandler(10))
		                .addHandlerLast(new WriteTimeoutHandler(10)));
			 return client;
		    };
		
		return new ReactorClientHttpConnector(resourceFactory(), mapper);
	}
	
	
	@Bean
	public WebClient webClient() {
	    return WebClient.builder()
	    		.clientConnector(reactorClientHttpConnector())
	    		.build(); 
	}
	
	@Bean
	public HttpResource httpResource() {
		return new HttpResource(webClient());
	}
	
	@Bean
	public PkixURIResolver pkixURIResolver() {
		return new PkixURIResolver(httpResource());
	}
}

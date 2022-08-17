package pl.grabojan.certsentryrx.util;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
		        .responseTimeout(Duration.ofSeconds(30))
		        .doOnConnected(conn -> conn
		                .addHandlerLast(new ReadTimeoutHandler(30))
		                .addHandlerLast(new WriteTimeoutHandler(30)));
		
		return httpClient;
	}
	
	@Bean
	@Profile(value = "default")
	public ReactorClientHttpConnector reactorClientHttpConnector1() {
		return new ReactorClientHttpConnector(httpClient());
	}
	
	@Bean
	@Profile(value = "httpclientthreads")
	public ReactorResourceFactory resourceFactory() {
	    ReactorResourceFactory factory = new ReactorResourceFactory();
	    factory.setUseGlobalResources(false); 
	    return factory;
	}
	
	@Bean
	@Profile(value = "httpclientthreads")
	public ReactorClientHttpConnector reactorClientHttpConnector2() {		
		 Function<HttpClient, HttpClient> mapper = client -> {
		        // Further customizations...
			 client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
		        .responseTimeout(Duration.ofSeconds(30))
		        .doOnConnected(conn -> conn
		                .addHandlerLast(new ReadTimeoutHandler(30))
		                .addHandlerLast(new WriteTimeoutHandler(30)));
			 return client;
		    };
		
		return new ReactorClientHttpConnector(resourceFactory(), mapper);
	}
	
	@Bean
	public WebClient webClient(ReactorClientHttpConnector reactorClientHttpConnector ) {
	    return WebClient.builder()
	    		.clientConnector(reactorClientHttpConnector)
	    		.build(); 
	}
	
	@Bean
	public HttpResource httpResource(WebClient webClient) {
		return new HttpResource(webClient);
	}
	
	@Bean
	public PkixURIResolver pkixURIResolver(HttpResource httpResource) {
		return new PkixURIResolver(httpResource);
	}
}

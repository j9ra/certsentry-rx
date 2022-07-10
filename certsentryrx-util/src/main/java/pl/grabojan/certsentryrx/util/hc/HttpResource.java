package pl.grabojan.certsentryrx.util.hc;

import java.net.URI;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpResource {
		
	private final WebClient webClient;
	
	public HttpResource(WebClient webClient) {
		this.webClient = webClient;
		
	}
	
	public Mono<byte[]> get(String uri) {
		
		log.debug("Resource {} for uri location: [{}]", "GET", uri);
		
		return webClient
				.get()
				.uri(URI.create(uri))
				.exchangeToMono(this::responseToByteArray);
	}
	
	public Mono<byte[]> post(String uri, byte[] body) {
		
		log.debug("Resource {} for uri location: [{}]", "POST", uri);
		
		return webClient
				.post()
				.uri(URI.create(uri))
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.bodyValue(body)
				.exchangeToMono(this::responseToByteArray);
	}
	
	
	private Mono<byte[]> responseToByteArray(ClientResponse response) {
		if(response.statusCode().equals(HttpStatus.OK)) {
			log.debug("Successful request - response contentType: {}, length: {}",
					response.headers().contentType(),
					response.headers().contentLength());

            return response.bodyToMono(ByteArrayResource.class).map(ByteArrayResource::getByteArray);
        } else {
            // Turn to error
        	log.error("Unsuccessful request - response code {}", response.statusCode().toString());
            return response.createException().flatMap( e -> Mono.error(new HttpResourceException("HttpResource Failed", e)));
        }
	}

}

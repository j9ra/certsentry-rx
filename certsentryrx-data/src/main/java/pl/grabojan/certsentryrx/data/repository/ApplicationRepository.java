package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.Application;
import reactor.core.publisher.Mono;

public interface ApplicationRepository extends ReactiveCrudRepository<Application, Long> {

	Mono<Application> findByApiKey(String apiKey);
}

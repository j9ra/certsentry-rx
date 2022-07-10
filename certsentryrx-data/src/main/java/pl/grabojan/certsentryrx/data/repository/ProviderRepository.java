package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.Provider;
import reactor.core.publisher.Flux;

public interface ProviderRepository extends ReactiveCrudRepository<Provider, Long> {

	Flux<Provider> findByName(String name);
	
	Flux<Provider> findByTradeName(String tradeName);
	
}

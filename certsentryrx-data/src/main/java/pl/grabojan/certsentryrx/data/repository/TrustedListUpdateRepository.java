package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.TrustedListUpdate;
import reactor.core.publisher.Flux;

public interface TrustedListUpdateRepository extends ReactiveCrudRepository<TrustedListUpdate, Long> {

	Flux<TrustedListUpdate> findByTrustedListId(Long id);

}

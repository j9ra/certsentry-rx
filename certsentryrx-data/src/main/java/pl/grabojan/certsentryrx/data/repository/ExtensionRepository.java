package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.ServiceExtension;
import reactor.core.publisher.Flux;

public interface ExtensionRepository extends ReactiveCrudRepository<ServiceExtension, Long> {

	Flux<ServiceExtension> findByServiceId(Long serviceId);

}

package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.SupplyPoint;
import reactor.core.publisher.Flux;

public interface SupplyPointRepository extends ReactiveCrudRepository<SupplyPoint, Long> {

	Flux<SupplyPoint> findByServiceId(Long serviceId);

}

package pl.grabojan.certsentryrx.data.repository;

import java.util.Date;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.Service;
import pl.grabojan.certsentryrx.data.model.ServiceStatus;
import pl.grabojan.certsentryrx.data.model.ServiceType;
import reactor.core.publisher.Flux;

public interface ServiceRepository extends ReactiveCrudRepository<Service, Long> {
	
	Flux<Service> findByType(ServiceType type);
	
	Flux<Service> findByName(String name);
	
	Flux<Service> findByStatus(ServiceStatus status);
	
	Flux<Service> findByProviderId(Long providerId);
	
	Flux<Service> findByStartDateBetween(Date startDate, Date endDate);

}

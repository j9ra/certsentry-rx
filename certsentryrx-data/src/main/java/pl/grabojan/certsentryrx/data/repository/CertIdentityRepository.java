package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.CertIdentity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CertIdentityRepository extends ReactiveCrudRepository<CertIdentity, Long> {

	Flux<CertIdentity> findBySerialNumber(String serialNumber);
	
	Flux<CertIdentity> findByIssuer(String issuer);
	
	Flux<CertIdentity> findBySubject(String subject);
	
	Mono<CertIdentity> findBySerialNumberAndIssuer(String serialNumber, String issuerName);

	Flux<CertIdentity> findByServiceId(Long serviceId);
	
}

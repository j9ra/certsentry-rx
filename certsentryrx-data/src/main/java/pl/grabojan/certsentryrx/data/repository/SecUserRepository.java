package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.SecUser;
import reactor.core.publisher.Mono;

public interface SecUserRepository extends ReactiveCrudRepository<SecUser, Long> {

	Mono<SecUser> findByUsername(String username);

	Mono<SecUser> findByApplicationId(Long applicationId);
	
}

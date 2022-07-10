package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.SecRole;
import reactor.core.publisher.Flux;

public interface SecRoleRepository extends ReactiveCrudRepository<SecRole, String> {

	  Flux<SecRole> findByUsername(String username);
}

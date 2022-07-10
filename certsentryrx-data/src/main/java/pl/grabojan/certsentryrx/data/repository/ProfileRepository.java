package pl.grabojan.certsentryrx.data.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.Profile;
import reactor.core.publisher.Mono;

public interface ProfileRepository extends ReactiveCrudRepository<Profile, Long> {
	
	Mono<Profile> findByName(String name);

}

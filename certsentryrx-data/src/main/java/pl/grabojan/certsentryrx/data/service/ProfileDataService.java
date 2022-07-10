package pl.grabojan.certsentryrx.data.service;

import lombok.RequiredArgsConstructor;
import pl.grabojan.certsentryrx.data.model.Profile;
import pl.grabojan.certsentryrx.data.repository.ProfileRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class ProfileDataService {
	
	private final ProfileRepository profileRepository;
	
	public Flux<Profile> getAllProfiles() {
				
		return profileRepository.findAll();
	}
	
	public Mono<Profile> getProfile(String name) {
		
		return profileRepository.findByName(name);
	}

}

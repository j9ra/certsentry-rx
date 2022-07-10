package pl.grabojan.certsentryrx.data.service;

import lombok.RequiredArgsConstructor;
import pl.grabojan.certsentryrx.data.model.SecUser;
import pl.grabojan.certsentryrx.data.repository.ApplicationRepository;
import pl.grabojan.certsentryrx.data.repository.SecRoleRepository;
import pl.grabojan.certsentryrx.data.repository.SecUserRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SecUserAppDataService {
	
	private final ApplicationRepository applicationRepository;
	private final SecUserRepository secUserRepository;
	private final SecRoleRepository secRoleRepository;

	
	public Mono<SecUser> getUserByName(String username) {
		return secUserRepository.findByUsername(username)
				.flatMap( user -> 
						Mono.just(user)
							.zipWith(secRoleRepository.findByUsername(user.getUsername()).collectList())
							.map(tupla -> tupla.getT1().withAuthorities(tupla.getT2()))	
							.flatMap(u -> { 
								if(u.getApplicationId() != null) {		
									return Mono.just(u)
											.zipWith(applicationRepository.findById(user.getApplicationId()))
											.map(tupla -> tupla.getT1().withApplication(tupla.getT2()));
								} 
								return Mono.just(u);
							})
				);
	}
	
	
	public Mono<SecUser> getUserByApiKey(String apiKey) {
		
		return applicationRepository.findByApiKey(apiKey)
				.flatMap(app -> Mono.just(app)
								.zipWith(secUserRepository.findByApplicationId(app.getId()))
								.map(tupla -> tupla.getT2().withApplication(tupla.getT1())))
				.flatMap(user -> Mono.just(user)
							.zipWith(secRoleRepository.findByUsername(user.getUsername()).collectList())
							.map(tupla -> tupla.getT1().withAuthorities(tupla.getT2())));
		
	}
}

package pl.grabojan.certsentryrx.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.data.model.TrustedList;
import pl.grabojan.certsentryrx.data.model.TrustedListUpdate;
import pl.grabojan.certsentryrx.data.repository.TrustedListRepository;
import pl.grabojan.certsentryrx.data.repository.TrustedListUpdateRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
public class TrustedListDataService {

	private final TrustedListRepository trustedListRepository;
	private final TrustedListUpdateRepository trustedListUpdateRepository;
		
	public Mono<TrustedList> getListForTerritory(String territory) {
		log.debug("TL Territory: {}", territory);
		
		return trustedListRepository.findByTerritory(territory).last();
	}
	
	public Mono<TrustedList> getListOfTheList() {
		return getListForTerritory("EU");
	}
	
	public Flux<TrustedListUpdate> getUpdatesForList(String territory) {
		
		return trustedListRepository.findByTerritory(territory)
				.last()
				.flatMapMany( tl -> trustedListUpdateRepository.findByTrustedListId(tl.getId()));
	}
	
	// TODO implement SAVE
	
}

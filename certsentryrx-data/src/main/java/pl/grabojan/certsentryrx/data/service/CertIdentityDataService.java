package pl.grabojan.certsentryrx.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.data.model.CertIdentity;
import pl.grabojan.certsentryrx.data.repository.CertIdentityRepository;
import reactor.core.publisher.Flux;


@Slf4j
@RequiredArgsConstructor
public class CertIdentityDataService {

	private final CertIdentityRepository certIdentityRepository;

	public Flux<CertIdentity> getIssuers(String issuerName) {
		log.debug("Searching issuers for name: [{}]", issuerName);
		// cert issuerName is CA subjectName
		return certIdentityRepository.findBySubject(issuerName);
	}
		
}

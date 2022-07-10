package pl.grabojan.certsentryrx.data.repository;

import java.util.Date;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import pl.grabojan.certsentryrx.data.model.TrustedList;
import pl.grabojan.certsentryrx.data.model.TrustedListType;
import reactor.core.publisher.Flux;

public interface TrustedListRepository extends ReactiveCrudRepository<TrustedList, Long> {
	
	Flux<TrustedList> findByType(TrustedListType type);
	
	Flux<TrustedList> findByTerritory(String territory);
	
	Flux<TrustedList> findByDistributionPoint(String distributionPoint);
	
	Flux<TrustedList> findByIsValid(Boolean isValid);
	
	Flux<TrustedList> findByListIssueBetween(Date listIssueBegin, Date listIssueEnd);
	
	Flux<TrustedList> findByNextUpdateBetween(Date nextUpdateBegin, Date nextUpdateEnd);

}

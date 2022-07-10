package pl.grabojan.certsentryrx.data.service;

import lombok.RequiredArgsConstructor;
import pl.grabojan.certsentryrx.data.model.Service;
import pl.grabojan.certsentryrx.data.model.SupplyPoint;
import pl.grabojan.certsentryrx.data.repository.CertIdentityRepository;
import pl.grabojan.certsentryrx.data.repository.ExtensionRepository;
import pl.grabojan.certsentryrx.data.repository.ServiceRepository;
import pl.grabojan.certsentryrx.data.repository.SupplyPointRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceDataService {
	
	private final ServiceRepository serviceRepository;
	private final SupplyPointRepository supplyPointRepository;
	private final CertIdentityRepository certIdentityRepository;
	private final ExtensionRepository serviceExtensionRepository;

	
	public Flux<SupplyPoint> getServiceSupplyPoints(Long serviceId) {
				
		return supplyPointRepository.findByServiceId(serviceId);
	}
	
	public Mono<Service> getServiceByCertSerialNumberAndIssuerName(String serialNumber, String issuerName) {
		
		return certIdentityRepository.findBySerialNumberAndIssuer(serialNumber, issuerName)
				.flatMap(cert -> serviceRepository.findById(cert.getServiceId()))
				.flatMap(service -> Mono.just(service)
										.zipWith(certIdentityRepository.findByServiceId(service.getId()).collectList())
										   .map(tuple -> tuple.getT1().withCertIdentities(tuple.getT2()))
										.zipWith(supplyPointRepository.findByServiceId(service.getId()).collectList())
											.map(tuple -> tuple.getT1().withSupplyPoints(tuple.getT2()))
										.zipWith(serviceExtensionRepository.findByServiceId(service.getId()).collectList())
											.map(tuple -> tuple.getT1().withExtensions(tuple.getT2()))
						);
	}
	
}

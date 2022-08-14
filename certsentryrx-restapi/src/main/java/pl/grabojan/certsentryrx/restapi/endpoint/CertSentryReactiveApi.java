package pl.grabojan.certsentryrx.restapi.endpoint;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.data.model.Profile;
import pl.grabojan.certsentryrx.data.repository.CertIdentityRepository;
import pl.grabojan.certsentryrx.data.repository.ProfileRepository;
import pl.grabojan.certsentryrx.data.service.EventLogDataService;
import pl.grabojan.certsentryrx.restapi.pkix.CertPathHandler;
import pl.grabojan.certsentryrx.restapi.pkix.PathParams;
import pl.grabojan.certsentryrx.restapi.pkix.RevocationDataParams;
import pl.grabojan.certsentryrx.util.cert.PkixURIResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
public class CertSentryReactiveApi {
		
	private final ProfileRepository profileRepository;
	
	private final CertIdentityRepository certIdentityRepository;
	
	private final CertPathHandler certPathHandler;
	
	private final EventLogDataService eventLogDataService;
	
	private final PkixURIResolver pkixURIResolver;
	
	private final Validator validator = new RequestsValidator();


	public Mono<ServerResponse> listProfiles(ServerRequest request) {
		    	
	   	Flux<ProfileResponse> profiles = profileRepository.findAll()
	    			.map(this::toProfileResponse);    	
	    	
	   	return ServerResponse.ok()
	   				.contentType(MediaType.APPLICATION_JSON)
	    			.body(profiles, ProfileResponse.class);
	}
	    
	public Mono<ServerResponse> getProfile(ServerRequest request) {

		String profileName = request.pathVariable("name");

		return profileRepository
				.findByName(profileName)
				.map(this::toProfileResponse)
				.flatMap(r -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just(r), ProfileResponse.class))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	    
	public Mono<ServerResponse> validation(ServerRequest request) {
	    	
	    Mono<ValidationRequest> validationRequest = request.bodyToMono(ValidationRequest.class).doOnNext(this::validate);
	    Mono<X509Certificate> userCertToValidate = validationRequest
		    		.flatMap(req -> Mono.just(certPathHandler.mapCertificate(req.getCert())))
		    		.share();
	    
	    return userCertToValidate
	    		.flatMap( cert -> {
	    			certPathHandler.abortIfCertIsExpired(cert);
	    			return Mono.just(cert);
	    		})
	    		.flatMap(cert -> Mono.just(cert)
	    								.zipWith(certIdentityRepository.findBySubject(certPathHandler.resolveX500Principal(cert))
	    										.map(ci -> certPathHandler.mapCertificate(ci.getValue()))
	    										.collectList(),
	    								(c, ca) -> new PathParams(c, ca))
	    		)
	    		.flatMap(pathParams ->  
	    			Mono.fromCallable(() -> certPathHandler.buildPath(pathParams.getTargetCert(), pathParams.getCaCerts()))
	    				.subscribeOn(Schedulers.boundedElastic())
	    		)
	    		.flatMap(certPath -> { 
	    			X509Certificate uc = certPathHandler.mapUserCertificate(certPath);
	    			RevocationDataParams rdp = certPathHandler.resolveRevocationData(uc);
	    			
	    			return rdp.hasOcsp() ? Mono.just(certPath)
    											.zipWith(pkixURIResolver.queryOCSP(rdp.getOcspURI(), certPath.getAnchor().getTrustedCert(), uc))
    												.map(tuple -> tuple.getT1().addOcspData(tuple.getT2()))
    									: rdp.hasCrl() ? Mono.just(certPath)
    			    	    								  .zipWith(pkixURIResolver.downloadCrl(rdp.getCrlURI()))
    			    	    								  	.map(tuple -> tuple.getT1().addCrlData(certPathHandler.mapCrl(tuple.getT2())))
    			    	    				: Mono.error(pkixURIResolver.failUnresolved()) ;
	    		})
	    		.flatMap(certPath -> 
	    			 Mono.fromCallable(() -> certPathHandler.validatePath(certPath.getAnchor(), certPath.getCertPath(),
	    					 certPath.getCrls(), certPath.getOcspData()))
	    			 			.map( pubKey -> { return certPath; })
	    			 			.subscribeOn(Schedulers.boundedElastic())
	    		)
	    		.flatMap( c -> {
	    			String ref = UUID.randomUUID().toString();
	    			return request.principal()
	    					.map(principal -> principal.getName())
	    					.flatMap(user -> 
	    								eventLogDataService.logInfoEvent("Validation", 
	    										createMsg((X509Certificate)c.getCertPath().getCertificates().get(0), ref, "Valid"),
	    										user)
	    								.flatMap(u -> ServerResponse.ok()
	    												.contentType(MediaType.APPLICATION_JSON)
	    												.bodyValue(ValidationResponse.valid(ref,
	    														certPathHandler.toStringPath(certPathHandler.toCertList(c))))
	    								)
	    					);
	    		})
	    		.onErrorResume(e ->  Mono.just("Error occured - " + e.getMessage())
	    				.flatMap(errormsg -> {
	    					log.error("Error: {}", errormsg);
	    					return userCertToValidate
	    							.flatMap(xc -> {
	    								String ref = UUID.randomUUID().toString();
	    								return request.principal()
	    				    					.map(principal -> principal.getName())
	    				    					.flatMap(user -> 
	    											eventLogDataService.logErrorEvent("Validation", createMsg(xc, ref, errormsg), user)
	    												.flatMap(u -> ServerResponse.ok()
	    																.contentType(MediaType.APPLICATION_JSON)
	    																.bodyValue(ValidationResponse.invalid(ref, 
	    																		certPathHandler.toStringPath(Collections.singletonList(xc)), errormsg))
	    										));
	    							});
	    				})
	    		);
	    	
	    }
	    
	private void validate(ValidationRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "request");
        validator.validate(request, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString()); 
        }
    }
	
	private ProfileResponse toProfileResponse(Profile profile) {
		ProfileResponse p = new ProfileResponse();
		p.setName(profile.getName());
		p.setTerritory(profile.getTerritory());
		p.setProvider(profile.getProvider() != null ? profile.getProvider() : "" );
		p.setService_info(profile.getServiceInfo() != null ? profile.getServiceInfo() : "");
		return p;
	}
	
	    
    private String createMsg(X509Certificate cert, String ref, String msg) {
    	
		StringBuilder sb = new StringBuilder();
		sb.append("Reference").append('=').append(ref).append('|');
		sb.append("SerialNumber").append('=').append(cert.getSerialNumber().toString()).append('|');
		sb.append("Issuer").append('=').
			append(cert.getIssuerX500Principal()).
			append('|');
		sb.append("Message").append('=').append(msg).append('|');
		
		return sb.toString();
	}
	 
	
}

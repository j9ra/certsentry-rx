package pl.grabojan.certsentryrx.util.cert;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.util.hc.HttpResource;
import pl.grabojan.certsentryrx.util.hc.HttpResourceException;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
public class PkixURIResolver {

	private final HttpResource httpResource;
		
	public Mono<byte[]> downloadCrl(String crlURI) {
		
		log.debug("Downloading CRL from: {}", crlURI);
		
		try {
			return httpResource.get(crlURI);
		} catch(HttpResourceException e) {	
			throw new UnresolvedResourceException("Unable to retrive CRL from: " + crlURI, e); 
		} finally {
			log.debug("Successfully downloaded CRL from: {}", crlURI);	
		}
		
		
	}
	
	public Mono<Map<X509Certificate, List<byte[]>>> queryOCSP(String ocspURI, X509Certificate issuerCA, X509Certificate userCert) {
		
		log.debug("Query OCSP responder at: {}", ocspURI);
		
		byte[] nonce = OcspClientService.createOcspNonce(userCert);
		byte[] ocspReqBlob =  OcspClientService.generateOCSPRequest(issuerCA, userCert, nonce);
		
		try {
			return httpResource.post(ocspURI, ocspReqBlob).map( respBlob -> Map.of(userCert, Arrays.asList( respBlob, nonce)));
		} catch(HttpResourceException e) {	
			throw new UnresolvedResourceException("Unable to retrive CRL from: " + ocspURI, e); 
		} finally {
			log.debug("Successful response from OCSP responder at: {}", ocspURI);
		}
	}
	
	public UnresolvedResourceException failUnresolved() {
		throw new UnresolvedResourceException("Unable to resolve resource - unsupported method");
	}

	
}

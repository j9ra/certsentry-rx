package pl.grabojan.certsentryrx.util.cert;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import pl.grabojan.certsentryrx.util.hc.HttpResource;
import pl.grabojan.certsentryrx.util.hc.HttpResourceException;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class PkixURIResolver {

	private final HttpResource httpResource;
		
	public Mono<byte[]> downloadCrl(String crlURI) {
		
		try {
			return httpResource.get(crlURI);
		} catch(HttpResourceException e) {	
			throw new UnresolvedResourceException("Unable to retrive CRL from: " + crlURI, e); 
		}
	}
	
	public Mono<Map<X509Certificate, List<byte[]>>> queryOCSP(String ocspURI, X509Certificate issuerCA, X509Certificate userCert) {
		byte[] nonce = OcspClientService.createOcspNonce(userCert);
		byte[] ocspReqBlob =  OcspClientService.generateOCSPRequest(issuerCA, userCert, nonce);
		
		try {
			return httpResource.post(ocspURI, ocspReqBlob).map( respBlob -> Map.of(userCert, Arrays.asList( respBlob, nonce)));
		} catch(HttpResourceException e) {	
			throw new UnresolvedResourceException("Unable to retrive CRL from: " + ocspURI, e); 
		}
	}
	
	public UnresolvedResourceException failUnresolved() {
		throw new UnresolvedResourceException("Unable to resolve resource - unsupported method");
	}

	
}

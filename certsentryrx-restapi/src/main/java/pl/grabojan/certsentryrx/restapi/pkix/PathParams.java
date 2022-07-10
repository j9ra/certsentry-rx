package pl.grabojan.certsentryrx.restapi.pkix;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class PathParams {

	private final X509Certificate targetCert;
	
	private final List<X509Certificate> caCerts;
	
	public PathParams(X509Certificate cert, List<X509Certificate> caCerts) {
		this.targetCert = cert;
		this.caCerts = new ArrayList<>(caCerts);
	}
	

}

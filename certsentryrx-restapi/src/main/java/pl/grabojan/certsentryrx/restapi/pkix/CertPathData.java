package pl.grabojan.certsentryrx.restapi.pkix;

import java.security.cert.CertPath;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;


@Data
public class CertPathData {
	private TrustAnchor anchor;
	private CertPath certPath;

	@Getter
	private List<X509CRL> crls = new ArrayList<>();
	private Map<X509Certificate,List<byte[]>> ocspData = new HashMap<>();

	public CertPathData(TrustAnchor anchor, CertPath certPath) {
		this.anchor = anchor;
		this.certPath = certPath;
	}
	
	public CertPathData addCrlData(X509CRL crl) {
		crls.add(crl);
		return this;
	}

	public CertPathData addOcspData(Map<X509Certificate,List<byte[]>> ocspRespNonce) {
		ocspData.putAll(ocspRespNonce);
		return this;
	}
}

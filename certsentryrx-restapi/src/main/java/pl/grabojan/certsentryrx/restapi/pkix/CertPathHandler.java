package pl.grabojan.certsentryrx.restapi.pkix;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

import lombok.extern.slf4j.Slf4j;
import pl.grabojan.certsentryrx.util.cert.CertificateServiceHelper;


@Slf4j
public class CertPathHandler {
	
	private final boolean oneLevel = true;
	
	
	
	private final CertificateServiceHelper certificateServiceHelper;
	
	public CertPathHandler(CertificateServiceHelper certificateServiceHelper) {
		this.certificateServiceHelper = certificateServiceHelper;
	}
	
	public X509Certificate mapCertificate(String cert) {
		return certificateServiceHelper.parseCertificate(cert);
	}

	public void abortIfCertIsExpired(X509Certificate cert) throws CertPathNotValidException {
		try {
			cert.checkValidity();
			log.debug("Target certificate within valid date range");
		} catch (CertificateNotYetValidException|CertificateExpiredException e) {
			String msg = "Target certificate is expired";
			log.error(msg);
			throw new CertPathNotValidException(msg);
		} 
	}
	
	public String resolveX500Principal(X509Certificate targetCert) {
		log.debug("Resolving issuers for cert: {}", targetCert);
		String issuerName = certificateServiceHelper.resolveX500Principal(targetCert.getIssuerX500Principal());
		log.debug("Issuer: {}", issuerName);
		
		return issuerName;
	}

	public X509Certificate mapCertificate(byte[] value) {
		return certificateServiceHelper.parseCertificate(value);
	}
	
	
	public CertPathData buildPath(X509Certificate userCert, List<X509Certificate> caCerts) throws CertPathNotFoundException {
		
		log.debug("Building CertPath");
		
		CertPathBuilder cpb = createCertPathBuilder();
		
		Set<TrustAnchor> rootCAs = new HashSet<>();
		
		List<X509Certificate> pathCerts = new ArrayList<>();
				
	    X509CertSelector targetConstraints = new X509CertSelector();
	    targetConstraints.setCertificate(userCert);
	    pathCerts.add(userCert);
	    
	    if(oneLevel) {
	    	makeTrustCerts(caCerts, rootCAs);
	    } else {
	    	splitCerts(caCerts, rootCAs, pathCerts);
	    }
	    
	    log.debug("Root CAs set: {}", rootCAs);
	    log.debug("SubCAs set: {}", pathCerts);
	    	    
	    PKIXBuilderParameters params = null;
		try {
			params = new PKIXBuilderParameters(rootCAs, targetConstraints);
		} catch (InvalidAlgorithmParameterException e) {
			log.error("PKIXBuilderParameters ctor() failed", e);
			throw new CertPathFailureException("Unable to create PKIXBuilderParameters instance",e);
		}
	    params.addCertStore(createCertStore(pathCerts));
		params.setRevocationEnabled(false); // only building path
	  	    
	    try {
	        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) cpb.build(params);
	        log.debug("Found CertPath");
	        return new CertPathData(result.getTrustAnchor(), result.getCertPath());
	        
	    } catch (CertPathBuilderException e) {
	    	log.error("Building CertPath failed", e);
	    	throw new CertPathNotFoundException("CertPath not found", e);
	    } catch (InvalidAlgorithmParameterException e) {
	    	log.error("Invalid algorithm found by CertPath builder", e);
	    	throw new CertPathFailureException("Build failed Invalid Algorithm", e);
	    }
	}
	
	public String resolveOcspResponderURI(X509Certificate xc) {
		return certificateServiceHelper.getOcspResponderURI(xc);
	}
	
	public String resolveCRLDPServerURI(X509Certificate xc) {
		return certificateServiceHelper.getCRLDPServerURI(xc);
	}
	
	public PublicKey validatePath(TrustAnchor trustAnchor, CertPath cp, List<X509CRL> crls, Map<X509Certificate, List<byte[]>> ocspData) throws CertPathNotValidException, CertPathPartialValidException {
		
		log.debug("Validating path");
		log.debug("TrustAnchor: {}", trustAnchor);
		log.debug("CertPath: {}", cp);
		log.debug("RevocationData: crl: {}, ocsp: {}", crls, ocspData);
		
		CertPathValidator cpv = createCertPathValidator();
		
		PKIXParameters params = null;
		try {
			params = new PKIXParameters(Collections.singleton(trustAnchor));
		} catch (InvalidAlgorithmParameterException e) {
			throw new CertPathFailureException("Unable to create PKIXBuilderParameters instance",e);
		}
		
		params.setRevocationEnabled(false); // handled by RevocationChecker
		PKIXRevocationChecker rc = (PKIXRevocationChecker)cpv.getRevocationChecker();
		
		Set<Option> options = new HashSet<>();
		options.add(Option.SOFT_FAIL); // Ignore network failures
		options.add(Option.NO_FALLBACK); // dont switch ocsp/crl methods
		
		if(oneLevel) {
			options.add(Option.ONLY_END_ENTITY); // Check only userCert, skip CAs
			log.debug("OneLevel issuer CA check is enabled");
		}
		
				
		// additional params
		if(crls.size() > 0) {
			log.debug("Adding revocationData CRLs");
			params.addCertStore(createCertStore(crls));
			options.add(Option.PREFER_CRLS); // Prefers CDP instead of OCSP
			log.debug("CRLs CDP is preferred");
		}
		
		if(ocspData.size() > 0) {
			log.debug("Adding revocationData OCSP responses");
			// convert params to PKIXRevocationChecker format
				
			Map<X509Certificate,byte[]> responses = new HashMap<>(); 
			List<java.security.cert.Extension> extns = new ArrayList<>(); 
										
			ocspData.forEach( 
				(cert, blobs) -> { 
					responses.put(cert, blobs.get(0));
					if(blobs.get(1) != null) {
						log.debug("nonce!");
						extns.add(new NonceExtension(blobs.get(1)));
					}
				});
					
			rc.setOcspResponses(responses);
			rc.setOcspExtensions(extns);
		}
		
		rc.setOptions(options); 
				
		params.addCertPathChecker(rc);
		
		try {
			
			PKIXCertPathValidatorResult pcpvr = (PKIXCertPathValidatorResult)cpv.validate(cp, params);
			log.debug("CertPath is valid");
			List<CertPathValidatorException> exps = rc.getSoftFailExceptions();
			if(!exps.isEmpty()) {
				log.error("Soft fail exceptions: {}", exps);
				throw new CertPathPartialValidException("Unable to check revocation", exps.get(0));
			}
						
			// public key of validated user certificate 
			// can be safely use to validate signature
			return pcpvr.getPublicKey();
			
		} catch (CertPathValidatorException e) {
			throw new CertPathNotValidException("CertPath not valid", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new CertPathFailureException("Validation failed Invalid Algorithm",e);
		}
	}
	
	
//	protected RevocationData checkRevocationInfo(List<X509Certificate> certPath) {	
//		
//		log.debug("Checking revocation infos");
//		log.debug("CertPath to check: {}", certPath);
//		log.debug("OneLevel?={}, RevokePreferOCSP?={}, RevokePreferCDP?={}, RevokeOcspUseNonce?={}", 
//				oneLevel, revokePreferOCSP, revokePreferCDP, revokeOcspUseNonce);
//		
//		RevocationData revData = new RevocationData();
//
//		// CertPath is constructed in revers order - from usercert by subca to rootca
//		for(int idx=0; idx < certPath.size()-1; ++idx) {
//			X509Certificate xc = certPath.get(idx);
//			log.debug("Checking cert with subject: [{}]", xc.getSubjectX500Principal());
//						
//			if((revokePreferOCSP && certificateServiceHelper.getOcspResponderURI(xc) != null) ||
//					(revokePreferCDP && certificateServiceHelper.getCRLDPServerURI(xc) != null)) {
//			
//				log.debug("Cert have OCSP/CDP extensions");
//				continue;
//			}
//			
//			// no OCSP or CDP found in cert - try use supplyPoint from TSL
//			X509Certificate xcIssuer = certPath.get(idx+1);			
//			List<SupplyPoint> suppPoints = certIdentityDataService.getCertIdentitySupplyPoints(
//					xcIssuer.getSerialNumber().toString(),
//					certificateServiceHelper.resolveX500Principal(xcIssuer.getIssuerX500Principal()));
//						
//			if(suppPoints.isEmpty()) {
//				log.error("No revocation information found in cert and no SupplyPoint available");
//				throw new CertPathPartialValidException("No revocation data available");
//			}
//			
//			log.debug("Found SupplyPoint(s): [{}]", suppPoints);
//						
//			// simple map - only one record by type
//			Map<SupplyPointType,SupplyPoint> supps = suppPoints.
//					stream().
//					collect(Collectors.toMap(s -> s.getType(),
//											Function.identity(),
//											(existing, replacement) -> existing));
//						
//			if(revokePreferOCSP && supps.containsKey(SupplyPointType.OCSP)) {
//				String ocspUri = supps.get(SupplyPointType.OCSP).getPointUri();
//				try {
//					byte[] ocspNonce = (revokeOcspUseNonce) ? createOcspNonce(xc) : null;
//					byte[] ocspReq = generateOCSPRequest(xcIssuer,xc,ocspNonce);
//					byte[] ocspResp = pkiUriResolver.resolveWithPost(ocspUri, ocspReq);
//					revData.getOcspResponses().put(xc, Arrays.asList(ocspResp, ocspNonce));
//					log.debug("Added RevocationData from SupplyPoint [{}]",
//							supps.get(SupplyPointType.OCSP));
//				} catch(UnresolvedResourceException e) {
//					log.error("Unresolved resource for OCSP " + ocspUri, e);
//					throw new CertPathPartialValidException("Unresolved resource", e);
//				}		
//			} else if(supps.containsKey(SupplyPointType.CRL)) {
//				String crlUri = supps.get(SupplyPointType.CRL).getPointUri();
//				try {
//					X509CRL crl = pkiUriResolver.getCrl(crlUri);
//					revData.getRevocationLists().add(crl);
//					log.debug("Added RevocationData from SupplyPoint [{}]",
//							supps.get(SupplyPointType.CRL));
//				} catch(UnresolvedResourceException e) {
//					log.error("Unresolved resource for CDP " + crlUri, e);
//					throw new CertPathPartialValidException("Unresolved resource", e);
//				}
//			} else {
//				log.error("No prefered SupplyPoint available");
//				throw new CertPathPartialValidException("No revocation data available");
//			}
//			
//		}
//		
//		return revData;
//	}
	
	
	
	public List<X509Certificate> toCertList(CertPathData cp) {
		List<X509Certificate> certsRet = new ArrayList<>();
		cp.getCertPath().getCertificates().forEach(
				c -> certsRet.add((X509Certificate)c));
		certsRet.add(cp.getAnchor().getTrustedCert());
		return certsRet;
	}
	
	public List<String> toStringPath(List<X509Certificate> path) {
		return path.stream().
				map(c -> certificateServiceHelper.certificateToString(c)).
				collect(Collectors.toList());
	}

	
	private void makeTrustCerts(List<X509Certificate> caCerts, Set<TrustAnchor> rootCAs) {
		
		if(caCerts == null || rootCAs == null) {
			throw new IllegalStateException("Argument cant be null!");
		}
		caCerts.forEach(cert -> rootCAs.add(new TrustAnchor(cert, null)));
	}
	
private void splitCerts(List<X509Certificate> caCerts, Set<TrustAnchor> rootCAs, List<X509Certificate> subCAs) {
		
		if(caCerts == null || rootCAs == null || subCAs == null) {
			throw new IllegalStateException("Argument cant be null!");
		}
		
		caCerts.forEach(cert -> {
			if(cert.getSubjectX500Principal().equals(cert.getIssuerX500Principal())) {
				rootCAs.add(new TrustAnchor(cert, null));
			} else {
				subCAs.add(cert);
			}
		});
	}
	
	private CertPathBuilder createCertPathBuilder() {
		try {
			return CertPathBuilder.getInstance("PKIX");
		} catch (NoSuchAlgorithmException e) {
			log.error("CertPathBuilder.getInstance() failed", e);
			throw new CertPathFailureException("Unable to create CertPathBuilder instance", e);
		}
	}
	
	private CertStore createCertStore(Collection<?> collection) {
		 CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(collection);
		 try {
			return CertStore.getInstance("Collection", ccsp);
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
			log.error("CertStore.getInstance() failed", e);
			throw new CertPathFailureException("Unable to create CertStore instance", e);
		}
	}
	
	private CertPathValidator createCertPathValidator() {
		try {
			return CertPathValidator.getInstance("PKIX");
		} catch (NoSuchAlgorithmException e) {
			log.error("CertPathValidator.getInstance() failed", e);
			throw new CertPathFailureException("Unable to create CertPathValidator instance", e);
		}
	}

	public X509Certificate mapUserCertificate(CertPathData certPath) {
		
		return (X509Certificate)certPath.getCertPath().getCertificates().get(0);
		
		
	}
	
	public X509CRL mapCrl(byte[] blob) {
		return certificateServiceHelper.parseCRL(blob);
	}

	public RevocationDataParams resolveRevocationData(X509Certificate uc) {
		
		String ocspURI = resolveOcspResponderURI(uc);
		String crlURI = resolveCRLDPServerURI(uc);
		
		log.debug("Revocation data for cert: {}, ocsp: {}, crl: {}", uc.getSubjectX500Principal(), ocspURI, crlURI);
		
		return new RevocationDataParams(ocspURI, crlURI);
	}
	
	
	private static class NonceExtension implements java.security.cert.Extension {
		
		private byte[] nonce;
		
		public NonceExtension(byte[] nonce) {
			this.nonce = nonce;
		}
		
		@Override
		public String getId() {
			return OCSPObjectIdentifiers.id_pkix_ocsp_nonce.getId();
		}

		@Override
		public boolean isCritical() {
			return false;
		}

		@Override
		public byte[] getValue() {
			return nonce;
		}

		@Override
		public void encode(OutputStream out) throws IOException {
			out.write(nonce);
		}
	}
}

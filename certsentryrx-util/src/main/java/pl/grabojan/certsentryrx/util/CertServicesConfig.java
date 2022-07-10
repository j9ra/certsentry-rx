package pl.grabojan.certsentryrx.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import pl.grabojan.certsentryrx.util.cert.CertificateServiceHelper;
import pl.grabojan.certsentryrx.util.cert.SecurityProviderRegistrar;


@Configuration
public class CertServicesConfig {

	@Bean(initMethod = "init")
	public SecurityProviderRegistrar securityProviderRegistrar() {
		return new SecurityProviderRegistrar(new Class<?>[] {
			BouncyCastleProvider.class
		});
	}
	
	@Bean
	@DependsOn("securityProviderRegistrar")
	public CertificateServiceHelper	certificateServiceHelper() {
		return new CertificateServiceHelper();
	}
	
}

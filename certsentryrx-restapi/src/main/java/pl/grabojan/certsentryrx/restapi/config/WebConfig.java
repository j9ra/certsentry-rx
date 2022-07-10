package pl.grabojan.certsentryrx.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import pl.grabojan.certsentryrx.data.DataConfig;
import pl.grabojan.certsentryrx.data.repository.CertIdentityRepository;
import pl.grabojan.certsentryrx.data.repository.ProfileRepository;
import pl.grabojan.certsentryrx.data.service.EventLogDataService;
import pl.grabojan.certsentryrx.restapi.endpoint.CertSentryReactiveApi;
import pl.grabojan.certsentryrx.restapi.pkix.CertPathHandler;
import pl.grabojan.certsentryrx.util.CertServicesConfig;
import pl.grabojan.certsentryrx.util.HttpClientConfig;
import pl.grabojan.certsentryrx.util.cert.CertificateServiceHelper;
import pl.grabojan.certsentryrx.util.cert.PkixURIResolver;

@Configuration
@Import({ DataConfig.class, CertServicesConfig.class, HttpClientConfig.class })
public class WebConfig {

	@Bean
    public RouterFunction<?> routerFunctionA(CertSentryReactiveApi certSentryReactiveApi) {
        return RouterFunctions.route()
        		.path("/v1", builder -> builder
        				.GET("/profile/{name}", certSentryReactiveApi::getProfile)
        				.GET("/profile", certSentryReactiveApi::listProfiles)
        				.POST("/validation",certSentryReactiveApi::validation))
        		.build();
    }
	
	@Bean
	public CertSentryReactiveApi certSentryReactiveApi(ProfileRepository profileRepository, 
			CertIdentityRepository certIdentityRepository, EventLogDataService eventLogDataService,
			CertPathHandler certPathHandler,PkixURIResolver pkixURIResolver) {
		return new CertSentryReactiveApi(profileRepository, certIdentityRepository, certPathHandler, 
				eventLogDataService, pkixURIResolver);
	}
	
	@Bean
	public CertPathHandler certPathHandler(CertificateServiceHelper certificateServiceHelper) {
		return new CertPathHandler(certificateServiceHelper);
	}
}

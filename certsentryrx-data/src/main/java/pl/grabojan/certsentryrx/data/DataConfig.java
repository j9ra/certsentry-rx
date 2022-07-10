package pl.grabojan.certsentryrx.data;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import pl.grabojan.certsentryrx.data.repository.ApplicationRepository;
import pl.grabojan.certsentryrx.data.repository.CertIdentityRepository;
import pl.grabojan.certsentryrx.data.repository.EventLogRepository;
import pl.grabojan.certsentryrx.data.repository.ExtensionRepository;
import pl.grabojan.certsentryrx.data.repository.ProfileRepository;
import pl.grabojan.certsentryrx.data.repository.SecRoleRepository;
import pl.grabojan.certsentryrx.data.repository.SecUserRepository;
import pl.grabojan.certsentryrx.data.repository.ServiceRepository;
import pl.grabojan.certsentryrx.data.repository.SupplyPointRepository;
import pl.grabojan.certsentryrx.data.repository.TrustedListRepository;
import pl.grabojan.certsentryrx.data.repository.TrustedListUpdateRepository;
import pl.grabojan.certsentryrx.data.service.CertIdentityDataService;
import pl.grabojan.certsentryrx.data.service.EventLogDataService;
import pl.grabojan.certsentryrx.data.service.ProfileDataService;
import pl.grabojan.certsentryrx.data.service.SecUserAppDataService;
import pl.grabojan.certsentryrx.data.service.ServiceDataService;
import pl.grabojan.certsentryrx.data.service.TrustedListDataService;

@Configuration
@EnableAutoConfiguration
@EnableR2dbcRepositories
public class DataConfig {

	
	@Bean
	public CertIdentityDataService certIdentityDataService(CertIdentityRepository certIdentityRepository) {
		return new CertIdentityDataService(certIdentityRepository);
	}
	
	@Bean
	public EventLogDataService eventLogDataService(EventLogRepository eventLogRepository) {
		return new EventLogDataService(eventLogRepository);
	}
	
	@Bean
	public ProfileDataService profileDataService(ProfileRepository profileRepository) {
		return new ProfileDataService(profileRepository);
	}
	
	@Bean
	public SecUserAppDataService secUserAppDataService(ApplicationRepository applicationRepository, 
			SecUserRepository secUserRepository, SecRoleRepository secRoleRepository) {
		return new SecUserAppDataService(applicationRepository, secUserRepository, secRoleRepository);
	}
	
	@Bean
	public ServiceDataService serviceDataService(ServiceRepository serviceRepository,
			SupplyPointRepository supplyPointRepository,
			CertIdentityRepository certIdentityRepository,
			ExtensionRepository extensionRepository) {
		return new ServiceDataService(serviceRepository, supplyPointRepository, certIdentityRepository, extensionRepository);
	}
	
	@Bean
	public TrustedListDataService trustedListDataService(TrustedListRepository trustedListRepository,
			TrustedListUpdateRepository trustedListUpdateRepository) {
		return new TrustedListDataService(trustedListRepository, trustedListUpdateRepository);
	}
	
}

package pl.grabojan.certsentryrx.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = { CertServicesConfig.class, HttpClientConfig.class })
class CertsentryrxUtilApplicationTests {

	@Test
	void contextLoads() {
	}

}

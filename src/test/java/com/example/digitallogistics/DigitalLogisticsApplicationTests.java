package com.example.digitallogistics;

import com.example.digitallogistics.config.TestSecurityConfig;
import com.example.digitallogistics.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	classes = LogisticsApiApplication.class,
	properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
		"spring.data.elasticsearch.repositories.enabled=false"
	}
)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class DigitalLogisticsApplicationTests {

	@Test
	void contextLoads() {
	}

}

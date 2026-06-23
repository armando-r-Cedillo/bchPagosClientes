package com.carga.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.batch.job.enabled=false"
})
class ClientesApplicationTests {

	@Test
	void contextLoads() {
	}

}

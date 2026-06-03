package br.com.busco.viagem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import br.com.busco.viagem.app.RotaGateway;

@SpringBootTest
class BuscoApiApplicationTests {

	@MockBean
	private RotaGateway rotaGateway;

	@Test
	void contextLoads() {
	}

}

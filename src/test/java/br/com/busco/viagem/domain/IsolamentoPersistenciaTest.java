package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.RotaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task 3.1: Rota como agregado local e Viagem persistindo isolada no próprio
 * schema — sem FKs para tabelas de outros contextos (só ids + snapshots).
 */
@DataJpaTest
class IsolamentoPersistenciaTest {

    @Autowired
    private RotaRepository rotaRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Test
    void devePersistirRotaComoAgregadoLocal() {
        Rota rota = Rota.builder()
                .numeroRota("101")
                .nome("Centro - Bairro")
                .enderecos(List.of(
                        new Endereco("Rua A", 100, null, "Centro", "Cidade", "SP", "01001000", null, null),
                        new Endereco("Rua B", 200, null, "Bairro", "Cidade", "SP", "01002000", null, null)))
                .tempoEstimado("40min")
                .distancia("12km")
                .rotasJson("{\"pontos\":[]}")
                .build();

        RotaId id = rotaRepository.save(rota).getId();

        Rota lida = rotaRepository.findById(id).orElseThrow();
        assertThat(lida.getNumeroRota()).isEqualTo("101");
        assertThat(lida.getEnderecos()).hasSize(2);
        assertThat(lida.getEnderecoInicial().getLogradouro()).isEqualTo("Rua A");
        assertThat(lida.getEnderecoFinal().getLogradouro()).isEqualTo("Rua B");
    }

    @Test
    void devePersistirViagemSozinhaSemTabelasAlheias() {
        RotaId rotaId = rotaRepository.save(Rota.builder()
                .numeroRota("202")
                .nome("Rota local")
                .enderecos(List.of())
                .build()).getId();

        Passageiro esperado = Passageiro.of(UUID.randomUUID(), "Maria Silva", "MANHA",
                "Escola Central", "123", "Ponto 1", true);
        Viagem viagem = Viagem.builder()
                .data(LocalDateTime.now().plusDays(1))
                .rota(rotaId)
                .motoristaId(UUID.randomUUID())
                .monitorId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .passageiros(List.of(esperado))
                .periodoViagem(PeriodoViagem.of(
                        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)))
                .build();

        ViagemId id = viagemRepository.save(viagem).getId();

        Viagem lida = viagemRepository.findById(id).orElseThrow();
        assertThat(lida.getStatus()).isEqualTo(Status.PLANEJADA);
        assertThat(lida.getRota()).isEqualTo(rotaId);
        assertThat(lida.getMotoristaId()).isNotNull();
        assertThat(lida.getMonitorId()).isNotNull();
        assertThat(lida.getVeiculoId()).isNotNull();
        assertThat(lida.getPassageiros()).hasSize(1);
        assertThat(lida.getPassageiros().get(0).getNome()).isEqualTo("Maria Silva");
    }
}

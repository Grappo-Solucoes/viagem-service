package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.RotaId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Task 3.3: embarcar (id ou carteirinha, justificativa p/ não planejado,
 * idempotente), embarcar-todos e desembarcar.
 */
class ViagemEmbarqueTest {

    private Viagem viagem;
    private Passageiro maria;
    private Passageiro joao;

    @BeforeEach
    void setUp() {
        maria = Passageiro.of(UUID.randomUUID(), "Maria Silva", "MANHA",
                "Escola Central", "123", "Ponto 1", true);
        joao = Passageiro.of(UUID.randomUUID(), "João Souza", "TARDE",
                "Escola Central", "456", "Ponto 2", true);
        viagem = Viagem.builder()
                .data(LocalDateTime.now().plusDays(1))
                .rota(RotaId.randomId())
                .motoristaId(UUID.randomUUID())
                .monitorId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .passageiros(List.of(maria, joao))
                .periodoViagem(PeriodoViagem.of(
                        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)))
                .build();
        viagem.iniciar(LocalDateTime.now(), List.of());
    }

    @Test
    void deveEmbarcarPassageiroPlanejadoComGps() {
        viagem.embarcarPassageiro(maria, -23.55, -46.63, null);

        assertThat(viagem.getPassageirosABordo()).containsExactly(maria);
        EmbarqueRegistro registro = viagem.getRegistrosEmbarque().get(0);
        assertThat(registro.getLatitudeEmbarque()).isEqualTo(-23.55);
        assertThat(registro.isPlanejado()).isTrue();
    }

    @Test
    void deveEncontrarPlanejadoPorCarteirinha() {
        Carteirinha carteirinha = Carteirinha.of("MARIA SILVA;123;1 - MANHA;ESCOLA CENTRAL");

        assertThat(viagem.encontrarPassageiroPorCarteirinha(carteirinha)).contains(maria);
    }

    @Test
    void deveIgnorarAcentoECaixaNaCarteirinha() {
        Carteirinha carteirinha = Carteirinha.of("maria sílva;999;2 - NOITE;OUTRA ESCOLA");

        assertThat(viagem.encontrarPassageiroPorCarteirinha(carteirinha)).contains(maria);
    }

    @Test
    void deveExigirJustificativaParaNaoPlanejado() {
        Passageiro estranho = Passageiro.comId(UUID.randomUUID());

        assertThatThrownBy(() -> viagem.embarcarPassageiro(estranho, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("justificativa");
        assertThatThrownBy(() -> viagem.embarcarPassageiro(estranho, null, null, "   "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(viagem.getRegistrosEmbarque()).isEmpty();
    }

    @Test
    void deveEmbarcarNaoPlanejadoComJustificativa() {
        Passageiro estranho = Passageiro.comId(UUID.randomUUID());

        viagem.embarcarPassageiro(estranho, null, null, "Aluno de outra rota");

        EmbarqueRegistro registro = viagem.getRegistrosEmbarque().get(0);
        assertThat(registro.isPlanejado()).isFalse();
        assertThat(registro.getJustificativa()).isEqualTo("Aluno de outra rota");
    }

    @Test
    void deveSerIdempotenteNoEmbarqueDuplicado() {
        viagem.embarcarPassageiro(maria, null, null, null);
        viagem.embarcarPassageiro(maria, null, null, null);

        assertThat(viagem.getRegistrosEmbarque()).hasSize(1);
        assertThat(viagem.getPassageirosABordo()).containsExactly(maria);
    }

    @Test
    void deveEmbarcarTodosPendentesDeUmaVez() {
        viagem.embarcarPassageiro(maria, null, null, null);

        List<Passageiro> embarcados = viagem.embarcarTodosPendentes();

        assertThat(embarcados).containsExactly(joao);
        assertThat(viagem.getPassageirosABordo()).containsExactlyInAnyOrder(maria, joao);
    }

    @Test
    void deveDesembarcarComGps() {
        viagem.embarcarPassageiro(maria, null, null, null);

        viagem.desembarcarPassageiro(maria.getPassageiroId(), -23.5, -46.6);

        assertThat(viagem.getPassageirosABordo()).isEmpty();
        EmbarqueRegistro registro = viagem.getRegistrosEmbarque().get(0);
        assertThat(registro.estaEmbarcado()).isFalse();
        assertThat(registro.getLatitudeDesembarque()).isEqualTo(-23.5);
    }

    @Test
    void deveRecusarDesembarqueDeQuemNaoEmbarcou() {
        assertThatThrownBy(() -> viagem.desembarcarPassageiro(joao.getPassageiroId(), null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("embarcado");
        assertThat(viagem.getRegistrosEmbarque()).isEmpty();
    }

    @Test
    void deveRecusarDesembarqueDeQuemNaoEstaNaViagem() {
        assertThatThrownBy(() -> viagem.desembarcarPassageiro(UUID.randomUUID(), null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nessa viagem");
    }
}

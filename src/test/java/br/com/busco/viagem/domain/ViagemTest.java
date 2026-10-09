package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.RotaId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ViagemTest {

    private RotaId rotaId;
    private PeriodoViagem periodoViagem;
    private LocalDateTime data;
    private UUID motoristaId;
    private UUID monitorId;
    private UUID veiculoId;

    @BeforeEach
    void setUp() {
        rotaId = RotaId.randomId();
        periodoViagem = PeriodoViagem.of(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );
        data = LocalDateTime.now().plusDays(1);
        motoristaId = UUID.randomUUID();
        monitorId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
    }

    private Viagem novaViagem() {
        return Viagem.builder()
                .data(data)
                .rota(rotaId)
                .motoristaId(motoristaId)
                .monitorId(monitorId)
                .veiculoId(veiculoId)
                .periodoViagem(periodoViagem)
                .build();
    }

    @Test
    void deveCriarViagemPlanejadaComReferenciasPorId() {
        Passageiro passageiro = Passageiro.comId(UUID.randomUUID());
        Viagem viagem = Viagem.builder()
                .data(data)
                .rota(rotaId)
                .motoristaId(motoristaId)
                .monitorId(monitorId)
                .veiculoId(veiculoId)
                .planejamentoId(UUID.randomUUID())
                .passageiros(List.of(passageiro))
                .periodoViagem(periodoViagem)
                .build();

        assertThat(viagem.getId()).isNotNull();
        assertThat(viagem.getRota()).isEqualTo(rotaId);
        assertThat(viagem.getMotoristaId()).isEqualTo(motoristaId);
        assertThat(viagem.getMonitorId()).isEqualTo(monitorId);
        assertThat(viagem.getVeiculoId()).isEqualTo(veiculoId);
        assertThat(viagem.getPlanejamentoId()).isNotNull();
        assertThat(viagem.getPassageiros()).containsExactly(passageiro);
        assertThat(viagem.getEmbarques()).isEmpty();
        assertThat(viagem.getStatus()).isEqualTo(Status.PLANEJADA);
        assertThat(viagem.getParadas()).isEmpty();
        assertThat(viagem.getAtrasoAcumulado()).isEqualTo(Duration.ZERO);
    }

    @Test
    void deveExigirReferenciasObrigatorias() {
        assertThatThrownBy(() -> Viagem.builder()
                .rota(rotaId).motoristaId(motoristaId).monitorId(monitorId).veiculoId(veiculoId)
                .periodoViagem(periodoViagem).build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveIniciarViagemPlanejada() {
        Viagem viagem = novaViagem();

        List<ParadaPrevista> paradasPrevistas = List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(LocalDateTime.now()).quantidadeAlunos(5).build(),
                ParadaPrevista.builder().ordem(2).horarioPrevisto(LocalDateTime.now().plusMinutes(30)).quantidadeAlunos(3).build()
        );

        LocalDateTime horarioInicio = LocalDateTime.now();
        viagem.iniciar(horarioInicio, paradasPrevistas);

        assertThat(viagem.getStatus()).isEqualTo(Status.INICIADA);
        assertThat(viagem.getDataInicio()).isEqualTo(horarioInicio);
        assertThat(viagem.getParadas()).hasSize(2);
        assertThat(viagem.getParadas().get(0).getOrdem()).isEqualTo(1);
        assertThat(viagem.getParadas().get(1).getOrdem()).isEqualTo(2);
    }

    @Test
    void naoDeveIniciarViagemSeNaoEstiverPlanejada() {
        Viagem viagem = novaViagem();
        viagem.iniciar(LocalDateTime.now(), List.of());

        assertThatThrownBy(() -> viagem.iniciar(LocalDateTime.now(), List.of()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void naoDeveIniciarViagemAposDataAgendada() {
        Viagem viagem = Viagem.builder()
                .data(LocalDateTime.now().minusDays(1))
                .rota(rotaId)
                .motoristaId(motoristaId)
                .monitorId(monitorId)
                .veiculoId(veiculoId)
                .periodoViagem(periodoViagem)
                .build();

        assertThatThrownBy(() -> viagem.iniciar(LocalDateTime.now(), List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Viagem não pode ser iniciada após a data");
    }

    @Test
    void deveRegistrarChegadaESaidaDeParada() {
        Viagem viagem = novaViagem();

        List<ParadaPrevista> paradasPrevistas = List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(LocalDateTime.now()).quantidadeAlunos(5).build(),
                ParadaPrevista.builder().ordem(2).horarioPrevisto(LocalDateTime.now().plusMinutes(30)).quantidadeAlunos(3).build()
        );

        viagem.iniciar(LocalDateTime.now(), paradasPrevistas);

        LocalDateTime chegada = LocalDateTime.now();
        viagem.registrarChegadaParada(1, chegada);
        assertThat(viagem.getParadas().get(0).getStatus()).isEqualTo(StatusParada.CHEGADA_REGISTRADA);
        assertThat(viagem.getParadas().get(0).getHorarioChegadaReal()).isEqualTo(chegada);

        LocalDateTime saida = LocalDateTime.now().plusMinutes(5);
        viagem.registrarSaidaParada(1, saida);
        assertThat(viagem.getParadas().get(0).getStatus()).isEqualTo(StatusParada.CONCLUIDA);
        assertThat(viagem.getParadas().get(0).getHorarioSaidaReal()).isEqualTo(saida);
    }

    @Test
    void deveManterEmAndamentoAoChegarNaUltimaParada() {
        Viagem viagem = novaViagem();

        List<ParadaPrevista> paradasPrevistas = List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(LocalDateTime.now()).quantidadeAlunos(5).build(),
                ParadaPrevista.builder().ordem(2).horarioPrevisto(LocalDateTime.now().plusMinutes(30)).quantidadeAlunos(3).build()
        );

        viagem.iniciar(LocalDateTime.now(), paradasPrevistas);

        viagem.registrarChegadaParada(1, LocalDateTime.now());
        viagem.registrarSaidaParada(1, LocalDateTime.now());

        viagem.registrarChegadaParada(2, LocalDateTime.now());
        assertThat(viagem.getStatus()).isEqualTo(Status.INICIADA);
    }

    @Test
    void devePausarERetomarViagem() {
        Viagem viagem = novaViagem();

        viagem.iniciar(LocalDateTime.now(), List.of());
        assertThat(viagem.isEmAndamento()).isTrue();

        viagem.pausar();
        assertThat(viagem.getStatus()).isEqualTo(Status.PAUSADA);
        assertThat(viagem.isEmAndamento()).isFalse();

        viagem.retomar();
        assertThat(viagem.getStatus()).isEqualTo(Status.INICIADA);
        assertThat(viagem.isEmAndamento()).isTrue();
    }

    @Test
    void deveFinalizarViagemIniciada() {
        Viagem viagem = novaViagem();

        viagem.iniciar(LocalDateTime.now(), List.of());
        LocalDateTime fimReal = LocalDateTime.now().plusHours(1);
        viagem.finalizar(fimReal);

        assertThat(viagem.getStatus()).isEqualTo(Status.FINALIZADA);
        assertThat(viagem.getDataFim()).isEqualTo(fimReal);
    }

    @Test
    void naoDeveFinalizarViagemPlanejada() {
        Viagem viagem = novaViagem();

        assertThatThrownBy(() -> viagem.finalizar(LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class);
        assertThat(viagem.getStatus()).isEqualTo(Status.PLANEJADA);
    }

    @Test
    void deveCancelarViagemPlanejada() {
        Viagem viagem = novaViagem();

        viagem.cancelar();
        assertThat(viagem.getStatus()).isEqualTo(Status.CANCELADA);
    }

    @Test
    void naoDeveCancelarViagemIniciada() {
        Viagem viagem = novaViagem();
        viagem.iniciar(LocalDateTime.now(), List.of());

        assertThatThrownBy(viagem::cancelar)
                .isInstanceOf(IllegalStateException.class);
        assertThat(viagem.getStatus()).isEqualTo(Status.INICIADA);
    }

    @Test
    void deveRegistrarAtrasoAcumulado() {
        Viagem viagem = novaViagem();

        Duration atraso = Duration.ofMinutes(10);
        viagem.registrarAtrasoDetectado(atraso);
        assertThat(viagem.getAtrasoAcumulado()).isEqualTo(atraso);
        assertThat(viagem.isAtrasada()).isTrue();
    }
}

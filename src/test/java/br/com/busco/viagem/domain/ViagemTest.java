package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.events.*;
import br.com.busco.viagem.sk.ids.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ViagemTest {

    private ViagemPlanejadaId viagemPlanejadaId;
    private RotaId rotaId;
    private PeriodoViagem periodoViagem;
    private LocalDate dataViagem;

    @BeforeEach
    void setUp() {
        viagemPlanejadaId = ViagemPlanejadaId.randomId();
        rotaId = RotaId.randomId();
        periodoViagem = PeriodoViagem.of(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );
        dataViagem = LocalDate.now();
    }

    @Test
    void deveCriarViagemComDadosIniciais() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        assertThat(viagem.getId()).isNotNull();
        assertThat(viagem.getViagemPlanejada()).isEqualTo(viagemPlanejadaId);
        assertThat(viagem.getRota()).isEqualTo(rotaId);
        assertThat(viagem.getPeriodoViagem()).isEqualTo(periodoViagem);
        assertThat(viagem.getDataViagem()).isEqualTo(dataViagem);
        assertThat(viagem.getStatus()).isEqualTo(Status.PENDENTE);
        assertThat(viagem.getParadas()).isEmpty();
        assertThat(viagem.getAtrasoAcumulado()).isEqualTo(Duration.ZERO);
    }

    @Test
    void deveIniciarViagemComSucesso() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        List<ParadaPrevista> paradasPrevistas = List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(LocalDateTime.now()).quantidadeAlunos(5).build(),
                ParadaPrevista.builder().ordem(2).horarioPrevisto(LocalDateTime.now().plusMinutes(30)).quantidadeAlunos(3).build()
        );

        LocalDateTime horarioInicio = LocalDateTime.now();
        viagem.iniciar(horarioInicio, paradasPrevistas);

        assertThat(viagem.getStatus()).isEqualTo(Status.EM_ANDAMENTO);
        assertThat(viagem.getPeriodoViagem().getHorarioRealInicio()).isEqualTo(horarioInicio);
        assertThat(viagem.getParadas()).hasSize(2);
        assertThat(viagem.getParadas().get(0).getOrdem()).isEqualTo(1);
        assertThat(viagem.getParadas().get(1).getOrdem()).isEqualTo(2);
    }

    @Test
    void naoDeveIniciarViagemSeNaoEstiverPendente() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        viagem.iniciar(LocalDateTime.now(), List.of());

        assertThatThrownBy(() -> viagem.iniciar(LocalDateTime.now(), List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Viagem já foi iniciada");
    }

    @Test
    void naoDeveIniciarViagemAposDataAgendada() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(LocalDate.now().minusDays(1)) // Ontem
                .build();

        assertThatThrownBy(() -> viagem.iniciar(LocalDateTime.now(), List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Viagem não pode ser iniciada após a data");
    }

    @Test
    void deveRegistrarChegadaESaidaDeParada() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

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
    void deveRetornarStatusPendenteQuandoChegarNaUltimaParada() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        List<ParadaPrevista> paradasPrevistas = List.of(
                ParadaPrevista.builder().ordem(1).horarioPrevisto(LocalDateTime.now()).quantidadeAlunos(5).build(),
                ParadaPrevista.builder().ordem(2).horarioPrevisto(LocalDateTime.now().plusMinutes(30)).quantidadeAlunos(3).build()
        );

        viagem.iniciar(LocalDateTime.now(), paradasPrevistas);

        viagem.registrarChegadaParada(1, LocalDateTime.now());
        viagem.registrarSaidaParada(1, LocalDateTime.now());

        // Chegada na última parada (ordem 2)
        viagem.registrarChegadaParada(2, LocalDateTime.now());
        assertThat(viagem.getStatus()).isEqualTo(Status.PENDENTE);
    }

    @Test
    void devePausarERetomarViagem() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        viagem.iniciar(LocalDateTime.now(), List.of());
        assertThat(viagem.isEmAndamento()).isTrue();

        viagem.pausar();
        assertThat(viagem.getStatus()).isEqualTo(Status.PAUSADA);
        assertThat(viagem.isEmAndamento()).isFalse();

        viagem.retomar();
        assertThat(viagem.getStatus()).isEqualTo(Status.EM_ANDAMENTO);
        assertThat(viagem.isEmAndamento()).isTrue();
    }

    @Test
    void deveFinalizarViagem() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        viagem.iniciar(LocalDateTime.now(), List.of());
        LocalDateTime fimReal = LocalDateTime.now().plusHours(1);
        viagem.finalizar(fimReal);

        assertThat(viagem.getStatus()).isEqualTo(Status.CONCLUIDA);
        assertThat(viagem.getPeriodoViagem().getHorarioRealFim()).isEqualTo(fimReal);
    }

    @Test
    void deveCancelarViagem() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        viagem.cancelar();
        assertThat(viagem.getStatus()).isEqualTo(Status.CANCELADA);
    }

    @Test
    void deveRegistrarAtrasoAcumulado() {
        Viagem viagem = Viagem.builder()
                .viagemPlanejada(viagemPlanejadaId)
                .rota(rotaId)
                .periodoViagem(periodoViagem)
                .dataViagem(dataViagem)
                .build();

        Duration atraso = Duration.ofMinutes(10);
        viagem.registrarAtrasoDetectado(atraso);
        assertThat(viagem.getAtrasoAcumulado()).isEqualTo(atraso);
        assertThat(viagem.isAtrasada()).isTrue();
    }
}

package br.com.busco.viagem.planejamento.domain;

import br.com.busco.viagem.planejamento.domain.exceptions.NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado;
import br.com.busco.viagem.sk.ids.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanejamentoTest {

    @Test
    void deveCriarPlanejamentoComStatusEmAnalise() {
        Planejamento planejamento = novoPlanejamento();

        assertThat(planejamento.getId()).isNotNull();
        assertThat(planejamento.getStatus()).isEqualTo(Status.EM_ANALISE);
    }

    @Test
    void deveAprovarPlanejamento() {
        Planejamento planejamento = novoPlanejamento();

        planejamento.planejar();

        assertThat(planejamento.getStatus()).isEqualTo(Status.PLANEJADO);
    }

    @Test
    void deveIniciarPlanejamentoQuandoPlanejado() {
        Planejamento planejamento = novoPlanejamento();
        planejamento.planejar();

        planejamento.iniciar();

        assertThat(planejamento.getStatus()).isEqualTo(Status.INICIADO);
    }

    @Test
    void naoDeveIniciarPlanejamentoQuandoNaoPlanejado() {
        Planejamento planejamento = novoPlanejamento();

        assertThatThrownBy(planejamento::iniciar)
                .isInstanceOf(NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado.class);
    }

    @Test
    void deveFinalizarPlanejamentoQuandoIniciado() {
        Planejamento planejamento = novoPlanejamento();
        planejamento.planejar();
        planejamento.iniciar();

        planejamento.finalizar();

        assertThat(planejamento.getStatus()).isEqualTo(Status.FINALIZADO);
    }

    @Test
    void naoDeveFinalizarPlanejamentoQuandoNaoIniciado() {
        Planejamento planejamento = novoPlanejamento();

        assertThatThrownBy(planejamento::finalizar)
                .isInstanceOf(NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado.class);
    }

    @Test
    void deveCancelarPlanejamento() {
        Planejamento planejamento = novoPlanejamento();

        planejamento.cancelar();

        assertThat(planejamento.getStatus()).isEqualTo(Status.CANCELADO);
    }

    private static Planejamento novoPlanejamento() {
        Set<AlunoId> passageiros = new HashSet<>();
        passageiros.add(AlunoId.randomId());

        Set<DayOfWeek> diasDaSemana = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        Set<CalendarioId> calendarios = Set.of(CalendarioId.randomId());

        return Planejamento.builder()
                .codigo(br.com.busco.viagem.sk.vo.Codigo.of(1))
                .rota(null)
                .periodoPlanejado(PeriodoPlanejado.of(LocalDateTime.now(), LocalDateTime.now().plusDays(7)))
                .motorista(MotoristaId.randomId())
                .monitor(MonitorId.randomId())
                .veiculo(VeiculoId.randomId())
                .passageiros(passageiros)
                .checklistInicial(GrupoChecklistId.randomId())
                .checklistFinal(GrupoChecklistId.randomId())
                .diasDaSemana(diasDaSemana)
                .calendarios(calendarios)
                .build();
    }
}

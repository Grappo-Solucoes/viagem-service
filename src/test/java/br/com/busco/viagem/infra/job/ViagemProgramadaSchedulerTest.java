package br.com.busco.viagem.infra.job;

import br.com.busco.viagem.app.ViagemPlanejadaEvent;
import br.com.busco.viagem.app.ViagemService;
import br.com.busco.viagem.app.cmd.CriarViagem;
import br.com.busco.viagem.domain.Passageiro;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/** Task 4.2: reator cria viagens a partir do evento, sem JDBC alheio. */
@ExtendWith(MockitoExtension.class)
class ViagemProgramadaSchedulerTest {

    @Mock
    private ViagemService viagemService;

    @InjectMocks
    private ViagemProgramadaScheduler scheduler;

    @Test
    void deveCriarUmaViagemPorDataDoEvento() {
        var evento = ViagemPlanejadaEvent.builder()
                .planejamentoId(UUID.randomUUID())
                .datas(List.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)))
                .rotaId(UUID.randomUUID())
                .motoristaId(UUID.randomUUID())
                .monitorId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .passageiros(List.of(Passageiro.comId(UUID.randomUUID())))
                .build();

        scheduler.on(evento);

        var captor = ArgumentCaptor.forClass(br.com.busco.viagem.app.cmd.CriarViagem.class);
        verify(viagemService, times(2)).handle(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(c -> c.getPlanejamento())
                .containsOnly(evento.planejamentoId());
    }

    @Test
    void deveIgnorarEventoSemDatas() {
        var evento = ViagemPlanejadaEvent.builder()
                .planejamentoId(UUID.randomUUID())
                .datas(List.of())
                .build();

        scheduler.on(evento);

        verify(viagemService, never()).handle(any(CriarViagem.class));
    }
}

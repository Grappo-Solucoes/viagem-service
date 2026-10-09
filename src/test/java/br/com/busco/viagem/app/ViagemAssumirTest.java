package br.com.busco.viagem.app;

import br.com.busco.viagem.domain.PeriodoViagem;
import br.com.busco.viagem.domain.RotaRepository;
import br.com.busco.viagem.domain.Status;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.ViagemRepository;
import br.com.busco.viagem.infra.exceptions.runtime.ConflictException;
import br.com.busco.viagem.infra.exceptions.runtime.ForbiddenException;
import br.com.busco.viagem.infra.exceptions.runtime.ObjectNotFoundException;
import br.com.busco.viagem.sk.ids.RotaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Task 3.4: assumir (204/403/404/409) no nível de serviço.
 */
@ExtendWith(MockitoExtension.class)
class ViagemAssumirTest {

    @Mock
    private ViagemRepository repository;
    @Mock
    private RotaRepository rotaRepository;
    @Mock
    private RotaGateway rotaGateway;

    private ViagemService service;
    private Viagem viagem;
    private UUID monitorAtual;

    @BeforeEach
    void setUp() {
        service = new ViagemService(repository, rotaRepository, rotaGateway,
                new com.fasterxml.jackson.databind.ObjectMapper());
        monitorAtual = UUID.randomUUID();
        viagem = Viagem.builder()
                .data(LocalDateTime.now().plusDays(1))
                .rota(RotaId.randomId())
                .motoristaId(UUID.randomUUID())
                .monitorId(monitorAtual)
                .veiculoId(UUID.randomUUID())
                .periodoViagem(PeriodoViagem.of(
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1)))
                .build();
        lenient().when(repository.findById(any(ViagemId.class))).thenReturn(Optional.of(viagem));
        lenient().when(repository.save(any(Viagem.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(repository.findByMonitorIdAndStatusNot(any(), any())).thenReturn(List.of());
    }

    @Test
    void deveTrocarMonitorQuandoValido() {
        UUID novoMonitor = UUID.randomUUID();

        service.assumirViagem(UUID.fromString(viagem.getId().toUUID()), novoMonitor, true);

        assertThat(viagem.getMonitorId()).isEqualTo(novoMonitor);
        verify(repository).save(viagem);
    }

    @Test
    void deveRecusarQuemNaoEhMonitor() {
        assertThatThrownBy(() -> service.assumirViagem(
                UUID.fromString(viagem.getId().toUUID()), UUID.randomUUID(), false))
                .isInstanceOf(ForbiddenException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveRetornar404QuandoViagemNaoExiste() {
        when(repository.findById(any(ViagemId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assumirViagem(
                UUID.randomUUID(), UUID.randomUUID(), true))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void deveRecusarQuandoNaoEstaPlanejada() {
        viagem.iniciar(LocalDateTime.now(), List.of());

        assertThatThrownBy(() -> service.assumirViagem(
                UUID.fromString(viagem.getId().toUUID()), UUID.randomUUID(), true))
                .isInstanceOf(ConflictException.class);
        assertThat(viagem.getMonitorId()).isEqualTo(monitorAtual);
    }

    @Test
    void deveRecusarQuandoHorarioPassado() {
        Viagem passada = Viagem.builder()
                .data(LocalDateTime.now().minusHours(1))
                .rota(RotaId.randomId())
                .motoristaId(UUID.randomUUID())
                .monitorId(monitorAtual)
                .veiculoId(UUID.randomUUID())
                .build();
        when(repository.findById(any(ViagemId.class))).thenReturn(Optional.of(passada));

        assertThatThrownBy(() -> service.assumirViagem(
                UUID.fromString(passada.getId().toUUID()), UUID.randomUUID(), true))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("horário passado");
    }

    @Test
    void deveRecusarQuandoMonitorTemConflito() {
        Viagem outra = Viagem.builder()
                .data(viagem.getData())
                .rota(RotaId.randomId())
                .motoristaId(UUID.randomUUID())
                .monitorId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .build();
        when(repository.findByMonitorIdAndStatusNot(any(), any())).thenReturn(List.of(outra));

        assertThatThrownBy(() -> service.assumirViagem(
                UUID.fromString(viagem.getId().toUUID()), UUID.randomUUID(), true))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("neste horário");
        assertThat(viagem.getMonitorId()).isEqualTo(monitorAtual);
    }

    @Test
    void deveSerIdempotenteParaOMesmoMonitor() {
        service.assumirViagem(
                UUID.fromString(viagem.getId().toUUID()), monitorAtual, true);

        assertThat(viagem.getMonitorId()).isEqualTo(monitorAtual);
    }
}

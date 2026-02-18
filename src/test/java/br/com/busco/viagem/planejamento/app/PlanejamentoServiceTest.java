package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.planejamento.app.cmd.*;
import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.planejamento.domain.PlanejamentoRepository;
import br.com.busco.viagem.planejamento.domain.Status;
import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.gateway.RotaGateway;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.vo.Codigo;
import br.com.busco.viagem.sk.vo.Rota;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanejamentoServiceTest {

    private PlanejamentoService service;
    private InMemoryPlanejamentoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPlanejamentoRepository();
        AtomicInteger ultimoCodigo = new AtomicInteger(10);
        RotaGateway rotaGateway = id -> null;
        ObterUltimoCodigo obterUltimoCodigo = () -> Optional.of(ultimoCodigo.getAndIncrement());
        service = new PlanejamentoService(repository, rotaGateway, obterUltimoCodigo);
    }

    @Test
    void deveCriarPlanejamento() {
        CriarPlanejamento cmd = comandoCriarPlanejamento();

        PlanejamentoId id = service.handle(cmd);

        Planejamento planejamento = repository.findById(id).orElseThrow();
        assertThat(planejamento.getId()).isEqualTo(id);
        assertThat(planejamento.getStatus()).isEqualTo(Status.EM_ANALISE);
        assertThat(planejamento.getCodigo()).isEqualTo(Codigo.of(11));
        assertThat(planejamento.getPassageiros()).hasSize(2);
        assertThat(planejamento.getDiasDaSemana()).contains(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        assertThat(planejamento.getCalendarios()).hasSize(1);
    }

    @Test
    void deveAprovarPlanejamento() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());

        PlanejamentoId idAtualizado = service.handle(new AprovarPlanejamento(UUID.fromString(id.toUUID())));

        assertThat(idAtualizado).isEqualTo(id);
        assertThat(repository.findById(id).orElseThrow().getStatus()).isEqualTo(Status.PLANEJADO);
    }

    @Test
    void deveIniciarPlanejamento() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());
        service.handle(new AprovarPlanejamento(UUID.fromString(id.toUUID())));

        PlanejamentoId idAtualizado = service.handle(new IniciarPlanejamento(UUID.fromString(id.toUUID())));

        assertThat(idAtualizado).isEqualTo(id);
        assertThat(repository.findById(id).orElseThrow().getStatus()).isEqualTo(Status.INICIADO);
    }

    @Test
    void deveFinalizarPlanejamento() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());
        service.handle(new AprovarPlanejamento(UUID.fromString(id.toUUID())));
        service.handle(new IniciarPlanejamento(UUID.fromString(id.toUUID())));

        PlanejamentoId idAtualizado = service.handle(new FinalizarPlanejamento(UUID.fromString(id.toUUID())));

        assertThat(idAtualizado).isEqualTo(id);
        assertThat(repository.findById(id).orElseThrow().getStatus()).isEqualTo(Status.FINALIZADO);
    }

    @Test
    void deveCancelarPlanejamento() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());

        PlanejamentoId idAtualizado = service.handle(new CancelarPlanejamento(UUID.fromString(id.toUUID())));

        assertThat(idAtualizado).isEqualTo(id);
        assertThat(repository.findById(id).orElseThrow().getStatus()).isEqualTo(Status.CANCELADO);
    }

    @Test
    void deveBuscarPlanejamentoPorId() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());

        Planejamento encontrado = service.buscarPorId(id);

        assertThat(encontrado.getId()).isEqualTo(id);
    }

    @Test
    void naoDeveBuscarPlanejamentoInexistente() {
        PlanejamentoId idInexistente = PlanejamentoId.randomId();

        assertThatThrownBy(() -> service.buscarPorId(idInexistente))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found any Account with code");
    }

    @Test
    void deveDuplicarPlanejamento() {
        PlanejamentoId originalId = service.handle(comandoCriarPlanejamento());

        PlanejamentoId duplicadoId = service.handle(new DuplicarPlanejamento(UUID.fromString(originalId.toUUID())));

        Planejamento original = repository.findById(originalId).orElseThrow();
        Planejamento duplicado = repository.findById(duplicadoId).orElseThrow();
        assertThat(duplicadoId).isNotEqualTo(originalId);
        assertThat(duplicado.getCodigo()).isEqualTo(Codigo.of(12));
        assertThat(duplicado.getStatus()).isEqualTo(Status.EM_ANALISE);
        assertThat(duplicado.getPassageiros()).isEqualTo(original.getPassageiros());
        assertThat(duplicado.getDiasDaSemana()).isEqualTo(original.getDiasDaSemana());
        assertThat(duplicado.getCalendarios()).isEqualTo(original.getCalendarios());
    }

    @Test
    void deveEditarPlanejamento() {
        PlanejamentoId id = service.handle(comandoCriarPlanejamento());
        Planejamento original = repository.findById(id).orElseThrow();
        LocalDateTime novaData = original.getPeriodoPlanejado().getInicio().plusDays(1).plusHours(2);

        EditarPlanejamento cmd = EditarPlanejamento.builder()
                .id(UUID.fromString(id.toUUID()))
                .data(novaData)
                .motorista(UUID.randomUUID())
                .veiculo(UUID.randomUUID())
                .monitor(UUID.randomUUID())
                .alunos(List.of(UUID.randomUUID()))
                .calendario(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .grupoChecklistInicial(UUID.randomUUID())
                .grupoChecklistFinal(UUID.randomUUID())
                .build();

        PlanejamentoId atualizadoId = service.handle(cmd);

        Planejamento atualizado = repository.findById(atualizadoId).orElseThrow();
        assertThat(atualizadoId).isEqualTo(id);
        assertThat(atualizado.getPeriodoPlanejado().getInicio()).isEqualTo(novaData);
        assertThat(atualizado.getMotorista().toUUID()).isEqualTo(cmd.getMotorista().toString());
        assertThat(atualizado.getMonitor().toUUID()).isEqualTo(cmd.getMonitor().toString());
        assertThat(atualizado.getVeiculo().toUUID()).isEqualTo(cmd.getVeiculo().toString());
        assertThat(atualizado.getChecklistInicial().toUUID()).isEqualTo(cmd.getGrupoChecklistInicial().toString());
        assertThat(atualizado.getChecklistFinal().toUUID()).isEqualTo(cmd.getGrupoChecklistFinal().toString());
        assertThat(atualizado.getPassageiros()).hasSize(1);
        assertThat(atualizado.getCalendarios()).hasSize(2);
    }

    private static CriarPlanejamento comandoCriarPlanejamento() {
        return CriarPlanejamento.builder()
                .dataInicio(LocalDate.now())
                .horaViagem(LocalTime.of(7, 0))
                .dataFim(LocalDate.now().plusDays(30))
                .rota(UUID.randomUUID())
                .calendarios(List.of(UUID.randomUUID()))
                .diasSemana(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
                .motorista(UUID.randomUUID())
                .veiculo(UUID.randomUUID())
                .monitor(UUID.randomUUID())
                .alunos(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .grupoChecklistInicial(UUID.randomUUID())
                .grupoChecklistFinal(UUID.randomUUID())
                .build();
    }

    private static class InMemoryPlanejamentoRepository implements PlanejamentoRepository {
        private final Map<PlanejamentoId, Planejamento> storage = new HashMap<>();

        @Override
        public Planejamento save(Planejamento planejamento) {
            storage.put(planejamento.getId(), planejamento);
            return planejamento;
        }

        @Override
        public Optional<Planejamento> findById(PlanejamentoId id) {
            return Optional.ofNullable(storage.get(id));
        }
    }
}

package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.infra.codigo.GeradorCodigo;
import br.com.busco.viagem.planejamento.app.cmd.*;
import br.com.busco.viagem.planejamento.domain.PeriodoPlanejado;
import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.planejamento.domain.PlanejamentoRepository;
import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.gateway.RotaGateway;
import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Rota;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@AllArgsConstructor

@Log
@Service
@Validated
@Transactional(propagation = REQUIRES_NEW)
public class PlanejamentoService {

    private final PlanejamentoRepository repository;
    private final RotaGateway rotaGateway;
    private final ObterUltimoCodigo obterUltimoCodigo;

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid CriarPlanejamento cmd) {

        MotoristaId motorista = MotoristaId.fromString(cmd.getMotorista().toString());
        MonitorId monitor = MonitorId.fromString(cmd.getMonitor().toString());
        VeiculoId veiculo = VeiculoId.fromString(cmd.getVeiculo().toString());
        GrupoChecklistId checklistInicial = GrupoChecklistId.fromString(cmd.getGrupoChecklistInicial().toString());
        GrupoChecklistId checklistFinal = GrupoChecklistId.fromString(cmd.getGrupoChecklistFinal().toString());
        Rota rota = rotaGateway.buscarRotaPorId(cmd.getRota());
        LocalDateTime horaInicio = LocalDateTime.of(cmd.getDataInicio(), cmd.getHoraViagem());
        LocalDateTime horaFim = LocalDateTime.of(cmd.getDataFim(), cmd.getHoraViagem());
        PeriodoPlanejado periodoPlanejado = PeriodoPlanejado.of(horaInicio, horaFim);
        Set<DayOfWeek> diasDaSemana = new HashSet<>(cmd.getDiasSemana());
        Set<CalendarioId> calendarios = cmd.getCalendarios().stream().map(UUID::toString).map(CalendarioId::fromString).collect(Collectors.toSet());

        Set<AlunoId> alunos = new HashSet<>();

        if (cmd.getAlunos() != null && !cmd.getAlunos().isEmpty()) {
            alunos = cmd.getAlunos().stream().map(UUID::toString).map(AlunoId::fromString).collect(Collectors.toSet());
        }

        Planejamento planejamento = Planejamento.builder()
                .codigo(GeradorCodigo.by(obterUltimoCodigo).gerar())
                .rota(rota)
                .periodoPlanejado(periodoPlanejado)
                .motorista(motorista)
                .monitor(monitor)
                .veiculo(veiculo)
                .passageiros(alunos)
                .checklistInicial(checklistInicial)
                .checklistFinal(checklistFinal)
                .diasDaSemana(diasDaSemana)
                .calendarios(calendarios)
                .build();

        return repository.save(planejamento).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid AprovarPlanejamento cmd) {

        Planejamento planejamento = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));
        planejamento.planejar();

        return repository.save(planejamento).getId();

    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid CancelarPlanejamento cmd) {

        Planejamento planejamento = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));
        planejamento.cancelar();

        return repository.save(planejamento).getId();

    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid IniciarPlanejamento cmd) {
        Planejamento planejamento = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));
        planejamento.iniciar();

        return repository.save(planejamento).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid FinalizarPlanejamento cmd) {
        Planejamento planejamento = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));
        planejamento.finalizar();

        return repository.save(planejamento).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid EditarPlanejamento cmd) {
        Planejamento planejamento = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));

        Rota rota = cmd.getRota() != null ? rotaGateway.buscarRotaPorId(cmd.getRota()) : null;
        MotoristaId motorista = cmd.getMotorista() != null
                ? MotoristaId.fromString(cmd.getMotorista().toString())
                : MotoristaId.VAZIO;
        MonitorId monitor = cmd.getMonitor() != null
                ? MonitorId.fromString(cmd.getMonitor().toString())
                : MonitorId.VAZIO;
        VeiculoId veiculo = cmd.getVeiculo() != null
                ? VeiculoId.fromString(cmd.getVeiculo().toString())
                : VeiculoId.VAZIO;
        GrupoChecklistId checklistInicial = cmd.getGrupoChecklistInicial() != null
                ? GrupoChecklistId.fromString(cmd.getGrupoChecklistInicial().toString())
                : GrupoChecklistId.VAZIO;
        GrupoChecklistId checklistFinal = cmd.getGrupoChecklistFinal() != null
                ? GrupoChecklistId.fromString(cmd.getGrupoChecklistFinal().toString())
                : GrupoChecklistId.VAZIO;
        Set<AlunoId> alunos = cmd.getAlunos() != null
                ? cmd.getAlunos().stream().map(UUID::toString).map(AlunoId::fromString).collect(Collectors.toSet())
                : new HashSet<>();
        Set<CalendarioId> calendarios = cmd.getCalendario() != null
                ? cmd.getCalendario().stream().map(UUID::toString).map(CalendarioId::fromString).collect(Collectors.toSet())
                : new HashSet<>();

        planejamento.updateForm()
                .partida(cmd.getPartida())
                .chegada(cmd.getChegada())
                .rota(rota)
                .motorista(motorista)
                .monitor(monitor)
                .veiculo(veiculo)
                .passageiros(alunos)
                .checklistInicial(checklistInicial)
                .checklistFinal(checklistFinal)
                .calendarios(calendarios)
                .aplicar();

        return repository.save(planejamento).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public PlanejamentoId handle(@NonNull @Valid DuplicarPlanejamento cmd) {
        Planejamento origem = buscarPorId(PlanejamentoId.fromString(cmd.getId().toString()));

        Planejamento novo = Planejamento.builder()
                .codigo(GeradorCodigo.by(obterUltimoCodigo).gerar())
                .rota(origem.getRota())
                .periodoPlanejado(origem.getPeriodoPlanejado())
                .motorista(origem.getMotorista())
                .monitor(origem.getMonitor())
                .veiculo(origem.getVeiculo())
                .passageiros(new HashSet<>(origem.getPassageiros()))
                .checklistInicial(origem.getChecklistInicial())
                .checklistFinal(origem.getChecklistFinal())
                .diasDaSemana(new HashSet<>(origem.getDiasDaSemana()))
                .calendarios(new HashSet<>(origem.getCalendarios()))
                .build();

        return repository.save(novo).getId();
    }

    @NonNull
    @Transactional(readOnly = true)
    public Planejamento buscarPorId(@NonNull PlanejamentoId id) {
        return repository.findById(requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code %s.", id.toUUID())));
    }
}

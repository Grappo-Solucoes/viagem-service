package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.infra.codigo.GeradorCodigo;
import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.gateway.RotaGateway;
import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.viagem.app.cmd.*;
import br.com.busco.viagem.viagem.domain.PeriodoPlanejado;
import br.com.busco.viagem.sk.vo.Rota;
import br.com.busco.viagem.viagem.domain.Viagem;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import br.com.busco.viagem.viagem.domain.exceptions.MotoristaComViagemAgendada;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
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
public class ViagemService {

    private final ViagemRepository repository;
    private final RotaGateway rotaGateway;
    private final DisponibilidadeMotoristaPolicy disponibilidadeMotoristaPolicy;
    private final ObterUltimoCodigo obterUltimoCodigo;

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid CriarViagem cmd) {
        MotoristaId motorista = MotoristaId.fromString(cmd.getMotorista().toString());
        MonitorId monitor = MonitorId.fromString(cmd.getMonitor().toString());
        VeiculoId veiculo = VeiculoId.fromString(cmd.getVeiculo().toString());
        GrupoChecklistId checklistInicial = GrupoChecklistId.fromString(cmd.getGrupoChecklistInicial().toString());
        GrupoChecklistId checklistFinal = GrupoChecklistId.fromString(cmd.getGrupoChecklistFinal().toString());
        PlanejamentoId planeamento = PlanejamentoId.fromString(cmd.getPlanejamento().toString());
        Rota rota = rotaGateway.buscarRotaPorId(cmd.getRota());
        LocalDateTime horaSaida = cmd.getData();
        LocalDateTime horaChegada = horaSaida.plus(rota.getTempoEstimado());
        PeriodoPlanejado periodoPlanejado = PeriodoPlanejado.of(horaSaida, horaChegada);
        Set<AlunoId> alunos = new HashSet<>();

        if (cmd.getAlunos() != null && !cmd.getAlunos().isEmpty()) {
            alunos = cmd.getAlunos().stream().map(UUID::toString).map(AlunoId::fromString).collect(Collectors.toSet());
        }

        if (!disponibilidadeMotoristaPolicy.isDisponivel(motorista)) {
            throw new MotoristaComViagemAgendada(periodoPlanejado.getPartida());
        }

        Viagem viagem = Viagem.builder()
                .motorista(motorista)
                .monitor(monitor)
                .rota(rota)
                .veiculo(veiculo)
                .checklistInicial(checklistInicial)
                .checklistFinal(checklistFinal)
                .periodoPlanejado(periodoPlanejado)
                .planejamento(planeamento)
                .codigo(GeradorCodigo.by(obterUltimoCodigo).gerar())
                .build();

        viagem.adicionarPassageiros(alunos);

        return repository.save(viagem).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid AtualizarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        MotoristaId motorista = MotoristaId.fromString(cmd.getMotorista().toString());
        MonitorId monitor = MonitorId.fromString(cmd.getMonitor().toString());
        VeiculoId veiculo = VeiculoId.fromString(cmd.getVeiculo().toString());
        GrupoChecklistId checklistInicial = GrupoChecklistId.fromString(cmd.getGrupoChecklistInicial().toString());
        GrupoChecklistId checklistFinal = GrupoChecklistId.fromString(cmd.getGrupoChecklistFinal().toString());

        Rota rota = rotaGateway.buscarRotaPorId(cmd.getRota());
        LocalDateTime horaSaida = cmd.getData();
        LocalDateTime horaChegada = horaSaida.plus(rota.getTempoEstimado());
        PeriodoPlanejado periodoPlanejado = PeriodoPlanejado.of(horaSaida, horaChegada);
        Set<AlunoId> alunos = new HashSet<>();

        if (cmd.getAlunos() != null && !cmd.getAlunos().isEmpty()) {
            alunos = cmd.getAlunos().stream().map(UUID::toString).map(AlunoId::fromString).collect(Collectors.toSet());
        }

        if (!disponibilidadeMotoristaPolicy.isDisponivel(motorista)) {
            throw new MotoristaComViagemAgendada(periodoPlanejado.getPartida());
        }

        viagem.update()
                .motorista(motorista)
                .monitor(monitor)
                .rota(rota)
                .veiculo(veiculo)
                .checklistInicial(checklistInicial)
                .checklistFinal(checklistFinal)
                .periodoPlanejado(periodoPlanejado)
                .passageiros(alunos)
                .aplicar();

        return repository.save(viagem).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid CancelarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        viagem.cancelar();
        return repository.save(viagem).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid IniciarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        viagem.iniciar();
        return repository.save(viagem).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid FinalizarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        viagem.finalizar();
        return repository.save(viagem).getId();
    }

    @NonNull
    @Transactional(readOnly = true)
    public Viagem buscarPorId(@NonNull ViagemId id) {
        return repository.findById(requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code %s.", id.toUUID())));
    }
}

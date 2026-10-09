package br.com.busco.viagem.app;

import br.com.busco.viagem.app.cmd.AtualizarRota;
import br.com.busco.viagem.app.cmd.AtualizarViagem;
import br.com.busco.viagem.app.cmd.CancelarViagem;
import br.com.busco.viagem.app.cmd.CriarRota;
import br.com.busco.viagem.app.cmd.CriarViagem;
import br.com.busco.viagem.app.cmd.DesembarcarAluno;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.EmbarcarTodos;
import br.com.busco.viagem.app.cmd.FinalizarViagem;
import br.com.busco.viagem.app.cmd.IniciarViagem;
import br.com.busco.viagem.app.cmd.RegistrarChegadaParada;
import br.com.busco.viagem.app.cmd.RegistrarSaidaParada;
import br.com.busco.viagem.domain.Carteirinha;
import br.com.busco.viagem.domain.Endereco;
import br.com.busco.viagem.domain.ParadaPrevista;
import br.com.busco.viagem.domain.Passageiro;
import br.com.busco.viagem.domain.PeriodoViagem;
import br.com.busco.viagem.domain.Rota;
import br.com.busco.viagem.domain.RotaRepository;
import br.com.busco.viagem.domain.Status;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.ViagemRepository;
import br.com.busco.viagem.infra.exceptions.runtime.ConflictException;
import br.com.busco.viagem.infra.exceptions.runtime.ForbiddenException;
import br.com.busco.viagem.infra.exceptions.runtime.ObjectNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.busco.viagem.sk.ids.RotaId;
import br.com.busco.viagem.sk.ids.ViagemId;
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
import java.util.List;
import java.util.UUID;

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
    private final RotaRepository rotaRepository;
    private final ObjectMapper objectMapper;

    @NonNull
    public ViagemId handle(@NonNull @Valid CriarViagem cmd) {
        RotaId rotaId = RotaId.fromString(cmd.getRota().toString());
        rotaRepository.findById(rotaId).orElseThrow(() -> new EntityNotFoundException(
                format("Rota não encontrada com o ID: %s", cmd.getRota())));

        List<Passageiro> passageiros = cmd.getPassageiros() != null && !cmd.getPassageiros().isEmpty()
                ? cmd.getPassageiros()
                : cmd.getAlunos() == null ? List.of()
                : cmd.getAlunos().stream().map(Passageiro::comId).toList();

        Viagem viagem = Viagem.builder()
                .data(cmd.getData())
                .rota(rotaId)
                .motoristaId(cmd.getMotorista())
                .monitorId(cmd.getMonitor())
                .veiculoId(cmd.getVeiculo())
                .planejamentoId(cmd.getPlanejamento())
                .passageiros(passageiros)
                .periodoViagem(PeriodoViagem.of(cmd.getData(), cmd.getData()))
                .build();

        return repository.save(viagem).getId();
    }

    @NonNull
    public ViagemId handle(@NonNull @Valid CancelarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        viagem.cancelar();
        return repository.save(viagem).getId();
    }

    @NonNull
    public ViagemId handle(@NonNull @Valid IniciarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        LocalDateTime horarioPartida = cmd.getHorarioPartida() != null
                ? cmd.getHorarioPartida() : LocalDateTime.now();

        // Agregado local (D2): paradas derivadas da rota própria, sem gateway externo.
        Rota rota = rotaRepository.findById(viagem.getRota()).orElseThrow(
                () -> new EntityNotFoundException(format(
                        "Rota não encontrada com o ID: %s", viagem.getRota().toUUID())));
        List<ParadaPrevista> paradasPrevistas = paradasDaRota(viagem, rota);

        viagem.iniciar(horarioPartida, paradasPrevistas);
        return repository.save(viagem).getId();
    }

    private List<ParadaPrevista> paradasDaRota(Viagem viagem, Rota rota) {
        List<Endereco> enderecos = rota.getEnderecos() == null ? List.of() : rota.getEnderecos();
        List<ParadaPrevista> previstas = new java.util.ArrayList<>();
        for (int i = 0; i < enderecos.size(); i++) {
            previstas.add(ParadaPrevista.builder()
                    .ordem(i + 1)
                    .horarioPrevisto(viagem.getData())
                    .quantidadeAlunos(viagem.getPassageiros().size())
                    .build());
        }
        return previstas;
    }

    @NonNull
    public ViagemId handle(RegistrarChegadaParada cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        viagem.registrarChegadaParada(
                cmd.getOrdemParada(),
                cmd.getHorarioChegada(),
                cmd.getLatitude(),
                cmd.getLongitude()
        );

        return repository.save(viagem).getId();
    }

    @NonNull
    public ViagemId handle(RegistrarSaidaParada cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        viagem.registrarSaidaParada(
                cmd.getOrdemParada(),
                cmd.getHorarioSaida(),
                cmd.getLatitude(),
                cmd.getLongitude()
        );

        return repository.save(viagem).getId();
    }

    @NonNull
    public ViagemId handle(@NonNull @Valid FinalizarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));
        LocalDateTime horario = cmd.getHorarioFinalizacao() != null
                ? cmd.getHorarioFinalizacao() : LocalDateTime.now();
        viagem.finalizar(horario);
        return repository.save(viagem).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public EmbarcarAlunoResultado handle(@NonNull @Valid EmbarcarAluno cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        Passageiro passageiro = encontrarPassageiro(viagem, cmd.getAluno());
        viagem.embarcarPassageiro(passageiro, cmd.getLatitude(), cmd.getLongitude(),
                cmd.getJustificativa());

        UUID viagemId = UUID.fromString(repository.save(viagem).getId().toUUID());
        return new EmbarcarAlunoResultado(viagemId, passageiro.getPassageiroId(), passageiro.getNome());
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public EmbarcarTodosResultado handle(@NonNull @Valid EmbarcarTodos cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        var embarcados = viagem.embarcarTodosPendentes();
        UUID viagemId = UUID.fromString(repository.save(viagem).getId().toUUID());

        return new EmbarcarTodosResultado(viagemId, embarcados.stream()
                .map(p -> new EmbarcarAlunoResultado(viagemId, p.getPassageiroId(), p.getNome()))
                .toList());
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public EmbarcarAlunoResultado handle(@NonNull @Valid DesembarcarAluno cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        Passageiro passageiro = encontrarPassageiro(viagem, cmd.getAluno());
        viagem.desembarcarPassageiro(passageiro.getPassageiroId(),
                cmd.getLatitude(), cmd.getLongitude());

        UUID viagemId = UUID.fromString(repository.save(viagem).getId().toUUID());
        return new EmbarcarAlunoResultado(viagemId, passageiro.getPassageiroId(), passageiro.getNome());
    }

    /**
     * Monitor autenticado assume a viagem planejada. Erros espelham o monólito:
     * 403 se não é monitor, 404 se a viagem não existe, 409 se indisponível
     * ou em conflito de horário.
     */
    @NonNull
    public ViagemId assumirViagem(@NonNull UUID viagemId, UUID monitorId, boolean ehMonitor) {
        if (!ehMonitor || monitorId == null) {
            throw new ForbiddenException("Apenas monitores podem assumir viagens");
        }
        Viagem viagem = repository.findById(ViagemId.fromString(viagemId.toString()))
                .orElseThrow(() -> new ObjectNotFoundException("Viagem não encontrada"));

        if (viagem.getStatus() != Status.PLANEJADA) {
            throw new ConflictException("A viagem não está mais disponível para ser assumida");
        }
        if (viagem.getData().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Não é possível assumir uma viagem com horário passado");
        }
        if (monitorId.equals(viagem.getMonitorId())) {
            return viagem.getId();
        }

        boolean emConflito = repository.findByMonitorIdAndStatusNot(
                        monitorId, Status.CANCELADA).stream()
                .filter(outra -> !outra.getId().equals(viagem.getId()))
                .anyMatch(viagem::isDataOcupada);
        if (emConflito) {
            throw new ConflictException("O monitor já possui uma viagem neste horário");
        }

        try {
            viagem.assumir(monitorId);
        } catch (IllegalStateException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        return repository.save(viagem).getId();
    }

    private Passageiro encontrarPassageiro(Viagem viagem, String codigo) {        if (codigo == null || codigo.isEmpty()) {
            throw new ObjectNotFoundException("Carteirinha do aluno não informada.");
        }

        if (codigo.contains(";")) {
            Carteirinha carteirinha = Carteirinha.of(codigo);
            return viagem.encontrarPassageiroPorCarteirinha(carteirinha)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            format("Aluno '%s' não encontrado.", carteirinha.nome())));
        }

        try {
            UUID passageiroId = UUID.fromString(codigo);
            return viagem.encontrarPassageiroPorId(passageiroId)
                    .orElseGet(() -> Passageiro.comId(passageiroId));
        } catch (IllegalArgumentException e) {
            Carteirinha carteirinha = Carteirinha.of(codigo);
            return viagem.encontrarPassageiroPorCarteirinha(carteirinha)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            format("Aluno '%s' não encontrado.", codigo)));
        }
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public ViagemId handle(@NonNull @Valid AtualizarViagem cmd) {
        Viagem viagem = buscarPorId(ViagemId.fromString(cmd.getId().toString()));

        RotaId novaRota = cmd.getRota() == null ? null : RotaId.fromString(cmd.getRota().toString());
        if (novaRota != null) {
            rotaRepository.findById(novaRota).orElseThrow(() -> new EntityNotFoundException(
                    format("Rota não encontrada com o ID: %s", cmd.getRota())));
        }
        List<Passageiro> novosPassageiros = cmd.getAlunos() == null ? null
                : cmd.getAlunos().stream().map(Passageiro::comId).toList();

        UUID motorista = cmd.getMotorista() != null ? cmd.getMotorista() : viagem.getMotoristaId();
        UUID monitor = cmd.getMonitor() != null ? cmd.getMonitor() : viagem.getMonitorId();
        UUID veiculo = cmd.getVeiculo() != null ? cmd.getVeiculo() : viagem.getVeiculoId();
        LocalDateTime data = cmd.getData() != null ? cmd.getData() : viagem.getData();
        validarOcupacao(viagem.getId(), data, motorista, monitor, veiculo);

        viagem.atualizar(cmd.getData(), novaRota, cmd.getMotorista(), cmd.getVeiculo(),
                cmd.getMonitor(), novosPassageiros);
        return repository.save(viagem).getId();
    }

    private void validarOcupacao(ViagemId viagemId, LocalDateTime data, UUID motorista,
                                 UUID monitor, UUID veiculo) {
        for (Viagem outra : repository.findByData(data)) {
            if (outra.getId().equals(viagemId) || outra.getStatus() == Status.CANCELADA) {
                continue;
            }
            if (motorista != null && motorista.equals(outra.getMotoristaId())) {
                throw new ConflictException("Motorista ocupado nessa data");
            }
            if (monitor != null && monitor.equals(outra.getMonitorId())) {
                throw new ConflictException("Monitor ocupado nessa data");
            }
            if (veiculo != null && veiculo.equals(outra.getVeiculoId())) {
                throw new ConflictException("Veículo ocupado nessa data");
            }
        }
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public RotaId handle(@NonNull @Valid CriarRota cmd) throws Exception {
        Rota rota = Rota.builder()
                .numeroRota(cmd.getNumeroRota())
                .nome(cmd.getNome())
                .enderecos(cmd.getEnderecos() != null ? cmd.getEnderecos() : List.of())
                .tempoEstimado(cmd.getTempoEstimado())
                .distancia(cmd.getDistancia())
                .rotasJson(cmd.getRotas() != null ? objectMapper.writeValueAsString(cmd.getRotas()) : null)
                .build();
        return rotaRepository.save(rota).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public RotaId handle(@NonNull @Valid AtualizarRota cmd) {
        Rota rota = rotaRepository.findById(RotaId.fromString(cmd.getId().toString()))
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Not found any Business with code %s.", cmd.getId())));
        List<Endereco> enderecos = cmd.getEnderecos() != null
                ? new java.util.ArrayList<>(cmd.getEnderecos()) : null;
        rota.atualizar(cmd.getNumeroRota(), cmd.getNome(), enderecos,
                cmd.getTempoEstimado(), cmd.getDistancia(), null);
        return rotaRepository.save(rota).getId();
    }

    @NonNull
    @Transactional(readOnly = true)
    public Rota buscarRotaPorId(@NonNull RotaId id) {
        return rotaRepository.findById(requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Business with code %s.", id.toUUID())));
    }

    @NonNull
    @Transactional(readOnly = true)
    public Viagem buscarPorId(@NonNull ViagemId id) {
        return repository.findById(requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code %s.", id.toUUID())));
    }
}

package br.com.busco.viagem.app;

import br.com.busco.viagem.app.cmd.CancelarViagem;
import br.com.busco.viagem.app.cmd.CriarViagem;
import br.com.busco.viagem.app.cmd.FinalizarViagem;
import br.com.busco.viagem.app.cmd.IniciarViagem;
import br.com.busco.viagem.app.cmd.RegistrarChegadaParada;
import br.com.busco.viagem.app.cmd.RegistrarSaidaParada;
import br.com.busco.viagem.domain.Passageiro;
import br.com.busco.viagem.domain.PeriodoViagem;
import br.com.busco.viagem.domain.RotaRepository;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.ViagemRepository;
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

import java.util.List;

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
    private final RotaGateway rotaGateway;

    @NonNull
    public ViagemId handle(@NonNull @Valid CriarViagem cmd) {
        RotaId rotaId = RotaId.fromString(cmd.getRota().toString());
        rotaRepository.findById(rotaId).orElseThrow(() -> new EntityNotFoundException(
                format("Rota não encontrada com o ID: %s", cmd.getRota())));

        List<Passageiro> passageiros = cmd.getAlunos() == null ? List.of()
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

        var paradasPrevistas = rotaGateway.calcularParadasComHorarios(
                viagem.getRota(),
                cmd.getHorarioPartida().toLocalTime()
        );

        viagem.iniciar(cmd.getHorarioPartida(), paradasPrevistas);
        return repository.save(viagem).getId();
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
        viagem.finalizar(cmd.getHorarioFinalizacao());
        return repository.save(viagem).getId();
    }

    @NonNull
    @Transactional(readOnly = true)
    public Viagem buscarPorId(@NonNull ViagemId id) {
        return repository.findById(requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException(format("Not found any Account with code %s.", id.toUUID())));
    }
}

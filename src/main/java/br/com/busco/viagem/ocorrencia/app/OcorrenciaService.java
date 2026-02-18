package br.com.busco.viagem.ocorrencia.app;

import br.com.busco.viagem.ocorrencia.app.cmd.AlterarTipoDeOcorrencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarEmergencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarOcorrencia;
import br.com.busco.viagem.ocorrencia.app.cmd.CriarTipoDeOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.Emergencia;
import br.com.busco.viagem.ocorrencia.domain.EmergenciaRepository;
import br.com.busco.viagem.ocorrencia.domain.Ocorrencia;
import br.com.busco.viagem.ocorrencia.domain.OcorrenciaRepository;
import br.com.busco.viagem.ocorrencia.domain.TipoOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.TipoOcorrenciaRepository;
import br.com.busco.viagem.sk.ids.EmergenciaId;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Log
@Service
@AllArgsConstructor
@Validated
@Transactional(propagation = REQUIRES_NEW)
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final EmergenciaRepository emergenciaRepository;
    private final TipoOcorrenciaRepository tipoOcorrenciaRepository;
    private final BuscarViagemGateway buscarViagemGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public TipoOcorrenciaId handle(@NonNull @Valid CriarTipoDeOcorrencia cmd) {
        TipoOcorrencia tipoOcorrencia = TipoOcorrencia.of(cmd.getTipoOcorrencia());
        return tipoOcorrenciaRepository.save(tipoOcorrencia).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public TipoOcorrenciaId handle(@NonNull @Valid AlterarTipoDeOcorrencia cmd) {
        TipoOcorrenciaId id = TipoOcorrenciaId.fromString(cmd.getId().toString());
        TipoOcorrencia tipoOcorrencia = tipoOcorrenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de ocorrencia nao encontrado."));

        tipoOcorrencia.alterarTipoOcorrencia(cmd.getTipoOcorrencia());

        return tipoOcorrenciaRepository.save(tipoOcorrencia).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public OcorrenciaId handle(@NonNull @Valid CriarOcorrencia cmd) {
        ViagemId viagemId = ViagemId.fromString(cmd.getViagem().toString());
        if (!buscarViagemGateway.existsById(viagemId)) {
            throw new EntityNotFoundException("Viagem nao encontrada.");
        }

        TipoOcorrenciaId tipoId = TipoOcorrenciaId.fromString(cmd.getTipoOcorrencia().toString());
        tipoOcorrenciaRepository.findById(tipoId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de ocorrencia nao encontrado."));

        Ocorrencia ocorrencia = Ocorrencia.builder()
                .viagem(viagemId)
                .tipoOcorrencia(tipoId)
                .motivo(cmd.getMotivo())
                .userId(usuarioAutenticadoGateway.getUserId())
                .setorResponsavel(cmd.getSetorResponsavel())
                .responsavelTratativas(cmd.getResponsavelTratativas())
                .build();

        return repository.save(ocorrencia).getId();
    }

    @NonNull
    @Lock(PESSIMISTIC_READ)
    public EmergenciaId handle(@NonNull @Valid CriarEmergencia cmd) {
        ViagemId viagemId = ViagemId.fromString(cmd.getViagem().toString());
        if (!buscarViagemGateway.existsById(viagemId)) {
            throw new EntityNotFoundException("Viagem nao encontrada.");
        }

        Emergencia emergencia = Emergencia.builder()
                .viagem(viagemId)
                .motivo(cmd.getMotivo())
                .tipoEmergencia(cmd.getTipoEmergencia())
                .userId(usuarioAutenticadoGateway.getUserId())
                .build();

        return emergenciaRepository.save(emergencia).getId();
    }

    @Lock(PESSIMISTIC_READ)
    public void iniciarAnalise(@NonNull OcorrenciaId id) {
        Ocorrencia ocorrencia = buscarPorId(id);
        ocorrencia.iniciarAnalise();
        repository.save(ocorrencia);
    }

    @Lock(PESSIMISTIC_READ)
    public void iniciarTratativas(@NonNull OcorrenciaId id) {
        Ocorrencia ocorrencia = buscarPorId(id);
        ocorrencia.iniciarTratativas();
        repository.save(ocorrencia);
    }

    @Lock(PESSIMISTIC_READ)
    public void finalizar(@NonNull OcorrenciaId id) {
        Ocorrencia ocorrencia = buscarPorId(id);
        ocorrencia.finalizar();
        repository.save(ocorrencia);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Ocorrencia buscarPorId(@NonNull OcorrenciaId id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ocorrencia nao encontrada."));
    }
}

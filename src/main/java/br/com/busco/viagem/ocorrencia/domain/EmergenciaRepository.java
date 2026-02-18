package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.sk.ids.EmergenciaId;

import java.util.Optional;

public interface EmergenciaRepository {

    Emergencia save(Emergencia emergencia);

    Optional<Emergencia> findById(EmergenciaId id);
}

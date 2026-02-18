package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.sk.ids.OcorrenciaId;

import java.util.Optional;

public interface OcorrenciaRepository {

    Ocorrencia save(Ocorrencia ocorrencia);

    Optional<Ocorrencia> findById(OcorrenciaId id);
}

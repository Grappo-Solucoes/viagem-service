package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;

import java.util.Optional;

public interface TipoOcorrenciaRepository {

    TipoOcorrencia save(TipoOcorrencia tipoOcorrencia);

    Optional<TipoOcorrencia> findById(TipoOcorrenciaId id);
}

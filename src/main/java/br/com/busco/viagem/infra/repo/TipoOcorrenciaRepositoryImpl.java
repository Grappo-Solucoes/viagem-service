package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.ocorrencia.domain.TipoOcorrencia;
import br.com.busco.viagem.ocorrencia.domain.TipoOcorrenciaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoOcorrenciaRepositoryImpl extends TipoOcorrenciaRepository, JpaRepository<TipoOcorrencia, TipoOcorrenciaId> {
}

package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.ocorrencia.domain.Ocorrencia;
import br.com.busco.viagem.ocorrencia.domain.OcorrenciaRepository;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepositoryImpl extends OcorrenciaRepository, JpaRepository<Ocorrencia, OcorrenciaId> {
}

package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.ocorrencia.domain.Emergencia;
import br.com.busco.viagem.ocorrencia.domain.EmergenciaRepository;
import br.com.busco.viagem.sk.ids.EmergenciaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergenciaRepositoryImpl extends EmergenciaRepository, JpaRepository<Emergencia, EmergenciaId> {
}

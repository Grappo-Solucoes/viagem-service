package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.domain.Rota;
import br.com.busco.viagem.domain.RotaRepository;
import br.com.busco.viagem.sk.ids.RotaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RotaRepositoryImpl extends RotaRepository, JpaRepository<Rota, RotaId> {
}

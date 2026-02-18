package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.planejamento.domain.PlanejamentoRepository;
import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanejamentoRepositoryImpl extends PlanejamentoRepository, JpaRepository<Planejamento, PlanejamentoId>, ObterUltimoCodigo {

    @Override
    @Query(value = "SELECT MAX(t.codigo.codigo) FROM Planejamento t")
    Optional<Integer> obterUltimoCodigo();
}

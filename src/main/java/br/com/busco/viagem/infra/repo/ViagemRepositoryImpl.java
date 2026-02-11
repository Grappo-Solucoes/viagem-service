package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.sk.ObterUltimoCodigo;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.Viagem;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ViagemRepositoryImpl extends ViagemRepository, JpaRepository<Viagem, ViagemId>, ObterUltimoCodigo {
    @Query(value = "SELECT MAX(t.codigo.codigo) FROM Viagem t")
    Optional<Integer> obterUltimoCodigo();

}

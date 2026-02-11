package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ViagemRepository {
    Viagem save(Viagem viagem);
    Optional<Viagem> findById(ViagemId id);

    @Query("SELECT COUNT(v) FROM Viagem v WHERE v.planejamento = :planejamento")
    Integer countViagemPlanejamento(@Param("planejamento") PlanejamentoId planejamento);

    @Query("SELECT CASE WHEN COUNT(v) = 0 THEN true ELSE false END " +
            "FROM Viagem v " +
            "WHERE v.planejamento = :planejamento " +
            "AND v.id != :viagemAtual " +
            "AND v.status NOT IN ('FINALIZADA', 'CANCELADA')")
    Boolean isUltimaViagemDoPlanejamento(
            @Param("viagemAtual") ViagemId viagemAtual,
            @Param("planejamento") PlanejamentoId planejamento);
}

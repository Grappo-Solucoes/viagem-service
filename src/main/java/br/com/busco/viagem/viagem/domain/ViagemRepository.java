package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.sk.ids.MotoristaId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
            "AND v.status IN ('INICIADA', 'FINALIZADA')")
    Boolean isPrimeiraViagemDoPlanejamento(
            @Param("viagemAtual") ViagemId viagemAtual,
            @Param("planejamento") PlanejamentoId planejamento);

    @Query("SELECT CASE WHEN COUNT(vAtual) = 1 AND COUNT(v) = 0 THEN true ELSE false END " +
            "FROM Viagem vAtual " +
            "LEFT JOIN Viagem v ON v.planejamento = vAtual.planejamento " +
            "AND v.id != vAtual.id " +
            "AND v.status IN ('INICIADA', 'FINALIZADA') " +
            "WHERE vAtual.id = :viagemAtual")
    Boolean isPrimeiraViagemDoPlanejamento(@Param("viagemAtual") ViagemId viagemAtual);

    @Query("SELECT CASE WHEN COUNT(v) = 0 THEN true ELSE false END " +
            "FROM Viagem v " +
            "WHERE v.planejamento = :planejamento " +
            "AND v.id != :viagemAtual " +
            "AND v.status NOT IN ('FINALIZADA', 'CANCELADA')")
    Boolean isUltimaViagemDoPlanejamento(
            @Param("viagemAtual") ViagemId viagemAtual,
            @Param("planejamento") PlanejamentoId planejamento);

    @Query("SELECT CASE WHEN COUNT(v) = 0 THEN true ELSE false END " +
            "FROM Viagem v " +
            "WHERE v.motorista = :motorista " +
            "AND v.status IN ('PENDENTE', 'INICIADA') " +
            "AND v.periodoPlanejado.partida < :fim " +
            "AND v.periodoPlanejado.chegada > :inicio")
    Boolean isMotoristaDisponivel(@Param("motorista") MotoristaId motorista,
                                  @Param("inicio") LocalDateTime inicio,
                                  @Param("fim") LocalDateTime fim);


    @Query("SELECT v.id " +
            "FROM Viagem v " +
            "WHERE v.planejamento = :planejamento " +
            "AND v.status = 'PENDENTE'")
    List<ViagemId> buscarViagensNaoIniciadasPlanejamento(@Param("planejamento") PlanejamentoId planejamento);
}

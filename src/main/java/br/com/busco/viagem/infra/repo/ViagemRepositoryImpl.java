package br.com.busco.viagem.infra.repo;

import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.ViagemRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ViagemRepositoryImpl extends ViagemRepository, JpaRepository<Viagem, ViagemId> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Viagem v where v.id = :id")
    Optional<Viagem> findByIdForUpdate(@Param("id") ViagemId id);
}

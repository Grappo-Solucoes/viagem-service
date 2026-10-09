package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.ViagemId;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViagemRepository {
    Viagem save(Viagem viagem);
    Optional<Viagem> findById(ViagemId id);

    @Query("SELECT v FROM Viagem v WHERE v.status IN ('INICIADA', 'PAUSADA')")
    List<Viagem> findAllEmAndamento();

    @Query("SELECT v FROM Viagem v WHERE v.data >= :inicio AND v.data < :fim AND v.status = 'PLANEJADA'")
    List<Viagem> findViagensPlanejadasNoIntervalo(LocalDateTime inicio, LocalDateTime fim);

    List<Viagem> findByMonitorIdAndStatusNot(UUID monitorId, Status status);

    List<Viagem> findByData(LocalDateTime data);
}

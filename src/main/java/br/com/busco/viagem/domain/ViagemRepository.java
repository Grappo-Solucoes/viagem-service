package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.sk.ids.ViagemPlanejadaId;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViagemRepository {
    Viagem save(Viagem viagem);
    Optional<Viagem> findById(ViagemId id);

    @Query("SELECT v FROM Viagem v WHERE v.status IN ('EM_ANDAMENTO', 'PAUSADA')")
    List<Viagem> findAllEmAndamento();

    Optional<Viagem> findByViagemPlanejada(ViagemPlanejadaId viagemPlanejadaId);

    @Query("SELECT v FROM Viagem v WHERE v.dataViagem = :data AND v.status = 'PENDENTE'")
    List<Viagem> findViagensPendentesDoDia(LocalDate data);
}

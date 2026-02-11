package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ids.ViagemId;

import java.util.Optional;

public interface ViagemRepository {
    Viagem save(Viagem viagem);
    Optional<Viagem> findById(ViagemId id);
}

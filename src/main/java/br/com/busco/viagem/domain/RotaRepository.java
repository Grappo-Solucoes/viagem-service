package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ids.RotaId;

import java.util.Optional;

public interface RotaRepository {
    Rota save(Rota rota);
    Optional<Rota> findById(RotaId id);
}

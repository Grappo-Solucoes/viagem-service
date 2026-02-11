package br.com.busco.viagem.planejamento.domain;

import br.com.busco.viagem.sk.ids.PlanejamentoId;

import java.util.Optional;

public interface PlanejamentoRepository {

    Planejamento save(Planejamento planejamento);
    Optional<Planejamento> findById(PlanejamentoId id);
}

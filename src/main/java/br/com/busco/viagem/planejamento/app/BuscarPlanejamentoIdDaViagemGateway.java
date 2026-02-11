package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;

public interface BuscarPlanejamentoIdDaViagemGateway {
    PlanejamentoId buscarPlanejamentoDaViagem(ViagemId viagem);
}

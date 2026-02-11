package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;

import java.util.List;

public interface BuscarViagensNaoIniciadasPlanejamentoGateway {
    List<ViagemId> buscarViagensNaoIniciadasPlanejamento(PlanejamentoId planejamento);
}

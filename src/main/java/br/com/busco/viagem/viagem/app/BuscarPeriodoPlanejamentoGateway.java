package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.PlanejamentoId;

public interface BuscarPeriodoPlanejamentoGateway {

    PeriodoPlanejamento buscarPeriodoPlanejamento(PlanejamentoId planejamento);
}

package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;

public interface IsUltimaViagemPlanejamentoPolicy {
    boolean isUltimaViagemDoPlanejamento(ViagemId viagem, PlanejamentoId planejamento);
}

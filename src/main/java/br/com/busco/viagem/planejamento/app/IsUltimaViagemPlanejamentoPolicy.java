package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.sk.ids.ViagemId;

public interface IsUltimaViagemPlanejamentoPolicy {
    boolean isUltimaViagemDoPlanejamento(ViagemId id);
}

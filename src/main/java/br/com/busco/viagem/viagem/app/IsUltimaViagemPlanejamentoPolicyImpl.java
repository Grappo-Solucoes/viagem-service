package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.planejamento.app.IsUltimaViagemPlanejamentoPolicy;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class IsUltimaViagemPlanejamentoPolicyImpl implements IsUltimaViagemPlanejamentoPolicy {
    private ViagemRepository repository;

    @Override
    public boolean isUltimaViagemDoPlanejamento(ViagemId viagem, PlanejamentoId planejamento) {
        return repository.isUltimaViagemDoPlanejamento(viagem, planejamento);
    }
}

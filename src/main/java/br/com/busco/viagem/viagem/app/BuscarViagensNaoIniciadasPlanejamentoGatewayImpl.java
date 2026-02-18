package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.ViagemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.List;

@Log
@Component
@AllArgsConstructor
public class BuscarViagensNaoIniciadasPlanejamentoGatewayImpl implements BuscarViagensNaoIniciadasPlanejamentoGateway {
    private final ViagemRepository repository;

    @Override
    public List<ViagemId> buscarViagensNaoIniciadasPlanejamento(PlanejamentoId planejamento) {
        return repository.buscarViagensNaoIniciadasPlanejamento(planejamento);
    }
}

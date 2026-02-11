package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.planejamento.app.BuscarPlanejamentoIdDaViagemGateway;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class BuscarPlanejamentoIdDaViagemGatewayImpl implements BuscarPlanejamentoIdDaViagemGateway {
    private final ViagemService service;

    @Override
    public PlanejamentoId buscarPlanejamentoDaViagem(ViagemId viagem) {
        return service.buscarPorId(viagem).getPlanejamento();
    }
}

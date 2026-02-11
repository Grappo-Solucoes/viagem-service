package br.com.busco.viagem.planejamento.infra.listener;

import br.com.busco.viagem.planejamento.app.BuscarPlanejamentoIdDaViagemGateway;
import br.com.busco.viagem.planejamento.app.IsPrimeiraViagemPlanejamentoPolicy;
import br.com.busco.viagem.planejamento.app.IsUltimaViagemPlanejamentoPolicy;
import br.com.busco.viagem.planejamento.app.PlanejamentoService;
import br.com.busco.viagem.planejamento.app.cmd.FinalizarPlanejamento;
import br.com.busco.viagem.planejamento.app.cmd.IniciarPlanejamento;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.viagem.domain.events.ViagemFinalizada;
import br.com.busco.viagem.viagem.domain.events.ViagemIniciada;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log
@Component
@AllArgsConstructor
public class ViagemListener {

    private final PlanejamentoService service;
    private final BuscarPlanejamentoIdDaViagemGateway gateway;
    private final IsPrimeiraViagemPlanejamentoPolicy primeiraViagemPolicy;
    private final IsUltimaViagemPlanejamentoPolicy ultimaViagemPolicy;

    @EventListener
    public void on(ViagemIniciada evt) {
        PlanejamentoId planejamento = gateway.buscarPlanejamentoDaViagem(evt.getId());
        IniciarPlanejamento cmd = IniciarPlanejamento.builder()
                .id(UUID.fromString(planejamento.toUUID()))
                .build();

        if (primeiraViagemPolicy.isPrimeiraViagemDoPlanejamento(evt.getId())) {
            service.handle(cmd);
        }
    }

    @EventListener
    public void on(ViagemFinalizada evt) {
        PlanejamentoId planejamento = gateway.buscarPlanejamentoDaViagem(evt.getId());
        FinalizarPlanejamento cmd = FinalizarPlanejamento.builder()
                .id(UUID.fromString(planejamento.toUUID()))
                .build();

        if (ultimaViagemPolicy.isUltimaViagemDoPlanejamento(evt.getId())) {
            service.handle(cmd);
        }
    }

}

package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.planejamento.app.PlanejamentoService;
import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class BuscarPeriodoPlanejamentoGatewayImpl implements BuscarPeriodoPlanejamentoGateway {
    private final PlanejamentoService service;

    @Override
    public PeriodoPlanejamento buscarPeriodoPlanejamento(PlanejamentoId planejamento) {
        Planejamento dados = service.buscarPorId(planejamento);
        br.com.busco.viagem.planejamento.domain.PeriodoPlanejado periodo = dados.getPeriodoPlanejado();

        return PeriodoPlanejamento.builder()
                .inicio(periodo.getInicio().toLocalDate())
                .fim(periodo.getFim().toLocalDate())
                .hora(periodo.getInicio().toLocalTime())
                .diasSemana(dados.getDiasDaSemana())
                .build();
    }
}

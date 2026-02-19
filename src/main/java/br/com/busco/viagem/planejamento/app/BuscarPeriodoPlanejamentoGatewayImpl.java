package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.planejamento.domain.PeriodoPlanejado;
import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.viagem.app.BuscarPeriodoPlanejamentoGateway;
import br.com.busco.viagem.viagem.app.PeriodoPlanejamento;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BuscarPeriodoPlanejamentoGatewayImpl implements BuscarPeriodoPlanejamentoGateway {
    private final PlanejamentoService service;

    @Override
    public PeriodoPlanejamento buscarPeriodoPlanejamento(PlanejamentoId planejamento) {
        Planejamento dados = service.buscarPorId(planejamento);
        PeriodoPlanejado periodo = dados.getPeriodoPlanejado();

        return PeriodoPlanejamento.builder()
                .inicio(periodo.getInicio().toLocalDate())
                .fim(periodo.getFim().toLocalDate())
                .hora(periodo.getInicio().toLocalTime())
                .diasSemana(dados.getDiasDaSemana())
                .build();
    }
}

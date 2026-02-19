package br.com.busco.viagem.planejamento.app;

import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.viagem.app.BuscarDadosPlanejamentoGateway;
import br.com.busco.viagem.viagem.app.DadosPlanejamentoAprovado;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Log
@Component
@AllArgsConstructor
public class BuscarDadosPlanejamentoGatewayImpl implements BuscarDadosPlanejamentoGateway {
    private final PlanejamentoService service;

    @Override
    public DadosPlanejamentoAprovado buscarPlanejamentoAprovado(PlanejamentoId planejamento) {
        Planejamento dados = service.buscarPorId(planejamento);

        return DadosPlanejamentoAprovado.builder()
                .rota(dados.getRota())
                .alunos(new ArrayList<>(dados.getPassageiros()))
                .motorista(dados.getMotorista())
                .monitor(dados.getMonitor())
                .veiculo(dados.getVeiculo())
                .grupoChecklistInicial(dados.getChecklistInicial())
                .grupoChecklistFinal(dados.getChecklistFinal())
                .planejamento(dados.getId())
                .build();
    }
}

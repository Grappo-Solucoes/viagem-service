package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.planejamento.app.PlanejamentoService;
import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

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

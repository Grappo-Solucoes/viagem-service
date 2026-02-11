package br.com.busco.viagem.viagem.infra.listener;

import br.com.busco.viagem.planejamento.domain.events.PlanejamentoAprovado;
import br.com.busco.viagem.planejamento.domain.events.PlanejamentoCancelado;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.app.*;
import br.com.busco.viagem.viagem.app.cmd.CancelarViagem;
import br.com.busco.viagem.viagem.app.cmd.CriarViagem;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log
@Component
@AllArgsConstructor
public class PlanejamentoListener {

    private final ViagemService service;
    private final BuscarPeriodoPlanejamentoGateway buscarPeriodoPlanejamentoGateway;
    private final BuscarDadosPlanejamentoGateway buscarDadosPlanejamentoGateway;
    private final BuscarViagensNaoIniciadasPlanejamentoGateway buscarViagensNaoIniciadasPlanejamentoGateway;

    @EventListener
    public void on(PlanejamentoAprovado evt) {
        PeriodoPlanejamento periodoPlanejamento = buscarPeriodoPlanejamentoGateway.buscarPeriodoPlanejamento(evt.getId());
        DadosPlanejamentoAprovado dadosPlanejamentoAprovado = buscarDadosPlanejamentoGateway.buscarPlanejamentoAprovado(evt.getId());

        List<LocalDateTime> datasAgendadas = periodoPlanejamento.gerarDatasAgendadas();
        for (LocalDateTime dataAgendada: datasAgendadas) {
            CriarViagem cmd = CriarViagem.builder()
                    .data(dataAgendada)
                    //.rota()
                    .alunos(dadosPlanejamentoAprovado.getAlunos().stream().map(AlunoId::toUUID).map(UUID::fromString).toList())
                    .motorista(UUID.fromString(dadosPlanejamentoAprovado.getMotorista().toUUID()))
                    .monitor(UUID.fromString(dadosPlanejamentoAprovado.getMonitor().toUUID()))
                    .veiculo(UUID.fromString(dadosPlanejamentoAprovado.getVeiculo().toUUID()))
                    .grupoChecklistInicial(UUID.fromString(dadosPlanejamentoAprovado.getGrupoChecklistInicial().toUUID()))
                    .grupoChecklistFinal(UUID.fromString(dadosPlanejamentoAprovado.getGrupoChecklistFinal().toUUID()))
                    .planejamento(UUID.fromString(evt.getId().toUUID()))
                    .build();

            service.handle(cmd);
        }
    }

    @EventListener
    public void on(PlanejamentoCancelado evt) {
        List<ViagemId> viagensCanceladas = buscarViagensNaoIniciadasPlanejamentoGateway.buscarViagensNaoIniciadasPlanejamento(evt.getId());
        for (ViagemId viagemCancelada : viagensCanceladas) {
            CancelarViagem cmd = CancelarViagem.builder().id(UUID.fromString(viagemCancelada.toUUID())).build();
            service.handle(cmd);
        }
    }
}

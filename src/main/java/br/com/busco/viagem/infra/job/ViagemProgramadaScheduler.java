package br.com.busco.viagem.infra.job;

import br.com.busco.viagem.app.ViagemPlanejadaEvent;
import br.com.busco.viagem.app.ViagemService;
import br.com.busco.viagem.app.cmd.CriarViagem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reage a {@link ViagemPlanejadaEvent} criando as viagens de execução.
 * Substitui a leitura JDBC de {@code agendamento_operacional} (tabela de
 * outro contexto). Desligável por profile ({@code sem-agendamento}) e
 * inativo nos testes.
 */
@Component
@Profile("!test & !sem-agendamento")
@RequiredArgsConstructor
@Slf4j
public class ViagemProgramadaScheduler {

    private final ViagemService viagemService;

    @EventListener
    public void on(ViagemPlanejadaEvent evento) {
        if (evento.datas() == null || evento.datas().isEmpty()) {
            return;
        }
        int criadas = 0;
        for (var data : evento.datas()) {
            try {
                CriarViagem cmd = CriarViagem.builder()
                        .data(data)
                        .rota(evento.rotaId())
                        .motorista(evento.motoristaId())
                        .monitor(evento.monitorId())
                        .veiculo(evento.veiculoId())
                        .planejamento(evento.planejamentoId())
                        .passageiros(evento.passageiros())
                        .alunos(evento.passageiros() == null ? null
                                : evento.passageiros().stream()
                                        .map(p -> p.getPassageiroId()).toList())
                        .build();
                viagemService.handle(cmd);
                criadas++;
            } catch (Exception ex) {
                log.error("Erro ao criar viagem programada para {}", data, ex);
            }
        }
        log.info("{} de {} viagens programadas criadas", criadas, evento.datas().size());
    }
}

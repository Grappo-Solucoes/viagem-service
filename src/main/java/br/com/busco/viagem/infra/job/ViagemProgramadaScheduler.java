package br.com.busco.viagem.infra.job;

import br.com.busco.viagem.app.ViagemService;
import br.com.busco.viagem.app.cmd.CriarViagem;
import br.com.busco.viagem.sk.ddd.TenantContext;
import br.com.busco.viagem.sk.ddd.TenantId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class ViagemProgramadaScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final ViagemService viagemService;

    @Value("${viagem.criacao-programada.duracao-padrao-minutos:60}")
    private long duracaoPadraoMinutos;

    @Value("${viagem.criacao-programada.zone:America/Sao_Paulo}")
    private String zone;

    /*
     Executa:
     - todo dia à 00:00
     - cria as viagens de execução para os agendamentos confirmados do dia
    */
    @Scheduled(cron = "0 0 0 * * *", zone = "${viagem.criacao-programada.zone:America/Sao_Paulo}")
    public void criarViagensProgramadasDoDia() {
        LocalDate data = LocalDate.now(ZoneId.of(zone));

        try {
            List<ViagemProgramada> viagensProgramadas = buscarViagensProgramadas(data);
            int viagensCriadas = 0;

            for (ViagemProgramada viagemProgramada : viagensProgramadas) {
                try {
                    criarViagem(viagemProgramada, data);
                    viagensCriadas++;
                } catch (Exception ex) {
                    log.error("Erro ao criar viagem programada {}", viagemProgramada.id(), ex);
                }
            }

            log.info("{} de {} viagens programadas criadas para {}", viagensCriadas, viagensProgramadas.size(), data);
        } catch (Exception ex) {
            log.error("Erro ao criar viagens programadas para {}", data, ex);
        }
    }

    private List<ViagemProgramada> buscarViagensProgramadas(LocalDate data) {
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.plusDays(1).atStartOfDay();

        return jdbcTemplate.query(
                """
                SELECT a.id, a.rota_id, a.data, a.tenant_id
                  FROM agendamento_operacional a
                 WHERE a.status = 'CONFIRMADO'
                   AND a.data >= ?
                   AND a.data < ?
                   AND NOT EXISTS (
                       SELECT 1
                         FROM viagens_execucao v
                        WHERE v.viagem_planejada_id = a.id
                          AND v.tenant_id = a.tenant_id
                   )
                """,
                (rs, rowNum) -> new ViagemProgramada(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("rota_id")),
                        rs.getTimestamp("data").toLocalDateTime(),
                        TenantId.fromString(rs.getString("tenant_id"))
                ),
                Timestamp.valueOf(inicioDoDia),
                Timestamp.valueOf(fimDoDia)
        );
    }

    private void criarViagem(ViagemProgramada viagemProgramada, LocalDate data) {
        LocalDateTime horarioInicio = viagemProgramada.horarioInicio();

        try {
            TenantContext.set(viagemProgramada.tenantId());

            CriarViagem cmd = CriarViagem.builder()
                    .viagem(viagemProgramada.id())
                    .rota(viagemProgramada.rota())
                    .horarioInicio(horarioInicio)
                    .horarioFim(horarioInicio.plusMinutes(duracaoPadraoMinutos))
                    .dataViagem(data)
                    .build();

            viagemService.handle(cmd);
        } finally {
            TenantContext.clear();
        }
    }

    private record ViagemProgramada(UUID id, UUID rota, LocalDateTime horarioInicio, TenantId tenantId) {
    }
}

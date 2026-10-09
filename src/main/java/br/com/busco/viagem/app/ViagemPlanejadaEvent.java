package br.com.busco.viagem.app;

import br.com.busco.viagem.domain.Passageiro;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Planejamento validado/confirmado (decisão D4): o contexto de planejamento
 * publica este evento e o viagem-service cria as viagens de execução — sem
 * leitura JDBC em tabelas alheias.
 */
@Builder
public record ViagemPlanejadaEvent(
        UUID planejamentoId,
        List<LocalDateTime> datas,
        UUID rotaId,
        UUID motoristaId,
        UUID monitorId,
        UUID veiculoId,
        List<Passageiro> passageiros) {
}

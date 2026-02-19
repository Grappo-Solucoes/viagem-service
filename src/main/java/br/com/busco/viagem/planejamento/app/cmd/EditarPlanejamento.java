package br.com.busco.viagem.planejamento.app.cmd;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditarPlanejamento {
    private UUID id;
    private LocalDateTime partida;
    private LocalDateTime chegada;
    private UUID motorista;
    private UUID rota;
    private List<UUID> calendario;
    private UUID veiculo;
    private UUID monitor;
    private List<UUID> alunos;
    private UUID grupoChecklistInicial;
    private UUID grupoChecklistFinal;
}

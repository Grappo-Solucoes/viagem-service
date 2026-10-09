package br.com.busco.viagem.app.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarViagem {
    private UUID id;
    private LocalDateTime data;
    private UUID rota;
    private List<UUID> alunos;
    private UUID motorista;
    private UUID monitor;
    private UUID veiculo;
    private UUID grupoChecklistInicial;
    private UUID grupoChecklistFinal;
}

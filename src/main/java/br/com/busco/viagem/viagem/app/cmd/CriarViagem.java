package br.com.busco.viagem.viagem.app.cmd;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriarViagem {
    private LocalDateTime data;
    private UUID rota;
    private List<UUID> alunos;
    private UUID motorista;
    private UUID monitor;
    private UUID veiculo;
    private UUID grupoChecklistInicial;
    private UUID grupoChecklistFinal;
    private UUID planejamento;
}
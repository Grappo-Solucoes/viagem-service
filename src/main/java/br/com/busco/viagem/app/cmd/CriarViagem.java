package br.com.busco.viagem.app.cmd;

import br.com.busco.viagem.domain.Passageiro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Payload igual ao {@code CriarViagem} do monólito. Checklist é aceito e ignorado (outro contexto). */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CriarViagem {
    @NotNull
    private LocalDateTime data;
    @NotNull
    private UUID rota;
    private List<UUID> alunos;
    @NotNull
    private UUID motorista;
    @NotNull
    private UUID monitor;
    @NotNull
    private UUID veiculo;
    private UUID grupoChecklistInicial;
    private UUID grupoChecklistFinal;
    private UUID planejamento;
    /** Snapshots completos quando a origem os informa; senão usa só os ids de `alunos`. */
    private List<Passageiro> passageiros;
}

package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.domain.ParadaExecucao;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParadaForaDeSequenciaEvent implements DomainEvent {
    @NotNull(message = "O parametro 'id' e obrigatorio!")
    private ViagemId id;
    private Integer ordemParada;
    private Instant occurredOn;

    public static ParadaForaDeSequenciaEvent from(Viagem viagem, ParadaExecucao parada) {
        return new ParadaForaDeSequenciaEvent(viagem.getId(), parada.getOrdem(), Instant.now());
    }
}

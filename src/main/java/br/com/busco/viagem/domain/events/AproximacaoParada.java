package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.Viagem;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AproximacaoParada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private Integer ordemParada;
    private Instant occurredOn;

    public static AproximacaoParada from(Viagem viagem, Integer ordemParada) {
        return new AproximacaoParada(viagem.getId(), ordemParada, Instant.now());
    }
}

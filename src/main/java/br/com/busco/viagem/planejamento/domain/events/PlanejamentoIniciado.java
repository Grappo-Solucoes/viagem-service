package br.com.busco.viagem.planejamento.domain.events;

import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlanejamentoIniciado implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private PlanejamentoId id;
    private Instant occurredOn;

    public static PlanejamentoIniciado from(Planejamento planejamento) {
        return new PlanejamentoIniciado(planejamento.getId(), Instant.now());
    }
}

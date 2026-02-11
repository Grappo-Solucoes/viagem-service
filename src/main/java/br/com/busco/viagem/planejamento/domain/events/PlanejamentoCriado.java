package br.com.busco.viagem.planejamento.domain.events;

import br.com.busco.viagem.planejamento.domain.Planejamento;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.PlanejamentoId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.viagem.domain.Viagem;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlanejamentoCriado implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private PlanejamentoId id;
    private Instant occurredOn;

    public static PlanejamentoCriado from(Planejamento planejamento) {
        return new PlanejamentoCriado(planejamento.getId(), Instant.now());
    }
}

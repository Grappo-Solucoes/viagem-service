package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.Viagem;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParadasPendentes implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private List<Integer> ordemParadas;
    private Instant occurredOn;

    public static ParadasPendentes from(Viagem viagem, List<Integer> ordemParadas) {
        return new ParadasPendentes(viagem.getId(), ordemParadas, Instant.now());
    }
}

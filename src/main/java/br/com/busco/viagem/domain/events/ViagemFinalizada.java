package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.Viagem;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViagemFinalizada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private LocalDateTime horarioFim;
    private Instant occurredOn;

    public static ViagemFinalizada from(Viagem viagem, LocalDateTime horario) {
        return new ViagemFinalizada(viagem.getId(), horario, Instant.now());
    }
}

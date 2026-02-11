package br.com.busco.viagem.viagem.infra.listener.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CalendarioAtualizado implements DomainEvent {

    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private UUID id;
    private Instant occurredOn;
}

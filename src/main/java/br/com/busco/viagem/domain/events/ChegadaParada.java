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
public class ChegadaParada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private int ordemParada;
    private LocalDateTime horarioChegada;
    private Instant occurredOn;

    public static ChegadaParada from(Viagem viagem, int ordem, LocalDateTime horarioChegada) {
        return new ChegadaParada(viagem.getId(), ordem, horarioChegada, Instant.now());
    }
}

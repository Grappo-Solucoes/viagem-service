package br.com.busco.viagem.ocorrencia.domain.events;

import br.com.busco.viagem.ocorrencia.domain.Ocorrencia;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OcorrenciaTratativasIniciada implements DomainEvent {

    @NotNull(message = "O parametro 'id' e obrigatorio!")
    private OcorrenciaId id;
    private Instant occurredOn;

    public static OcorrenciaTratativasIniciada from(Ocorrencia ocorrencia) {
        return new OcorrenciaTratativasIniciada(ocorrencia.getId(), Instant.now());
    }
}

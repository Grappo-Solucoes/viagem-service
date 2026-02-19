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
public class OcorrenciaRealizada implements DomainEvent {

    @NotNull(message = "O parametro 'id' e obrigatorio!")
    private OcorrenciaId id;
    private Instant occurredOn;

    public static OcorrenciaRealizada from(Ocorrencia ocorrencia) {
        return new OcorrenciaRealizada(ocorrencia.getId(), Instant.now());
    }
}

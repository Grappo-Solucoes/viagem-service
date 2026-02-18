package br.com.busco.viagem.ocorrencia.domain.events;

import br.com.busco.viagem.ocorrencia.domain.TipoOcorrencia;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TipoOcorrenciaCriada implements DomainEvent {

    @NotNull(message = "O parametro 'id' e obrigatorio!")
    private TipoOcorrenciaId id;
    private Instant occurredOn;

    public static TipoOcorrenciaCriada from(TipoOcorrencia tipoOcorrencia) {
        return new TipoOcorrenciaCriada(tipoOcorrencia.getId(), Instant.now());
    }
}

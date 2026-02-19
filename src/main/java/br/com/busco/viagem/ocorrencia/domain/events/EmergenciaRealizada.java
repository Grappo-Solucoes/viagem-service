package br.com.busco.viagem.ocorrencia.domain.events;

import br.com.busco.viagem.ocorrencia.domain.Emergencia;
import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.EmergenciaId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmergenciaRealizada implements DomainEvent {

    @NotNull(message = "O parametro 'id' e obrigatorio!")
    private EmergenciaId id;
    private Instant occurredOn;

    public static EmergenciaRealizada from(Emergencia emergencia) {
        return new EmergenciaRealizada(emergencia.getId(), Instant.now());
    }
}

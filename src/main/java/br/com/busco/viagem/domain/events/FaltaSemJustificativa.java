package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.domain.Passageiro;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Faltas sem justificativa: esperados que nunca embarcaram. Sem listas de
 * ausência (outro contexto) — só embarcados vs esperados.
 */
@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FaltaSemJustificativa implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private List<PassageiroFaltante> passageiros;
    private Instant occurredOn;

    public static FaltaSemJustificativa of(Viagem viagem, List<Passageiro> faltantes) {
        return new FaltaSemJustificativa(viagem.getId(),
                faltantes.stream().map(PassageiroFaltante::of).toList(), Instant.now());
    }

    @Data
    @Builder
    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PassageiroFaltante {
        private UUID id;
        private String nome;

        public static PassageiroFaltante of(Passageiro passageiro) {
            return new PassageiroFaltante(passageiro.getPassageiroId(), passageiro.getNome());
        }
    }
}

package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.Viagem;
import br.com.busco.viagem.domain.Passageiro;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AlunoEmbarcado implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private UUID passageiroId;
    private String nomePassageiro;
    private Instant occurredOn;

    public static AlunoEmbarcado of(Viagem viagem, Passageiro passageiro) {
        return new AlunoEmbarcado(viagem.getId(), passageiro.getPassageiroId(),
                passageiro.getNome(), Instant.now());
    }
}

package br.com.busco.viagem.domain.events;

import br.com.busco.viagem.sk.ddd.DomainEvent;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.domain.LocalizacaoAtual;
import br.com.busco.viagem.domain.Viagem;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PosicaoAtualizada implements DomainEvent {
    @NotNull(message = "O parâmetro 'id' é obrigatório!")
    private ViagemId id;
    private LocalizacaoAtual novaPosicao;
    private Instant occurredOn;

    public static PosicaoAtualizada from(Viagem viagem, LocalizacaoAtual novaPosicao) {
        return new PosicaoAtualizada(viagem.getId(), novaPosicao, Instant.now());
    }
}

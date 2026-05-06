package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParadaPrevista implements ValueObject  {
    int ordem;
    LocalDateTime horarioPrevisto;
    int quantidadeAlunos;

    @Builder
    public ParadaPrevista(int ordem, LocalDateTime horarioPrevisto, int quantidadeAlunos) {
        this.ordem = ordem;
        this.horarioPrevisto = horarioPrevisto;
        this.quantidadeAlunos = quantidadeAlunos;
    }
}

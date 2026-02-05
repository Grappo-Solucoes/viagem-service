package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = "valor")
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Destino implements ValueObject {
    public static Destino EMPTY = new Destino("");

    private String valor;

    public static Destino of(String funcao) {
        if (isNull(funcao) || funcao.isEmpty()) {
            return EMPTY;
        }
        return new Destino(funcao);
    }

}

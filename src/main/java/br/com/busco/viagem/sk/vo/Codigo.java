package br.com.busco.viagem.sk.vo;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode(of = "codigo")
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Codigo implements ValueObject {

    public static Codigo EMPTY = new Codigo(0);

    private Integer codigo;

    public String toFormattedString() {
        return "#" + codigo;
    }

    public static Codigo of(Integer codigo) {
        if (isNull(codigo) || codigo == 0) {
            return EMPTY;
        }
        return new Codigo(codigo);
    }

}
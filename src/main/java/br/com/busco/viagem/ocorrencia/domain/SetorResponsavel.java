package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Getter
@Embeddable
@EqualsAndHashCode(of = "valor")
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SetorResponsavel implements ValueObject {

    public static final SetorResponsavel VAZIO = new SetorResponsavel("");

    private String valor;

    public static SetorResponsavel of(String valor) {
        if (isNull(valor) || valor.isBlank()) {
            return VAZIO;
        }
        return new SetorResponsavel(valor);
    }
}

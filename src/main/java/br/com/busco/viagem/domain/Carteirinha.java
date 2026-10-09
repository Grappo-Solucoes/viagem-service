package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Texto bruto da carteirinha/QR lido no embarque, formato
 * {@code "NOME;MATRICULA;{ordem} - TURNO;ESCOLA"} (mesmo do monólito).
 */
@Getter
@ToString
@EqualsAndHashCode(of = "codigo")
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Carteirinha implements ValueObject {

    private String codigo;

    private Carteirinha(String codigo) {
        this.codigo = codigo;
    }

    public static Carteirinha of(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("O código da carteirinha não pode ser nulo nem vazio");
        }
        return new Carteirinha(codigo);
    }

    public String nome() {
        return campo(0);
    }

    public String matricula() {
        return campo(1);
    }

    public String turno() {
        return campo(2);
    }

    public String escola() {
        return campo(3);
    }

    private String campo(int indice) {
        if (codigo == null || codigo.isEmpty()) {
            return "";
        }
        String[] partes = codigo.split(";", -1);
        return indice < partes.length ? partes[indice].trim() : "";
    }
}

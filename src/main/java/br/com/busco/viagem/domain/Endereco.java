package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Endereço de um ponto da rota (agregado local do contexto viagem).
 * Campos alinhados ao {@code Endereco} do cadastro no monólito.
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"logradouro", "numero", "cep"})
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endereco implements ValueObject {

    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String latitude;
    private String longitude;

    @JsonCreator
    public Endereco(@JsonProperty("logradouro") String logradouro,
                    @JsonProperty("numero") Integer numero,
                    @JsonProperty("complemento") String complemento,
                    @JsonProperty("bairro") String bairro,
                    @JsonProperty("cidade") String cidade,
                    @JsonProperty("estado") String estado,
                    @JsonProperty("cep") String cep,
                    @JsonProperty("latitude") String latitude,
                    @JsonProperty("longitude") String longitude) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String completo() {
        return String.format("%s, %s - %s, %s - %s",
                logradouro, numero, bairro, cidade, estado);
    }
}

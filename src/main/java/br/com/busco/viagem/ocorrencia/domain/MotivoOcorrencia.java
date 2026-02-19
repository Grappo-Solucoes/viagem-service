package br.com.busco.viagem.ocorrencia.domain;

import org.apache.commons.lang3.Validate;

import jakarta.persistence.Entity;
import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Table
@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MotivoOcorrencia implements ValueObject {

    private String motivo;

    public static final MotivoOcorrencia VAZIO = new MotivoOcorrencia("");

    public static MotivoOcorrencia of(@NonNull String motivo) {
        if (motivo.isBlank()) {
            return VAZIO;
        }
        return new MotivoOcorrencia(motivo);
    }

}

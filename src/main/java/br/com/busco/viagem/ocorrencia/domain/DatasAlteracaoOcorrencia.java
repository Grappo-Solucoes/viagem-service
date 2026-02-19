package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"data", "dataInicioAnalise", "dataInicioTratativas", "dataFinalizada"})
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasAlteracaoOcorrencia implements ValueObject {

    private LocalDateTime data;
    private LocalDateTime dataInicioAnalise;
    private LocalDateTime dataInicioTratativas;
    private LocalDateTime dataFinalizada;

    public static DatasAlteracaoOcorrencia criar() {
        return new DatasAlteracaoOcorrencia(LocalDateTime.now(), null, null, null);
    }

    public DatasAlteracaoOcorrencia comInicioAnalise() {
        return new DatasAlteracaoOcorrencia(
                this.data,
                LocalDateTime.now(),
                this.dataInicioTratativas,
                this.dataFinalizada
        );
    }

    public DatasAlteracaoOcorrencia comInicioTratativas() {
        return new DatasAlteracaoOcorrencia(
                this.data,
                this.dataInicioAnalise,
                LocalDateTime.now(),
                this.dataFinalizada
        );
    }

    public DatasAlteracaoOcorrencia comFinalizacao() {
        return new DatasAlteracaoOcorrencia(
                this.data,
                this.dataInicioAnalise,
                this.dataInicioTratativas,
                LocalDateTime.now()
        );
    }
}

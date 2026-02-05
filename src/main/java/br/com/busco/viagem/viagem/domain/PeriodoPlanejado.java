package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.viagem.domain.exceptions.PlanejamentoPeriodoSemDatasPrevistas;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"partida", "chegada"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class PeriodoPlanejado implements ValueObject {

    private LocalDateTime partida;
    private LocalDateTime chegada;
    private Duration duracaoEstimada;

    public static PeriodoPlanejado of(LocalDateTime partida, LocalDateTime chegada) {
        if (isNull(partida) || isNull(chegada)) {
            throw new PlanejamentoPeriodoSemDatasPrevistas();
        }
        return new PeriodoPlanejado(partida, chegada, Duration.between(partida, chegada));
    }

}

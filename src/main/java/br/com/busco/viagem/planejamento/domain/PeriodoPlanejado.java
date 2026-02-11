package br.com.busco.viagem.planejamento.domain;

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
@EqualsAndHashCode(of = {"inicio", "fim"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class PeriodoPlanejado implements ValueObject {

    private LocalDateTime inicio;
    private LocalDateTime fim;

    public static PeriodoPlanejado of(LocalDateTime partida, LocalDateTime chegada) {
        if (isNull(partida) || isNull(chegada)) {
            throw new PlanejamentoPeriodoSemDatasPrevistas();
        }
        return new PeriodoPlanejado(partida, chegada);
    }

}

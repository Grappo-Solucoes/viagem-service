package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.exceptions.PlanejamentoPeriodoSemDatasPrevistas;
import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {
        "horarioPrevistoInicio",
        "horarioPrevistoFim",
        "horarioRealInicio",
        "horarioRealFim"
})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class PeriodoViagem implements ValueObject {

    private LocalDateTime horarioPrevistoInicio;
    private LocalDateTime horarioPrevistoFim;
    private LocalDateTime horarioRealInicio;
    private LocalDateTime horarioRealFim;

    private PeriodoViagem(LocalDateTime horarioPrevistoInicio, LocalDateTime horarioPrevistoFim, LocalDateTime horarioRealInicio, LocalDateTime horarioRealFim) {
        this.horarioPrevistoInicio = horarioPrevistoInicio;
        this.horarioPrevistoFim = horarioPrevistoFim;
        this.horarioRealInicio = horarioRealInicio;
        this.horarioRealFim = horarioRealFim;
    }

    private PeriodoViagem(LocalDateTime previstoInicio, LocalDateTime previstoFim) {
        this.horarioPrevistoInicio = previstoInicio;
        this.horarioPrevistoFim = previstoFim;
    }

    public PeriodoViagem withInicioReal(LocalDateTime inicioReal) {
        return new PeriodoViagem(
                horarioPrevistoInicio,
                horarioPrevistoFim,
                inicioReal,
                horarioRealFim
        );

    }

    public PeriodoViagem withFimReal(LocalDateTime fimReal) {
        return new PeriodoViagem(
                horarioPrevistoInicio,
                horarioPrevistoFim,
                horarioRealInicio,
                fimReal
        );
    }

    public Duration getAtrasoInicio() {
        if (horarioRealInicio == null) return Duration.ZERO;
        return Duration.between(horarioPrevistoInicio, horarioRealInicio);
    }

    public Duration getDuracaoReal() {
        if (horarioRealInicio == null || horarioRealFim == null) return Duration.ZERO;
        return Duration.between(horarioRealInicio, horarioRealFim);
    }

    public Duration getDuracaoPrevista() {
        return Duration.between(horarioPrevistoInicio, horarioPrevistoFim);
    }

    public static PeriodoViagem of(LocalDateTime partida, LocalDateTime chegada) {
        if (isNull(partida) || isNull(chegada)) {
            throw new PlanejamentoPeriodoSemDatasPrevistas();
        }
        return new PeriodoViagem(partida, chegada);
    }

}

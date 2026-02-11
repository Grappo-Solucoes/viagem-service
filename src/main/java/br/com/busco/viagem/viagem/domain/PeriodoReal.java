package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.viagem.domain.exceptions.RealizacaoPeriodoSemDatasPrevistas;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.*;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"partida", "chegada"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PROTECTED)
public class PeriodoReal implements ValueObject {

    private LocalDateTime partida;
    private LocalDateTime chegada;
    private Duration atrasoPartida;
    private Duration atrasoChegada;


    public void atualizarChegada(LocalDateTime chegada) {
        this.chegada = LocalDateTime.now();
        this.atrasoChegada = Duration.between(this.chegada, chegada);
    }

    public static PeriodoReal of(LocalDateTime prevista) {
        if ( isNull(prevista)) {
            throw new RealizacaoPeriodoSemDatasPrevistas();
        }

        LocalDateTime inicio = LocalDateTime.now();
        return new PeriodoReal(inicio, null, Duration.between(inicio, prevista), null);
    }
}

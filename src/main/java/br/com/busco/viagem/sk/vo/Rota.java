package br.com.busco.viagem.sk.vo;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.viagem.domain.Destino;
import br.com.busco.viagem.viagem.domain.Origem;
import br.com.busco.viagem.viagem.domain.exceptions.RotaSemOrigemDestinoDefinidas;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Distance;

import java.time.Duration;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"origem", "destino"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Rota implements ValueObject {

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "origem"))
    private Origem origem;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "destino"))
    private Destino destino;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "distancia")),
            @AttributeOverride(name = "metric", column = @Column(name = "metrica")),
    })
    private Distance distancia;

    @Column(name = "tempo_estimado_minutos")
    private Integer tempoEstimadoMinutos;

    public Duration getTempoEstimado() {
        return tempoEstimadoMinutos != null
                ? Duration.ofMinutes(tempoEstimadoMinutos)
                : null;
    }


    public static Rota of(Origem origem, Destino destino, Distance distancia, Integer tempoEstimadoMinutos) {
        if (isNull(origem) || isNull(destino) || isNull(distancia)) {
            throw new RotaSemOrigemDestinoDefinidas();
        }
        return new Rota(origem, destino, distancia, tempoEstimadoMinutos);
    }

}

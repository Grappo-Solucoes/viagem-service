package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"latitude", "longitude", "timestamp"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalizacaoAtual implements ValueObject {

    private Double latitude;
    private Double longitude;
    private Float velocidade;  // km/h
    private Float direcao;     // graus
    private LocalDateTime timestamp;
    private Float precisao;    // metros

    @Builder
    private LocalizacaoAtual(Double latitude, Double longitude, Float velocidade, Float direcao, LocalDateTime timestamp, Float precisao) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocidade = velocidade;
        this.direcao = direcao;
        this.timestamp = timestamp;
        this.precisao = precisao;
    }

    public Localizacao toLocalizacao() {
        return Localizacao.comCoordenadas(this.latitude, this.longitude);
    }

    public boolean isRecent() {
        return timestamp != null &&
                timestamp.isAfter(LocalDateTime.now().minusSeconds(30));
    }

    public static LocalizacaoAtual fromLocalizacao(Localizacao loc, LocalDateTime timestamp) {
        return new LocalizacaoAtual(
                loc.getLatitude(),
                loc.getLongitude(),
                null,
                null,
                timestamp,
                null
        );
    }
}
package br.com.busco.viagem.infra;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    public Long convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.getSeconds();
    }

    public Duration convertToEntityAttribute(Long seconds) {
        return seconds == null ? Duration.ZERO : Duration.ofSeconds(seconds);
    }
}
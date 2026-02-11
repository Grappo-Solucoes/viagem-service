package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"inicio", "fim"})
@NoArgsConstructor(access = PUBLIC, force = true)
public class PeriodoPlanejamento implements ValueObject {

    private LocalDate inicio;
    private LocalDate fim;
    private LocalTime hora;
    private Set<DayOfWeek> diasSemana;

    @Builder
    private PeriodoPlanejamento(LocalDate inicio, LocalDate fim, LocalTime hora, Set<DayOfWeek> diasSemana) {
        this.inicio = inicio;
        this.fim = fim;
        this.hora = hora;
        this.diasSemana = diasSemana;
    }

    public List<LocalDateTime> gerarDatasAgendadas() {
        List<LocalDateTime> filteredDateTimes = new ArrayList<>();

        LocalDate currentDate = inicio;
        while (!currentDate.isAfter(fim)) {
            if (diasSemana.contains(currentDate.getDayOfWeek())) {
                filteredDateTimes.add(LocalDateTime.of(currentDate, hora));
            }
            currentDate = currentDate.plusDays(1);
        }

        return filteredDateTimes;

    }
}

package br.com.busco.viagem.app.cmd;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IniciarViagem {
    private UUID id;
    private LocalDateTime horarioPartida;
}

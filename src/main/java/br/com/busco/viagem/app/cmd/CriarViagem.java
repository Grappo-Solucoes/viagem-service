package br.com.busco.viagem.app.cmd;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriarViagem {
    private UUID rota;
    private UUID viagem;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioFim;
    private LocalDate dataViagem;
}
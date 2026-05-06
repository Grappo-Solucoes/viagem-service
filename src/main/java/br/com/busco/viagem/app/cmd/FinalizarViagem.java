package br.com.busco.viagem.app.cmd;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FinalizarViagem {
    private UUID id;
    private LocalDateTime horarioFinalizacao;
}

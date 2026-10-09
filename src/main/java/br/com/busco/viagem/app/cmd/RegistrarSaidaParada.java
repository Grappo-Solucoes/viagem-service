package br.com.busco.viagem.app.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarSaidaParada {
    private UUID id;
    private Integer ordemParada;
    private LocalDateTime horarioSaida;
    private Double latitude;
    private Double longitude;
}

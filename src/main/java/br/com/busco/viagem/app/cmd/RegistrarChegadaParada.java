package br.com.busco.viagem.app.cmd;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarChegadaParada {
    private UUID id;
    private Integer ordemParada;
    private LocalDateTime horarioChegada;

}

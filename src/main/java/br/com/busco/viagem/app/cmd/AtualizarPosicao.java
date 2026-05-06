package br.com.busco.viagem.app.cmd;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarPosicao {
    private UUID id;
    Double latitude;
    Double longitude;
    Float velocidade;
    Float direcao;
    LocalDateTime timestamp;
    Float precisao;
}

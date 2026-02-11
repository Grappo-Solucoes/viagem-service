package br.com.busco.viagem.viagem.app.cmd;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FinalizarViagem {
    private UUID id;
}

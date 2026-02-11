package br.com.busco.viagem.planejamento.app.cmd;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicarPlanejamento {
    private UUID id;
}

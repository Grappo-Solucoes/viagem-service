package br.com.busco.viagem.ocorrencia.app.cmd;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarTipoDeOcorrencia {

    private UUID id;
    private String tipoOcorrencia;
}

package br.com.busco.viagem.ocorrencia.app.cmd;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriarOcorrencia {

    private UUID viagem;
    private UUID tipoOcorrencia;
    private String motivo;
    private String setorResponsavel;
    private UUID responsavelTratativas;
}

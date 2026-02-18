package br.com.busco.viagem.ocorrencia.app.cmd;

import br.com.busco.viagem.ocorrencia.domain.TipoEmergencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriarEmergencia {

    private UUID viagem;
    private String motivo;
    private TipoEmergencia tipoEmergencia;
}

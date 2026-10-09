package br.com.busco.viagem.app.cmd;

import br.com.busco.viagem.domain.Endereco;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtualizarRota {
    private UUID id;
    private String numeroRota;
    public String nome;
    private List<Endereco> enderecos;
    private String tempoEstimado;
    private String distancia;
}

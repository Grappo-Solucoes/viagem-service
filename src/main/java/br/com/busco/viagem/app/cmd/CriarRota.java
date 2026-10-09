package br.com.busco.viagem.app.cmd;

import br.com.busco.viagem.domain.Endereco;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CriarRota {
    public String numeroRota;
    public String nome;
    public List<Endereco> enderecos;
    private List<Rotas> rotas;
    public String tempoEstimado;
    public String distancia;

    @NoArgsConstructor
    @Data
    public static class Rotas {
        private String nome;
        private List<Ponto> pontos;
        private String tipo;
        private String markerColor;
        private Integer fontSize;
        private String iconUrl;
        private String polylineColor;
        private Integer polylineWeight;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class Ponto {
        private double lat;
        private double lng;
    }
}

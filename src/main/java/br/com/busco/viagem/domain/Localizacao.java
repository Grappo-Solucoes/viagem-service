package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Localizacao implements ValueObject {

    private Double latitude;
    private Double longitude;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    @Builder
    public Localizacao(Double latitude, Double longitude, String logradouro,
                       String numero, String complemento, String bairro,
                       String cidade, String estado, String cep) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;

        validar();
    }

    private void validar() {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude e longitude são obrigatórias");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude inválida: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude inválida: " + longitude);
        }
    }

    /**
     * Calcula distância em metros até outra localização (fórmula de Haversine)
     */
    public double distanciaAte(Localizacao outra) {
        return calcularDistanciaHaversine(
                this.latitude, this.longitude,
                outra.latitude, outra.longitude
        );
    }

    private double calcularDistanciaHaversine(double lat1, double lon1,
                                              double lat2, double lon2) {
        double R = 6371000; // Raio da Terra em metros

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

    /**
     * Formata endereço completo
     */
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro);
        if (numero != null && !numero.isEmpty()) {
            sb.append(", ").append(numero);
        }
        if (complemento != null && !complemento.isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        if (bairro != null && !bairro.isEmpty()) {
            sb.append(" - ").append(bairro);
        }
        sb.append(" - ").append(cidade).append("/").append(estado);
        return sb.toString();
    }

    /**
     * Factory method para criar apenas com coordenadas
     */
    public static Localizacao comCoordenadas(Double latitude, Double longitude) {
        return Localizacao.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /**
     * Verifica se está dentro de um raio de outra localização
     */
    public boolean estaDentroDoRaio(Localizacao centro, double raioMetros) {
        return distanciaAte(centro) <= raioMetros;
    }
}
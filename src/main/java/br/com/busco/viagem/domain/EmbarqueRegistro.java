package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Linha de embarque: quem embarcou, com GPS e horário do embarque e
 * (quando aplicável) do desembarque. Valor embutido na viagem, sem
 * tabela/repositório separado — mesmo papel do {@code EmbarqueRegistro}
 * do monólito, porém referenciando o passageiro por id + snapshot.
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"passageiroId", "embarcadoEm"})
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbarqueRegistro implements ValueObject {

    @Column(name = "passageiro_id", nullable = false)
    private UUID passageiroId;

    private String nome;
    private LocalDateTime embarcadoEm;
    private Double latitudeEmbarque;
    private Double longitudeEmbarque;

    @Column(nullable = false)
    private boolean planejado;

    @Column(length = 500)
    private String justificativa;

    private LocalDateTime desembarcadoEm;
    private Double latitudeDesembarque;
    private Double longitudeDesembarque;

    public EmbarqueRegistro(UUID passageiroId, String nome, Double latitude, Double longitude,
                            boolean planejado, String justificativa) {
        this.passageiroId = passageiroId;
        this.nome = nome;
        this.embarcadoEm = LocalDateTime.now();
        this.latitudeEmbarque = latitude;
        this.longitudeEmbarque = longitude;
        this.planejado = planejado;
        this.justificativa = justificativa;
    }

    /** Cópia com os dados de desembarque preenchidos. */
    public EmbarqueRegistro comDesembarque(Double latitude, Double longitude) {
        EmbarqueRegistro copia = new EmbarqueRegistro(passageiroId, nome, latitudeEmbarque,
                longitudeEmbarque, planejado, justificativa);
        copia.embarcadoEm = this.embarcadoEm;
        copia.desembarcadoEm = LocalDateTime.now();
        copia.latitudeDesembarque = latitude;
        copia.longitudeDesembarque = longitude;
        return copia;
    }

    public boolean estaEmbarcado() {
        return desembarcadoEm == null;
    }
}

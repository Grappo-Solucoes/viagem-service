package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.AbstractEntity;
import br.com.busco.viagem.sk.ids.ParadaExecucaoId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static br.com.busco.viagem.sk.ids.ParadaExecucaoId.randomId;

@Entity
@Table(name = "viagem_paradas")
@Getter
@EqualsAndHashCode(of = {"ordem"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParadaExecucao extends AbstractEntity<ParadaExecucaoId> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Column(nullable = false)
    private Integer ordem;

    private LocalDateTime horarioPrevisto;
    private LocalDateTime horarioChegadaReal;
    private Double latitudeChegada;
    private Double longitudeChegada;
    private LocalDateTime horarioSaidaReal;
    private Double latitudeSaida;
    private Double longitudeSaida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusParada status;

    private Integer ordemExecucao;

    @Builder
    public ParadaExecucao(Viagem viagem, Integer ordem,
                          LocalDateTime horarioPrevisto) {
        super(randomId());
        this.viagem = viagem;
        this.ordem = ordem;
        this.horarioPrevisto = horarioPrevisto;
        this.status = StatusParada.PENDENTE;
    }

    public void registrarChegada(LocalDateTime horario, int ordemExecucao) {
        registrarChegada(horario, ordemExecucao, null, null);
    }

    public void registrarChegada(LocalDateTime horario, int ordemExecucao,
                                 Double latitude, Double longitude) {
        if (this.status != StatusParada.PENDENTE && this.status != StatusParada.APROXIMANDO) {
            throw new IllegalStateException("Parada já processada");
        }
        this.horarioChegadaReal = horario;
        this.latitudeChegada = latitude;
        this.longitudeChegada = longitude;
        this.ordemExecucao = ordemExecucao;
        this.status = StatusParada.CHEGADA_REGISTRADA;
    }

    public void registrarSaida(LocalDateTime horario) {
        registrarSaida(horario, null, null);
    }

    public void registrarSaida(LocalDateTime horario, Double latitude, Double longitude) {
        if (this.status != StatusParada.CHEGADA_REGISTRADA) {
            throw new IllegalStateException("Parada precisa ter chegada antes da saída");
        }
        this.horarioSaidaReal = horario;
        this.latitudeSaida = latitude;
        this.longitudeSaida = longitude;
        this.status = StatusParada.CONCLUIDA;
    }

    /** Atraso da chegada em relação ao previsto (zero se adiantado/sem horários). */
    public java.time.Duration atrasoChegada() {
        if (horarioPrevisto == null || horarioChegadaReal == null) {
            return java.time.Duration.ZERO;
        }
        java.time.Duration atraso =
                java.time.Duration.between(horarioPrevisto, horarioChegadaReal);
        return atraso.isNegative() ? java.time.Duration.ZERO : atraso;
    }

    public boolean isPendente() {
        return status == StatusParada.PENDENTE;
    }

    public boolean isDestino() {
        return viagem != null &&
                viagem.getParadas() != null &&
                !viagem.getParadas().isEmpty() &&
                viagem.getParadas().get(viagem.getParadas().size() - 1).equals(this);
    }

    public boolean isForaDeSequencia() {
        return ordemExecucao != null && !ordemExecucao.equals(ordem);
    }
}
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
    private LocalDateTime horarioSaidaReal;

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
        if (this.status != StatusParada.PENDENTE && this.status != StatusParada.APROXIMANDO) {
            throw new IllegalStateException("Parada já processada");
        }
        this.horarioChegadaReal = horario;
        this.ordemExecucao = ordemExecucao;
        this.status = StatusParada.CHEGADA_REGISTRADA;
    }

    public void registrarSaida(LocalDateTime horario) {
        if (this.status != StatusParada.CHEGADA_REGISTRADA) {
            throw new IllegalStateException("Parada precisa ter chegada antes da saída");
        }
        this.horarioSaidaReal = horario;
        this.status = StatusParada.CONCLUIDA;
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
package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaAnaliseIniciada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaFinalizada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaRealizada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaTratativasIniciada;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Ocorrencia extends AbstractAggregateRoot<OcorrenciaId> {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "viagem_id", nullable = false))
    @NonNull
    private ViagemId viagem;

    @NonNull
    private String motivo;

    @Column(name = "user_id", nullable = false)
    @NonNull
    private UUID userId;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "tipo_ocorrencia_id", nullable = false))
    @NonNull
    private TipoOcorrenciaId tipoOcorrencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_ocorrencia", nullable = false)
    private StatusOcorrencia statusOcorrencia;

    private LocalDateTime data;

    private LocalDateTime dataInicioAnalise;

    private LocalDateTime dataInicioTratativas;

    private LocalDateTime dataFinalizada;

    private String setorResponsavel;

    private String responsavelTratativas;

    @Builder
    private Ocorrencia(@NonNull ViagemId viagem,
                       @NonNull String motivo,
                       @NonNull UUID userId,
                       @NonNull TipoOcorrenciaId tipoOcorrencia,
                       String setorResponsavel,
                       String responsavelTratativas) {
        super(OcorrenciaId.randomId());
        this.viagem = viagem;
        this.motivo = motivo;
        this.userId = userId;
        this.tipoOcorrencia = tipoOcorrencia;
        this.data = LocalDateTime.now();
        this.statusOcorrencia = StatusOcorrencia.PENDENTE;
        this.setorResponsavel = setorResponsavel;
        this.responsavelTratativas = responsavelTratativas;
    }

    public static class OcorrenciaBuilder {
        public Ocorrencia build() {
            Ocorrencia ocorrencia = new Ocorrencia(
                    this.viagem,
                    this.motivo,
                    this.userId,
                    this.tipoOcorrencia,
                    this.setorResponsavel,
                    this.responsavelTratativas
            );
            ocorrencia.registerEvent(OcorrenciaRealizada.from(ocorrencia));
            return ocorrencia;
        }
    }

    public void iniciarAnalise() {
        if (this.statusOcorrencia != StatusOcorrencia.PENDENTE) {
            throw new IllegalStateException("A analise so pode ser iniciada a partir do status PENDENTE.");
        }
        this.statusOcorrencia = StatusOcorrencia.EM_ANDAMENTO;
        this.dataInicioAnalise = LocalDateTime.now();
        this.registerEvent(OcorrenciaAnaliseIniciada.from(this));
    }

    public void iniciarTratativas() {
        if (this.statusOcorrencia != StatusOcorrencia.EM_ANDAMENTO) {
            throw new IllegalStateException("As tratativas so podem ser iniciadas a partir do status EM_ANDAMENTO.");
        }
        this.statusOcorrencia = StatusOcorrencia.TRATATIVAS;
        this.dataInicioTratativas = LocalDateTime.now();
        this.registerEvent(OcorrenciaTratativasIniciada.from(this));
    }

    public void finalizar() {
        if (this.statusOcorrencia != StatusOcorrencia.TRATATIVAS) {
            throw new IllegalStateException("A ocorrencia so pode ser finalizada a partir do status TRATATIVAS.");
        }
        this.statusOcorrencia = StatusOcorrencia.FINALIZADA;
        this.dataFinalizada = LocalDateTime.now();
        this.registerEvent(OcorrenciaFinalizada.from(this));
    }
}

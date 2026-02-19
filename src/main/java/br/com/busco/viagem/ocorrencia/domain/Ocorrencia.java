package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas;
import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente;
import br.com.busco.viagem.ocorrencia.domain.exceptions.NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaAnaliseIniciada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaFinalizada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaRealizada;
import br.com.busco.viagem.ocorrencia.domain.events.OcorrenciaTratativasIniciada;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.OcorrenciaId;
import br.com.busco.viagem.sk.ids.TipoOcorrenciaId;
import br.com.busco.viagem.sk.ids.UserId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @Embedded
    @AttributeOverride(name = "motivo", column = @Column(name = "motivo", nullable = false))
    private MotivoOcorrencia motivo;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "user_id", nullable = false))
    @NonNull
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "tipo_ocorrencia_id", nullable = false))
    @NonNull
    private TipoOcorrenciaId tipoOcorrencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_ocorrencia", nullable = false)
    private StatusOcorrencia statusOcorrencia;

    @Embedded
    private DatasAlteracaoOcorrencia datasAlteracao;

    @Embedded
    private SetorResponsavel setorResponsavel;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "responsavel_tratativas_id"))
    private UserId responsavelTratativas;

    @Builder
    private Ocorrencia(@NonNull ViagemId viagem,
                       @NonNull String motivo,
                       @NonNull UserId userId,
                       @NonNull TipoOcorrenciaId tipoOcorrencia,
                       SetorResponsavel setorResponsavel,
                       UserId responsavelTratativas) {
        super(OcorrenciaId.randomId());
        this.viagem = viagem;
        this.motivo = MotivoOcorrencia.of(motivo);
        this.userId = userId;
        this.tipoOcorrencia = tipoOcorrencia;
        this.datasAlteracao = DatasAlteracaoOcorrencia.criar();
        this.statusOcorrencia = StatusOcorrencia.PENDENTE;
        this.setorResponsavel = setorResponsavel != null ? setorResponsavel : SetorResponsavel.VAZIO;
        this.responsavelTratativas = responsavelTratativas != null ? responsavelTratativas : UserId.VAZIO;
        this.registerEvent(OcorrenciaRealizada.from(this));
    }

    public void iniciarAnalise() {
        if (this.statusOcorrencia != StatusOcorrencia.PENDENTE) {
            throw new NaoPossivelIniciarAnaliseOcorrenciaQueNaoSejaPendente();
        }
        this.statusOcorrencia = StatusOcorrencia.EM_ANDAMENTO;
        this.datasAlteracao = this.datasAlteracao.comInicioAnalise();
        this.registerEvent(OcorrenciaAnaliseIniciada.from(this));
    }

    public void iniciarTratativas() {
        if (this.statusOcorrencia != StatusOcorrencia.EM_ANDAMENTO) {
            throw new NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento();
        }
        this.statusOcorrencia = StatusOcorrencia.TRATATIVAS;
        this.datasAlteracao = this.datasAlteracao.comInicioTratativas();
        this.registerEvent(OcorrenciaTratativasIniciada.from(this));
    }

    public void finalizar() {
        if (this.statusOcorrencia != StatusOcorrencia.TRATATIVAS) {
            throw new NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas();
        }
        this.statusOcorrencia = StatusOcorrencia.FINALIZADA;
        this.datasAlteracao = this.datasAlteracao.comFinalizacao();
        this.registerEvent(OcorrenciaFinalizada.from(this));
    }

    public LocalDateTime getData() {
        return this.datasAlteracao.getData();
    }

    public LocalDateTime getDataInicioAnalise() {
        return this.datasAlteracao.getDataInicioAnalise();
    }

    public LocalDateTime getDataInicioTratativas() {
        return this.datasAlteracao.getDataInicioTratativas();
    }

    public LocalDateTime getDataFinalizada() {
        return this.datasAlteracao.getDataFinalizada();
    }
}

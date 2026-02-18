package br.com.busco.viagem.planejamento.domain;

import br.com.busco.viagem.planejamento.domain.events.*;
import br.com.busco.viagem.planejamento.domain.exceptions.NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Codigo;
import br.com.busco.viagem.sk.vo.Rota;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@Getter
@EqualsAndHashCode(of = {"rota", "periodoPlanejado"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public final class Planejamento extends AbstractAggregateRoot<PlanejamentoId> {

    @Embedded
    private Codigo codigo;

    @Embedded
    private Rota rota;

    @Embedded
    private PeriodoPlanejado periodoPlanejado;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "motorista_id"))
    private MotoristaId motorista;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "monitor_id"))
    private MonitorId monitor;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "veiculo_id"))
    private VeiculoId veiculo;

    @ElementCollection
    @CollectionTable(
            name = "planejamento_passageiros",
            joinColumns = @JoinColumn(name = "planejamento_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"planejamento_id", "aluno_id"}
            )
    )
    @Column(name = "aluno_id")
    private Set<AlunoId> passageiros = new HashSet<>();

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "checklist_inicial_id"))
    private GrupoChecklistId checklistInicial;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "checklist_final_id"))
    private GrupoChecklistId checklistFinal;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "planejamento_dias_da_semana",
            joinColumns = @JoinColumn(name = "planejamento_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_da_semana", nullable = false)
    private Set<DayOfWeek> diasDaSemana = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "planejamento_calendario",
            joinColumns = @JoinColumn(name = "planejamento_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"planejamento_id", "aluno_id"}
            )
    )
    @Column(name = "aluno_id")
    private Set<CalendarioId> calendarios = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder
    private Planejamento(Codigo codigo, Rota rota, PeriodoPlanejado periodoPlanejado, MotoristaId motorista, MonitorId monitor, VeiculoId veiculo, Set<AlunoId> passageiros, GrupoChecklistId checklistInicial, GrupoChecklistId checklistFinal, Set<DayOfWeek> diasDaSemana, Set<CalendarioId> calendarios) {
        super(PlanejamentoId.randomId());
        this.codigo = codigo;
        this.rota = rota;
        this.periodoPlanejado = periodoPlanejado;
        this.motorista = motorista;
        this.monitor = monitor;
        this.veiculo = veiculo;
        this.passageiros = passageiros;
        this.checklistInicial = checklistInicial;
        this.checklistFinal = checklistFinal;
        this.diasDaSemana = diasDaSemana;
        this.calendarios = calendarios;
        this.status = Status.EM_ANALISE;

        this.registerEvent(PlanejamentoCriado.from(this));
    }

    public void planejar() {
        this.status = Status.PLANEJADO;
        this.registerEvent(PlanejamentoAprovado.from(this));
    }

    public void cancelar() {
        this.status = Status.CANCELADO;
        this.registerEvent(PlanejamentoCancelado.of(this));
    }

    public void iniciar() {
        if (!this.status.equals(Status.PLANEJADO)) {
            throw new NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado();
        }

        this.status = Status.INICIADO;
        this.registerEvent(PlanejamentoIniciado.from(this));
    }


    public void finalizar() {
        if (!this.status.equals(Status.INICIADO)) {
            throw new NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado();
        }

        this.status = Status.FINALIZADO;
        this.registerEvent(PlanejamentoFinalizado.from(this));
    }

    public void update() {

    }

    public PlanejamentoForm updateForm() {
        return new PlanejamentoForm((form) -> {
            if (form.getRota() != null) {
                this.rota = form.getRota();
            }
            if (form.getData() != null && this.periodoPlanejado != null) {
                Duration duracao = Duration.between(this.periodoPlanejado.getInicio(), this.periodoPlanejado.getFim());
                this.periodoPlanejado = PeriodoPlanejado.of(form.getData(), form.getData().plus(duracao));
            }
            if (form.getMotorista() != null) {
                this.motorista = form.getMotorista();
            }
            if (form.getMonitor() != null) {
                this.monitor = form.getMonitor();
            }
            if (form.getVeiculo() != null) {
                this.veiculo = form.getVeiculo();
            }
            if (form.getPassageiros() != null) {
                this.passageiros = form.getPassageiros();
            }
            if (form.getChecklistInicial() != null) {
                this.checklistInicial = form.getChecklistInicial();
            }
            if (form.getChecklistFinal() != null) {
                this.checklistFinal = form.getChecklistFinal();
            }
            if (form.getCalendarios() != null) {
                this.calendarios = form.getCalendarios();
            }
        });
    }
}

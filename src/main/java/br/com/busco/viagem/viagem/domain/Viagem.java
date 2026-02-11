package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Codigo;
import br.com.busco.viagem.sk.vo.Rota;
import br.com.busco.viagem.viagem.domain.events.*;
import br.com.busco.viagem.viagem.domain.exceptions.NaoPossivelIniciarViagemQueNaoSejaPendente;
import br.com.busco.viagem.viagem.domain.exceptions.ViagemIncompleta;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Table
@Entity
@Getter
@EqualsAndHashCode(of = {"rota", "periodoPlanejado"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public final class Viagem extends AbstractAggregateRoot<ViagemId> {

    @Embedded
    private Codigo codigo;

    @Embedded
    private Rota rota;

    @Embedded
    private PeriodoPlanejado periodoPlanejado;

    @Embedded
    private PeriodoReal periodoReal;

    private Status status;

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
            name = "viagem_passageiros",
            joinColumns = @JoinColumn(name = "viagem_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"viagem_id", "aluno_id"}
            )
    )
    @Column(name = "aluno_id")
    private Set<AlunoId> passageiros = new HashSet<>();

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "planejamento_id"))
    private PlanejamentoId planejamento;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "checklist_inicial_id"))
    private GrupoChecklistId checklistInicial;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "checklist_final_id"))
    private GrupoChecklistId checklistFinal;

    @Builder
    private Viagem(Codigo codigo,
                   Rota rota,
                   PeriodoPlanejado periodoPlanejado,
                   MotoristaId motorista,
                   MonitorId monitor,
                   VeiculoId veiculo,
                   PlanejamentoId planejamento,
                   GrupoChecklistId checklistInicial,
                   GrupoChecklistId checklistFinal) {
        super(ViagemId.randomId());
        this.rota = rota;
        this.periodoPlanejado = periodoPlanejado;
        this.status = Status.PENDENTE;
        this.motorista = motorista;
        this.monitor = monitor;
        this.veiculo = veiculo;
        this.codigo = codigo;
        this.planejamento = planejamento;
        this.checklistInicial = checklistInicial;
        this.checklistFinal = checklistFinal;

        this.registerEvent(ViagemCriada.from(this));
    }

    public void alocarMotorista(MotoristaId motorista) {
        this.motorista = motorista;
        this.registerEvent(MotoristaAlocado.from(this));
    }

    public void alocarMonitor(MonitorId monitor) {
        this.monitor = monitor;
        this.registerEvent(MonitorAlocado.from(this));
    }

    public void trocarVeiculo(VeiculoId veiculo) {
        this.veiculo = veiculo;
        this.registerEvent(VeiculoAlterado.from(this));
    }

    public void trocarRota(Rota rota) {
        this.rota = rota;
        this.registerEvent(RotaAlterada.from(this));
    }

    public void desalocarMotorista() {
        this.motorista = MotoristaId.VAZIO;
        this.registerEvent(MotoristaDesalocado.from(this));
    }

    public void desalocarMonitor() {
        this.monitor = MonitorId.VAZIO;
        this.registerEvent(MonitorDesalocado.from(this));
    }

    public void trocarMotorista(MotoristaId motorista) {
        desalocarMotorista();
        alocarMotorista(motorista);
    }

    public void trocarMonitor(MonitorId monitor) {
        desalocarMonitor();
        alocarMonitor(monitor);
    }

    public ViagemForm update() {
        return new ViagemForm((form) -> {
            trocarRota(form.getRota());
            trocarMotorista(form.getMotorista());
            trocarMonitor(form.getMonitor());
            trocarVeiculo(form.getVeiculo());
            adicionarPassageiros(form.getPassageiros());
            this.periodoPlanejado = form.getPeriodoPlanejado();
            this.checklistInicial = form.getChecklistInicial();
            this.checklistFinal = form.getChecklistFinal();
        });
    }


    public void adicionarPassageiro(AlunoId alunoId) {
        boolean jaExiste = passageiros.stream()
                .anyMatch(p -> p.equals(alunoId));

        if (jaExiste) {
            throw new IllegalArgumentException("Aluno já está na viagem");
        }

        passageiros.add(alunoId);
    }

    public void adicionarPassageiros(Set<AlunoId> alunosIds) {
        alunosIds.forEach(this::adicionarPassageiro);
    }

    public void removerPassageiro(AlunoId alunoId) {
        AlunoId passageiro = buscarPassageiro(alunoId);

        passageiros.remove(passageiro);
    }

    private AlunoId buscarPassageiro(AlunoId alunoId) {
        return passageiros.stream()
                .filter(p -> p.equals(alunoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aluno não está na viagem"));
    }

    public void iniciar() {
        if (status != Status.PENDENTE) {
            throw new NaoPossivelIniciarViagemQueNaoSejaPendente();
        }
        if (motorista == null || veiculo == null) {
            throw new ViagemIncompleta();
        }
        this.periodoReal = PeriodoReal.of(periodoPlanejado.getPartida());
        this.status = Status.INICIADA;
        this.registerEvent(ViagemIniciada.from(this));
    }

    public void finalizar() {
        this.periodoReal.atualizarChegada(periodoPlanejado.getChegada());
        this.status = Status.FINALIZADA;
        this.registerEvent(ViagemFinalizada.from(this));
    }

    public void cancelar() {
        if (status != Status.PENDENTE) {
            throw new NaoPossivelIniciarViagemQueNaoSejaPendente();
        }
        if (motorista == null || veiculo == null) {
            throw new ViagemIncompleta();
        }
        this.status = Status.CANCELADA;
        this.registerEvent(ViagemCancelada.from(this));
    }

}
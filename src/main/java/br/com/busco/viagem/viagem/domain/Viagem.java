package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.viagem.domain.events.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Table
@Entity
@Getter
@EqualsAndHashCode(of = {"rota", "periodoPlanejado"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public final class Viagem extends AbstractAggregateRoot<ViagemId> {

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
    private Set<Passageiro> passageiros = new HashSet<>();

    @Builder
    private Viagem(Rota rota, PeriodoPlanejado periodoPlanejado, MotoristaId motorista, MonitorId monitor, VeiculoId veiculo) {
        super(ViagemId.randomId());
        this.rota = rota;
        this.periodoPlanejado = periodoPlanejado;
        this.status = Status.PENDENTE;
        this.motorista = motorista;
        this.monitor = monitor;
        this.veiculo = veiculo;
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

    public void adicionarPassageiro(AlunoId alunoId) {
        boolean jaExiste = passageiros.stream()
                .anyMatch(p -> p.getAluno().equals(alunoId));

        if (jaExiste) {
            throw new IllegalArgumentException("Aluno já está na viagem");
        }

        passageiros.add(Passageiro.of(alunoId));
    }

    public void adicionarPassageiros(Set<AlunoId> alunosIds) {
        alunosIds.forEach(this::adicionarPassageiro);
    }

    public void removerPassageiro(AlunoId alunoId) {
        Passageiro passageiro = buscarPassageiro(alunoId);

        if (passageiro.embarcou()) {
            throw new IllegalStateException("Não é possível remover passageiro já embarcado");
        }

        passageiros.remove(passageiro);
    }

    public void embarcarPassageiro(AlunoId alunoId, LocalDateTime horario) {
        Passageiro passageiro = buscarPassageiro(alunoId);
        passageiro.embarcar(horario);
        registerEvent(PassageiroEmbarcado.from(this));
    }

    public void marcarFalta(AlunoId alunoId) {
        Passageiro passageiro = buscarPassageiro(alunoId);
        passageiro.faltou();
        this.registerEvent(PassageiroFaltouSemJustificativa.from(this));
    }

    public void marcarAusenciaJustificada(AlunoId alunoId, String motivo) {
        Passageiro passageiro = buscarPassageiro(alunoId);
        passageiro.ausenteJustificado(motivo);
        this.registerEvent(PassageiroAusenciaRegistrada.from(this));
    }

    private Passageiro buscarPassageiro(AlunoId alunoId) {
        return passageiros.stream()
                .filter(p -> p.getAluno().equals(alunoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aluno não está na viagem"));
    }

    public void iniciar() {
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
        this.status = Status.CANCELADA;
        this.registerEvent(ViagemCancelada.from(this));
    }

}
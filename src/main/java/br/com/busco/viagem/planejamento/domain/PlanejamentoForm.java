package br.com.busco.viagem.planejamento.domain;

import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Rota;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public final class PlanejamentoForm {

    private final Consumer<PlanejamentoForm> action;
    private LocalDateTime data;
    private Rota rota;
    private MotoristaId motorista;
    private MonitorId monitor;
    private VeiculoId veiculo;
    private Set<AlunoId> passageiros = new HashSet<>();
    private GrupoChecklistId checklistInicial;
    private GrupoChecklistId checklistFinal;
    private Set<CalendarioId> calendarios = new HashSet<>();

    public PlanejamentoForm data(LocalDateTime data) {
        this.data = data;
        return this;
    }

    public PlanejamentoForm rota(Rota rota) {
        this.rota = rota;
        return this;
    }

    public PlanejamentoForm motorista(MotoristaId motorista) {
        this.motorista = motorista;
        return this;
    }

    public PlanejamentoForm monitor(MonitorId monitor) {
        this.monitor = monitor;
        return this;
    }

    public PlanejamentoForm veiculo(VeiculoId veiculo) {
        this.veiculo = veiculo;
        return this;
    }

    public PlanejamentoForm passageiros(Set<AlunoId> passageiros) {
        this.passageiros = passageiros;
        return this;
    }

    public PlanejamentoForm checklistInicial(GrupoChecklistId checklistInicial) {
        this.checklistInicial = checklistInicial;
        return this;
    }

    public PlanejamentoForm checklistFinal(GrupoChecklistId checklistFinal) {
        this.checklistFinal = checklistFinal;
        return this;
    }

    public PlanejamentoForm calendarios(Set<CalendarioId> calendarios) {
        this.calendarios = calendarios;
        return this;
    }

    public void aplicar() {
        action.accept(this);
    }
}

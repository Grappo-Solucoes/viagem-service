package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ids.*;
import br.com.busco.viagem.sk.vo.Rota;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public final class ViagemForm {

    private final Consumer<ViagemForm> action;
    private Rota rota;
    private PeriodoPlanejado periodoPlanejado;
    private MotoristaId motorista;
    private MonitorId monitor;
    private VeiculoId veiculo;
    private Set<AlunoId> passageiros = new HashSet<>();
    private GrupoChecklistId checklistInicial;
    private GrupoChecklistId checklistFinal;

    public ViagemForm rota(Rota rota) {
        this.rota = rota;
        return this;
    }

    public ViagemForm periodoPlanejado(PeriodoPlanejado periodoPlanejado) {
        this.periodoPlanejado = periodoPlanejado;
        return this;
    }

    public ViagemForm motorista(MotoristaId motorista) {
        this.motorista = motorista;
        return this;
    }

    public ViagemForm monitor(MonitorId monitor) {
        this.monitor = monitor;
        return this;
    }

    public ViagemForm veiculo(VeiculoId veiculo) {
        this.veiculo = veiculo;
        return this;
    }

    public ViagemForm passageiros(Set<AlunoId> passageiros) {
        this.passageiros = passageiros;
        return this;
    }

    public ViagemForm checklistInicial(GrupoChecklistId checklistInicial) {
        this.checklistInicial = checklistInicial;
        return this;
    }

    public ViagemForm checklistFinal(GrupoChecklistId checklistFinal) {
        this.checklistFinal = checklistFinal;
        return this;
    }

    public void aplicar() {
        action.accept(this);
    }
}
package br.com.busco.viagem.viagem.app;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.sk.ids.*;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"inicio", "fim"})
@NoArgsConstructor(access = PUBLIC, force = true)
public class DadosPlanejamentoAprovado implements ValueObject {

    private UUID rota;
    private List<AlunoId> alunos;
    private MotoristaId motorista;
    private MonitorId monitor;
    private VeiculoId veiculo;
    private GrupoChecklistId grupoChecklistInicial;
    private GrupoChecklistId grupoChecklistFinal;
    private PlanejamentoId planejamento;

    @Builder
    private DadosPlanejamentoAprovado(UUID rota, List<AlunoId> alunos, MotoristaId motorista, MonitorId monitor, VeiculoId veiculo, GrupoChecklistId grupoChecklistInicial, GrupoChecklistId grupoChecklistFinal, PlanejamentoId planejamento) {
        this.rota = rota;
        this.alunos = alunos;
        this.motorista = motorista;
        this.monitor = monitor;
        this.veiculo = veiculo;
        this.grupoChecklistInicial = grupoChecklistInicial;
        this.grupoChecklistFinal = grupoChecklistFinal;
        this.planejamento = planejamento;
    }
}

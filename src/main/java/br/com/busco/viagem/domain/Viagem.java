package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.events.*;
import br.com.busco.viagem.infra.DurationConverter;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.RotaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import br.com.busco.viagem.sk.ids.ViagemPlanejadaId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.busco.viagem.sk.ids.ViagemId.randomId;

@Entity
@Table(name = "viagens_execucao")
@Getter
@EqualsAndHashCode(of = {"viagemPlanejada"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Viagem extends AbstractAggregateRoot<ViagemId> {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "viagem_planejada_id", nullable = false))
    private ViagemPlanejadaId viagemPlanejada;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "rota_id", nullable = false))
    private RotaId rota;

    @Embedded
    private PeriodoViagem periodoViagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "viagem", orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ParadaExecucao> paradas;

    @Column(nullable = false)
    private LocalDate dataViagem;

    @Convert(converter = DurationConverter.class)
    private Duration atrasoAcumulado;

    @Builder
    private Viagem(ViagemPlanejadaId viagemPlanejada,
                   RotaId rota,
                   PeriodoViagem periodoViagem,
                   LocalDate dataViagem) {
        super(randomId());
        this.viagemPlanejada = viagemPlanejada;
        this.rota = rota;
        this.periodoViagem = periodoViagem;
        this.status = Status.PENDENTE;
        this.dataViagem = dataViagem;
        this.paradas = new ArrayList<>();
        this.atrasoAcumulado = Duration.ZERO;

        registerEvent(ViagemCriada.from(this));
    }

    public void iniciar(LocalDateTime horarioReal, List<ParadaPrevista> paradasPrevistas) {
        if (status != Status.PENDENTE) {
            throw new IllegalStateException("Viagem já foi iniciada");
        }
        if (LocalDate.now().isAfter(dataViagem)) {
            throw new IllegalStateException("Viagem não pode ser iniciada após a data");
        }

        this.status = Status.EM_ANDAMENTO;
        this.periodoViagem = periodoViagem.withInicioReal(horarioReal);

        this.paradas = paradasPrevistas.stream()
                .map(pp -> ParadaExecucao.builder()
                        .viagem(this)
                        .ordem(pp.getOrdem())
                        .horarioPrevisto(pp.getHorarioPrevisto())
                        .build())
                .collect(Collectors.toList());

        registerEvent(ViagemIniciada.from(this, horarioReal));
    }

    public void registrarChegadaParada(int ordem, LocalDateTime horarioChegada) {
        ParadaExecucao parada = getParadaPorOrdem(ordem);
        parada.registrarChegada(horarioChegada, ordem);

        if (isUltimaParada(ordem)) {
            this.status = Status.PENDENTE;
        }

        registerEvent(ChegadaParada.from(this, ordem, horarioChegada));
    }

    public void registrarSaidaParada(int ordem, LocalDateTime horarioSaida) {
        ParadaExecucao parada = getParadaPorOrdem(ordem);
        parada.registrarSaida(horarioSaida);
        registerEvent(SaidaParada.from(this, ordem, horarioSaida));
    }

    public void finalizar(LocalDateTime horarioFinalizacao) {
        if (status != Status.EM_ANDAMENTO && status != Status.PENDENTE){
            throw new IllegalStateException("Só pode finalizar viagem em andamento");
        }

        this.status = Status.CONCLUIDA;
        this.periodoViagem = periodoViagem.withFimReal(horarioFinalizacao);
        validarTodasParadasConcluidas();

        registerEvent(ViagemFinalizada.from(this, horarioFinalizacao));
    }

    public void pausar() {
        if (status != Status.EM_ANDAMENTO) {
            throw new IllegalStateException("Só pode pausar viagem em andamento");
        }
        this.status = Status.PAUSADA;
        registerEvent(ViagemPausada.from(this));
    }

    public void retomar() {
        if (status != Status.PAUSADA) {
            throw new IllegalStateException("Só pode retomar viagem pausada");
        }
        this.status = Status.EM_ANDAMENTO;
        registerEvent(ViagemRetomada.from(this));
    }

    public void registrarAtrasoDetectado(Duration atraso) {
        this.atrasoAcumulado = atraso;

        if (atraso.toMinutes() > 15) {
            registerEvent(AtrasoCritico.from(this, atraso));
        }
    }

    public void cancelar() {
        this.status = Status.CANCELADA;
        registerEvent(ViagemCancelada.from(this));
    }


    private void detectarDesvioSequencia(ParadaExecucao parada) {

        boolean existeAnteriorPendente =
                paradas.stream()
                        .anyMatch(p ->
                                p.getOrdem() < parada.getOrdem()
                                        && p.isPendente()
                        );

        if (existeAnteriorPendente) {
            registerEvent(ParadaForaDeSequenciaEvent.from(this, parada));
        }
    }


    private void validarTodasParadasConcluidas() {
        boolean todasConcluidas = paradas.stream()
                .allMatch(p -> p.getStatus() == StatusParada.CONCLUIDA);

        if (!todasConcluidas) {
            registerEvent(ParadasPendentes.from(this, paradas.stream()
                    .filter(p -> p.getStatus() != StatusParada.CONCLUIDA)
                    .map(ParadaExecucao::getOrdem)
                    .collect(Collectors.toList())));
        }
    }

    private ParadaExecucao getParadaPorOrdem(int ordem) {
        return paradas.stream()
                .filter(p -> p.getOrdem() == ordem)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parada " + ordem + " não encontrada"));
    }

    private boolean isUltimaParada(int ordem) {
        return paradas.stream()
                .mapToInt(ParadaExecucao::getOrdem)
                .max()
                .orElse(0) == ordem;
    }
    public boolean isEmAndamento() {
        return status == Status.EM_ANDAMENTO;
    }

    public boolean isAtrasada() {
        return atrasoAcumulado != null && atrasoAcumulado.toMinutes() > 5;
    }
}

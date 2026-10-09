package br.com.busco.viagem.domain;

import br.com.busco.viagem.domain.events.AlunoEmbarcado;
import br.com.busco.viagem.domain.events.AtrasoCritico;
import br.com.busco.viagem.domain.events.FaltaSemJustificativa;
import br.com.busco.viagem.domain.events.MonitorAtribuido;
import br.com.busco.viagem.domain.events.MonitorRemovido;
import br.com.busco.viagem.domain.events.ChegadaParada;
import br.com.busco.viagem.domain.events.ParadaForaDeSequenciaEvent;
import br.com.busco.viagem.domain.events.ParadasPendentes;
import br.com.busco.viagem.domain.events.SaidaParada;
import br.com.busco.viagem.domain.events.ViagemCancelada;
import br.com.busco.viagem.domain.events.ViagemCriada;
import br.com.busco.viagem.domain.events.ViagemFinalizada;
import br.com.busco.viagem.domain.events.ViagemIniciada;
import br.com.busco.viagem.domain.events.ViagemPausada;
import br.com.busco.viagem.domain.events.ViagemRetomada;
import br.com.busco.viagem.infra.DurationConverter;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.RotaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.busco.viagem.sk.ids.ViagemId.randomId;

/**
 * Agregado de execução de viagem. Referencia rota, motorista, monitor,
 * veículo e planejamento só por id (sem entidades JPA cruzadas, sem FK
 * externa); passageiros esperados são {@link Passageiro} (snapshot) e os
 * embarques são {@link EmbarqueRegistro} embutidos. Sem listas de
 * ausência/ocorrência/métrica (outros contextos).
 */
@Entity
@Table(name = "viagens_execucao")
@Getter
@EqualsAndHashCode(of = {"rota", "data"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Viagem extends AbstractAggregateRoot<ViagemId> {

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(name = "codigo_viagem")
    private String codigoViagem;

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "rota_id", nullable = false))
    private RotaId rota;

    @Column(name = "motorista_id", nullable = false)
    private UUID motoristaId;

    @Column(name = "monitor_id", nullable = false)
    private UUID monitorId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "planejamento_id")
    private UUID planejamentoId;

    @ElementCollection
    @CollectionTable(name = "viagem_passageiros", joinColumns = @JoinColumn(name = "viagem_id"))
    private List<Passageiro> passageiros = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "viagem_embarques", joinColumns = @JoinColumn(name = "viagem_id"))
    private List<EmbarqueRegistro> embarques = new ArrayList<>();

    @Embedded
    private PeriodoViagem periodoViagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "viagem", orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ParadaExecucao> paradas = new ArrayList<>();

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Convert(converter = DurationConverter.class)
    private Duration atrasoAcumulado;

    @Builder
    private Viagem(LocalDateTime data, RotaId rota, UUID motoristaId, UUID monitorId,
                   UUID veiculoId, UUID planejamentoId, List<Passageiro> passageiros,
                   PeriodoViagem periodoViagem, String codigoViagem) {
        super(randomId());
        this.data = Objects.requireNonNull(data, "A data da viagem é obrigatória");
        this.rota = Objects.requireNonNull(rota, "A rota da viagem é obrigatória");
        this.motoristaId = Objects.requireNonNull(motoristaId, "O motorista da viagem é obrigatório");
        this.monitorId = Objects.requireNonNull(monitorId, "O monitor da viagem é obrigatório");
        this.veiculoId = Objects.requireNonNull(veiculoId, "O veículo da viagem é obrigatório");
        this.planejamentoId = planejamentoId;
        this.passageiros = passageiros != null ? new ArrayList<>(passageiros) : new ArrayList<>();
        this.embarques = new ArrayList<>();
        this.periodoViagem = periodoViagem;
        this.codigoViagem = codigoViagem;
        this.status = Status.PLANEJADA;
        this.paradas = new ArrayList<>();
        this.atrasoAcumulado = Duration.ZERO;

        registerEvent(ViagemCriada.from(this));
    }

    // ------------------------------------------------------------------
    // Ciclo de vida
    // ------------------------------------------------------------------

    public void iniciar(LocalDateTime horarioReal, List<ParadaPrevista> paradasPrevistas) {
        this.status = status.iniciar();
        if (LocalDate.now().isAfter(data.toLocalDate())) {
            throw new IllegalStateException("Viagem não pode ser iniciada após a data");
        }
        this.dataInicio = horarioReal;
        if (periodoViagem != null) {
            this.periodoViagem = periodoViagem.withInicioReal(horarioReal);
        }

        this.paradas = paradasPrevistas.stream()
                .map(pp -> ParadaExecucao.builder()
                        .viagem(this)
                        .ordem(pp.getOrdem())
                        .horarioPrevisto(pp.getHorarioPrevisto())
                        .build())
                .collect(Collectors.toList());

        registerEvent(ViagemIniciada.from(this, horarioReal));
    }

    public void finalizar(LocalDateTime horarioFinalizacao) {
        this.status = status.finalizar();
        this.dataFim = horarioFinalizacao;
        if (periodoViagem != null) {
            this.periodoViagem = periodoViagem.withFimReal(horarioFinalizacao);
        }
        validarTodasParadasConcluidas();

        registerEvent(ViagemFinalizada.from(this, horarioFinalizacao));

        List<Passageiro> faltantes = passageirosNaoEmbarcados();
        if (!faltantes.isEmpty()) {
            registerEvent(FaltaSemJustificativa.of(this, faltantes));
        }
    }

    /**
     * Esperados que nunca embarcaram (mesmo que tenham desembarcado depois,
     * contam como presentes — regra do monólito). Sem ausência: só
     * embarcados vs esperados.
     */
    public List<Passageiro> passageirosNaoEmbarcados() {
        List<UUID> embarcaram = embarques.stream()
                .map(EmbarqueRegistro::getPassageiroId)
                .toList();
        return getPassageiros().stream()
                .filter(p -> !embarcaram.contains(p.getPassageiroId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void assumir(UUID novoMonitorId) {
        Objects.requireNonNull(novoMonitorId, "O monitor é obrigatório");
        if (status != Status.PLANEJADA) {
            throw new IllegalStateException("Somente viagens planejadas podem ser assumidas");
        }
        if (novoMonitorId.equals(this.monitorId)) {
            return;
        }
        if (this.monitorId != null) {
            registerEvent(MonitorRemovido.of(getId()));
        }
        this.monitorId = novoMonitorId;
        registerEvent(MonitorAtribuido.of(getId()));
    }

    public void cancelar() {
        this.status = status.cancelar();
        registerEvent(ViagemCancelada.from(this));
    }

    public void pausar() {
        this.status = status.pausar();
        registerEvent(ViagemPausada.from(this));
    }

    public void retomar() {
        this.status = status.retomar();
        registerEvent(ViagemRetomada.from(this));
    }

    // ------------------------------------------------------------------
    // Paradas e atraso
    // ------------------------------------------------------------------

    public void registrarChegadaParada(int ordem, LocalDateTime horarioChegada) {
        registrarChegadaParada(ordem, horarioChegada, null, null);
    }

    public void registrarChegadaParada(int ordem, LocalDateTime horarioChegada,
                                       Double latitude, Double longitude) {
        if (status != Status.INICIADA) {
            throw new IllegalStateException("Só pode registrar parada em viagem em andamento");
        }
        ParadaExecucao parada = getParadaPorOrdem(ordem);
        parada.registrarChegada(horarioChegada, ordem, latitude, longitude);
        detectarDesvioSequencia(parada);
        acumularAtraso(parada.atrasoChegada());

        registerEvent(ChegadaParada.from(this, ordem, horarioChegada));
    }

    public void registrarSaidaParada(int ordem, LocalDateTime horarioSaida) {
        registrarSaidaParada(ordem, horarioSaida, null, null);
    }

    public void registrarSaidaParada(int ordem, LocalDateTime horarioSaida,
                                     Double latitude, Double longitude) {
        if (status != Status.INICIADA) {
            throw new IllegalStateException("Só pode registrar parada em viagem em andamento");
        }
        ParadaExecucao parada = getParadaPorOrdem(ordem);
        parada.registrarSaida(horarioSaida, latitude, longitude);
        registerEvent(SaidaParada.from(this, ordem, horarioSaida));
    }

    private void acumularAtraso(Duration atraso) {
        if (atraso == null || atraso.isZero()) {
            return;
        }
        Duration acumulado = atrasoAcumulado == null ? atraso : atrasoAcumulado.plus(atraso);
        registrarAtrasoDetectado(acumulado);
    }

    public void registrarAtrasoDetectado(Duration atraso) {
        this.atrasoAcumulado = atraso;

        if (atraso.toMinutes() > 15) {
            registerEvent(AtrasoCritico.from(this, atraso));
        }
    }

    // ------------------------------------------------------------------
    // Passageiros esperados (snapshot)
    // ------------------------------------------------------------------

    /** Esperados ativos; viagem planejada filtra inativos (monólito). */
    public List<Passageiro> getPassageiros() {
        return passageiros.stream()
                .filter(p -> p.getPassageiroId() != null)
                .filter(p -> this.status != Status.PLANEJADA || p.isAtivo())
                .sorted(Comparator.comparing(Passageiro::getPassageiroId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<Passageiro> encontrarPassageiroPorId(UUID passageiroId) {
        return passageiros.stream()
                .filter(p -> passageiroId.equals(p.getPassageiroId()))
                .findFirst();
    }

    /**
     * Procura entre os esperados aquele cujos dados (lidos do texto bruto da
     * carteirinha/QR) correspondem — mesma regra do monólito: nome obrigatório,
     * turno/escola/matrícula desempatam.
     */
    public Optional<Passageiro> encontrarPassageiroPorCarteirinha(Carteirinha carteirinha) {
        return passageiros.stream()
                .filter(p -> p.nomeConfereComCarteirinha(carteirinha))
                .max(Comparator.comparingInt(p -> p.pontosQueConferemComCarteirinha(carteirinha)));
    }

    public boolean isEmAndamento() {
        return status == Status.INICIADA;
    }

    public boolean isAtrasada() {
        return atrasoAcumulado != null && atrasoAcumulado.toMinutes() > 5;
    }

    public boolean isDataOcupada(Viagem outra) {
        return this.data.equals(outra.data);
    }

    // ------------------------------------------------------------------
    // Embarque / desembarque (regras do monólito, sem Aluno entidade)
    // ------------------------------------------------------------------

    /** Passageiros com desembarque ainda não registrado (a bordo agora). */
    public List<Passageiro> getPassageirosABordo() {
        List<UUID> aBordo = embarques.stream()
                .filter(EmbarqueRegistro::estaEmbarcado)
                .map(EmbarqueRegistro::getPassageiroId)
                .toList();
        return passageiros.stream()
                .filter(p -> aBordo.contains(p.getPassageiroId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<EmbarqueRegistro> getRegistrosEmbarque() {
        return List.copyOf(embarques);
    }

    public void embarcarPassageiro(Passageiro passageiro, Double latitude, Double longitude,
                                   String justificativa) {
        Objects.requireNonNull(passageiro, "O passageiro é obrigatório");
        boolean planejado = passageiros.contains(passageiro);
        String justificativaNormalizada = justificativa == null ? null : justificativa.trim();
        if (!planejado && (justificativaNormalizada == null || justificativaNormalizada.isEmpty())) {
            throw new IllegalArgumentException(
                    "A justificativa é obrigatória para embarcar um passageiro não planejado.");
        }
        if (justificativaNormalizada != null && justificativaNormalizada.length() > 500) {
            throw new IllegalArgumentException("A justificativa deve ter no máximo 500 caracteres.");
        }
        boolean jaEmbarcado = embarques.stream()
                .anyMatch(r -> r.getPassageiroId().equals(passageiro.getPassageiroId())
                        && r.estaEmbarcado());
        if (jaEmbarcado) {
            return;
        }
        embarques.add(new EmbarqueRegistro(passageiro.getPassageiroId(), passageiro.getNome(),
                latitude, longitude, planejado, planejado ? null : justificativaNormalizada));
        registerEvent(AlunoEmbarcado.of(this, passageiro));
    }

    /** Embarca todos os esperados que ainda não embarcaram. */
    public List<Passageiro> embarcarTodosPendentes() {
        List<Passageiro> embarcados = new ArrayList<>();
        for (Passageiro passageiro : getPassageiros()) {
            boolean pendente = embarques.stream()
                    .noneMatch(r -> r.getPassageiroId().equals(passageiro.getPassageiroId())
                            && r.estaEmbarcado());
            if (pendente) {
                embarcarPassageiro(passageiro, null, null, null);
                embarcados.add(passageiro);
            }
        }
        return embarcados;
    }

    public void desembarcarPassageiro(UUID passageiroId, Double latitude, Double longitude) {
        boolean esperado = passageiros.stream()
                .anyMatch(p -> p.getPassageiroId().equals(passageiroId));
        if (!esperado) {
            throw new IllegalStateException("Passageiro não está nessa viagem");
        }
        EmbarqueRegistro ativo = embarques.stream()
                .filter(r -> r.getPassageiroId().equals(passageiroId) && r.estaEmbarcado())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Passageiro não está embarcado nessa viagem"));

        embarques.remove(ativo);
        embarques.add(ativo.comDesembarque(latitude, longitude));
    }

    // ------------------------------------------------------------------

    private void detectarDesvioSequencia(ParadaExecucao parada) {
        boolean existeAnteriorPendente = paradas.stream()
                .anyMatch(p -> p.getOrdem() < parada.getOrdem() && p.isPendente());
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
}

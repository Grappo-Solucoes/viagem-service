package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.RotaId;
import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static br.com.busco.viagem.sk.ids.RotaId.randomId;

/**
 * Rota como agregado local do contexto viagem (decisão D2): gerenciada aqui
 * (CRUD próprio) em vez de gateway externo. Sem FKs para fora do schema.
 */
@Entity
@Table(name = "rotas")
@Getter
@EqualsAndHashCode(of = {"numeroRota"}, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rota extends AbstractAggregateRoot<RotaId> {

    @ElementCollection
    @CollectionTable(name = "rota_enderecos", joinColumns = @JoinColumn(name = "rota_id"))
    private List<Endereco> enderecos = new ArrayList<>();

    private String nome;

    @Column(nullable = false)
    private String numeroRota;

    private String tempoEstimado;
    private String distancia;

    @Column(name = "rotas_json", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String rotasJson;

    @Builder
    private Rota(List<Endereco> enderecos, String numeroRota, String nome,
                 String tempoEstimado, String distancia, String rotasJson) {
        super(randomId());
        this.enderecos = enderecos != null ? new ArrayList<>(enderecos) : new ArrayList<>();
        this.numeroRota = Objects.requireNonNull(numeroRota, "O número da rota é obrigatório");
        this.nome = nome;
        this.tempoEstimado = tempoEstimado;
        this.distancia = distancia;
        this.rotasJson = rotasJson;
    }

    public void atualizar(String numeroRota, String nome, List<Endereco> enderecos,
                          String tempoEstimado, String distancia, String rotasJson) {
        if (numeroRota != null) {
            this.numeroRota = numeroRota;
        }
        if (nome != null) {
            this.nome = nome;
        }
        if (enderecos != null) {
            this.enderecos = new ArrayList<>(enderecos);
        }
        if (tempoEstimado != null) {
            this.tempoEstimado = tempoEstimado;
        }
        if (distancia != null) {
            this.distancia = distancia;
        }
        if (rotasJson != null) {
            this.rotasJson = rotasJson;
        }
    }

    public Endereco getEnderecoInicial() {
        if (enderecos != null && !enderecos.isEmpty()) {
            return enderecos.get(0);
        }
        return null;
    }

    public Endereco getEnderecoFinal() {
        if (enderecos != null && !enderecos.isEmpty()) {
            return enderecos.get(enderecos.size() - 1);
        }
        return null;
    }
}

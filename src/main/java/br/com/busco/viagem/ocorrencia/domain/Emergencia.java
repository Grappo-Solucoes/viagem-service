package br.com.busco.viagem.ocorrencia.domain;

import br.com.busco.viagem.ocorrencia.domain.events.EmergenciaRealizada;
import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;
import br.com.busco.viagem.sk.ids.EmergenciaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Emergencia extends AbstractAggregateRoot<EmergenciaId> {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "viagem_id", nullable = false))
    @NonNull
    private ViagemId viagem;

    @NonNull
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emergencia", nullable = false)
    @NonNull
    private TipoEmergencia tipoEmergencia;

    @Column(name = "user_id", nullable = false)
    @NonNull
    private UUID userId;

    private LocalDateTime data;

    @Builder
    private Emergencia(@NonNull ViagemId viagem,
                       @NonNull String motivo,
                       @NonNull TipoEmergencia tipoEmergencia,
                       @NonNull UUID userId) {
        super(EmergenciaId.randomId());
        this.viagem = viagem;
        this.motivo = motivo;
        this.tipoEmergencia = tipoEmergencia;
        this.userId = userId;
        this.data = LocalDateTime.now();
    }

    public static class EmergenciaBuilder {
        public Emergencia build() {
            Emergencia emergencia = new Emergencia(this.viagem, this.motivo, this.tipoEmergencia, this.userId);
            emergencia.registerEvent(EmergenciaRealizada.from(emergencia));
            return emergencia;
        }
    }
}

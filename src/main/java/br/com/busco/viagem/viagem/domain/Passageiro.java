package br.com.busco.viagem.viagem.domain;

import br.com.busco.viagem.sk.ddd.ValueObject;
import br.com.busco.viagem.sk.ids.AlunoId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Getter
@Embeddable
@EqualsAndHashCode(of = {"aluno"})
@NoArgsConstructor(access = PUBLIC, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Passageiro implements ValueObject {

    @Embedded
    @AttributeOverride(name = "uuid", column = @Column(name = "aluno_id"))
    private AlunoId aluno;

    @Enumerated(EnumType.STRING)
    private StatusPassageiro status;
    private LocalDateTime horarioEmbarque;
    private String motivoAusencia;

    private Passageiro(AlunoId alunoId) {
        this.aluno = Objects.requireNonNull(alunoId, "AlunoId não pode ser nulo");
        this.status = StatusPassageiro.AGUARDANDO;
    }

    public Passageiro embarcar(LocalDateTime horario) {
        validarHorario(horario);

        if (this.status == StatusPassageiro.EMBARCADO) {
            throw new IllegalStateException("Aluno já embarcado");
        }

        if (this.status == StatusPassageiro.FALTOU || this.status == StatusPassageiro.CANCELADO) {
            throw new IllegalStateException("Não é possível embarcar aluno com status: " + this.status);
        }

        this.status = StatusPassageiro.EMBARCADO;
        this.horarioEmbarque = horario;
        this.motivoAusencia = null;
        return this;
    }

    public Passageiro faltou() {
        validarTransicaoParaAusente();
        this.status = StatusPassageiro.FALTOU;
        this.motivoAusencia = "Falta não justificada";
        return this;
    }

    public Passageiro ausenteJustificado(String motivo) {
        Objects.requireNonNull(motivo, "Motivo da ausência é obrigatório");
        validarTransicaoParaAusente();

        this.status = StatusPassageiro.AUSENTE_JUSTIFICADO;
        this.motivoAusencia = motivo;
        return this;
    }

    public Passageiro cancelar(String motivo) {
        if (this.status == StatusPassageiro.EMBARCADO) {
            throw new IllegalStateException("Não é possível cancelar aluno já embarcado");
        }

        this.status = StatusPassageiro.CANCELADO;
        this.motivoAusencia = motivo;
        return this;
    }

    private void validarTransicaoParaAusente() {
        if (this.status == StatusPassageiro.EMBARCADO) {
            throw new IllegalStateException("Não é possível marcar como ausente aluno já embarcado");
        }

        if (this.status == StatusPassageiro.CANCELADO) {
            throw new IllegalStateException("Aluno já cancelado");
        }
    }

    private void validarHorario(LocalDateTime horario) {
        Objects.requireNonNull(horario, "Horário de embarque" + " não pode ser nulo");

        if (horario.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário de embarque" + " não pode ser no futuro");
        }
    }

    public boolean embarcou() {
        return status == StatusPassageiro.EMBARCADO;
    }

    public boolean faltouOuAusente() {
        return status == StatusPassageiro.FALTOU ||
                status == StatusPassageiro.AUSENTE_JUSTIFICADO;
    }


    public static Passageiro of(AlunoId alunoId) {
        return new Passageiro(alunoId);
    }

}

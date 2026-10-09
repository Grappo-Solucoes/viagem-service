package br.com.busco.viagem.domain;

/**
 * Estados da viagem, alinhados ao monólito ({@code PLANEJADA → INICIADA → FINALIZADA/CANCELADA}),
 * com {@code PAUSADA} como extensão do ciclo de execução (pausar/retomar).
 */
public enum Status {
    PLANEJADA,
    INICIADA,
    PAUSADA,
    FINALIZADA,
    CANCELADA;

    public Status iniciar() {
        if (this == PLANEJADA) {
            return INICIADA;
        }
        throw new IllegalStateException("Somente viagens planejadas podem ser iniciadas");
    }

    public Status finalizar() {
        if (this == INICIADA || this == PAUSADA) {
            return FINALIZADA;
        }
        throw new IllegalStateException("Somente viagens iniciadas podem ser finalizadas");
    }

    public Status cancelar() {
        if (this == PLANEJADA) {
            return CANCELADA;
        }
        throw new IllegalStateException("Somente viagens planejadas podem ser canceladas");
    }

    public Status pausar() {
        if (this == INICIADA) {
            return PAUSADA;
        }
        throw new IllegalStateException("Só pode pausar viagem em andamento");
    }

    public Status retomar() {
        if (this == PAUSADA) {
            return INICIADA;
        }
        throw new IllegalStateException("Só pode retomar viagem pausada");
    }

    public boolean podeAtualizarPosicao() {
        return this == INICIADA || this == PAUSADA;
    }

    public boolean podeRegistrarParada() {
        return this == INICIADA;
    }
}

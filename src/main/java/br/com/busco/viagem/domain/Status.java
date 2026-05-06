package br.com.busco.viagem.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {
    PENDENTE("Aguardando início"),
    EM_ANDAMENTO("Em curso"),
    PAUSADA("Pausada"),
    CONCLUIDA("Finalizada"),
    CANCELADA("Cancelada");

    private String descricao;

    public boolean podeAtualizarPosicao() {
        return this == EM_ANDAMENTO || this == PAUSADA;
    }

    public boolean podeRegistrarParada() {
        return this == EM_ANDAMENTO;
    }
}

package br.com.busco.viagem.ocorrencia.domain;

import lombok.Getter;

@Getter
public enum StatusOcorrencia {
    PENDENTE("PENDENTE"),
    EM_ANDAMENTO("EM_ANDAMENTO"),
    TRATATIVAS("TRATATIVAS"),
    FINALIZADA("FINALIZADA");

    private final String valor;

    StatusOcorrencia(String valor) {
        this.valor = valor;
    }

    public static StatusOcorrencia fromString(String status) {
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status de Ocorrencia invalido: " + status);
        }
    }
}

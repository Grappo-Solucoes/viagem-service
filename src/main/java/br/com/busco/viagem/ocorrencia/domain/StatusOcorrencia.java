package br.com.busco.viagem.ocorrencia.domain;

import lombok.Getter;

@Getter
public enum StatusOcorrencia {
    PENDENTE,
    EM_ANDAMENTO,
    TRATATIVAS,
    FINALIZADA;
}

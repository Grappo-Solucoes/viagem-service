package br.com.busco.viagem.domain;

public enum StatusParada {
    PENDENTE,
    APROXIMANDO,
    CHEGADA_REGISTRADA,
    CONCLUIDA,
    PULADA;  // Se o ônibus passou direto (aluno faltou)
}

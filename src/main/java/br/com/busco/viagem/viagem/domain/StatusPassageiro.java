package br.com.busco.viagem.viagem.domain;

public enum StatusPassageiro {
    AGUARDANDO, // Ainda não processado
    EMBARCADO,
    FALTOU,
    AUSENTE_JUSTIFICADO,
    CANCELADO,
    TRANSFERIDO
}
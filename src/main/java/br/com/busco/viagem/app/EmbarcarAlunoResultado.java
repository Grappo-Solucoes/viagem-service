package br.com.busco.viagem.app;

import java.util.UUID;

public record EmbarcarAlunoResultado(UUID viagemId, UUID passageiroId, String nome) {
}

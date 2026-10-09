package br.com.busco.viagem.app;

import java.util.List;
import java.util.UUID;

public record EmbarcarTodosResultado(UUID viagemId, List<EmbarcarAlunoResultado> embarcados) {
}

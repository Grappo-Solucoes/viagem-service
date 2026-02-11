package br.com.busco.viagem.planejamento.domain.exceptions;

public final class NaoPossivelFinalizarPlanejamentoQueNaoSejaPlanejado extends IllegalStateException {
    public NaoPossivelFinalizarPlanejamentoQueNaoSejaPlanejado() {
        super("Planejamento não pode ser finalizado por nao estar planejado");
    }

}

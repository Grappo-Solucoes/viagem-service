package br.com.busco.viagem.planejamento.domain.exceptions;

public final class NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado extends IllegalStateException {
    public NaoPossivelIniciarPlanejamentoQueNaoSejaPlanejado() {
        super("Planejamento não pode ser inciado por nao estar planejado");
    }

}

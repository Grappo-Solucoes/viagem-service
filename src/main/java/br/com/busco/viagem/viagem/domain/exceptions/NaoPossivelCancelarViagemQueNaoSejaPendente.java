package br.com.busco.viagem.viagem.domain.exceptions;

public final class NaoPossivelCancelarViagemQueNaoSejaPendente extends IllegalStateException {
    public NaoPossivelCancelarViagemQueNaoSejaPendente() {
        super("Viagem não pode ser cancelada por nao estar pendente");
    }

}

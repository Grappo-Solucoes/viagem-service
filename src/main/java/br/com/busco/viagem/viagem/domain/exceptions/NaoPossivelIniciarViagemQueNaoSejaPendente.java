package br.com.busco.viagem.viagem.domain.exceptions;

public final class NaoPossivelIniciarViagemQueNaoSejaPendente extends IllegalStateException {
    public NaoPossivelIniciarViagemQueNaoSejaPendente() {
        super("Viagem não pode ser iniciada por nao estar pendente");
    }

}

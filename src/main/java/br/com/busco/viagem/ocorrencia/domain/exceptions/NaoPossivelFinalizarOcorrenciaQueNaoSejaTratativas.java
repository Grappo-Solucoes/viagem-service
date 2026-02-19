package br.com.busco.viagem.ocorrencia.domain.exceptions;

public final class NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas extends IllegalStateException {
    public NaoPossivelFinalizarOcorrenciaQueNaoSejaTratativas() {
        super("A ocorrencia so pode ser finalizada a partir do status TRATATIVAS.");
    }
}

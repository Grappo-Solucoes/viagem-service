package br.com.busco.viagem.ocorrencia.domain.exceptions;

public final class NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento extends IllegalStateException {
    public NaoPossivelIniciarTratativasOcorrenciaQueNaoSejaEmAndamento() {
        super("As tratativas so podem ser iniciadas a partir do status EM_ANDAMENTO.");
    }
}
